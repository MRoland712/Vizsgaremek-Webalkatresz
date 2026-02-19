import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { GetAllStatsService } from '../../services/getallstats.service';
import { GetAllStatsModel } from '../../models/getallstats.model';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css',
})
export class AnalyticsComponent implements OnInit {
  private statsService = inject(GetAllStatsService);

  // ── UI állapot ───────────────────────────────────────────────
  isLoading = true;
  hasError = false;
  lastUpdated = new Date();

  // ── Stat cards ───────────────────────────────────────────────
  pageViews = 0;
  uniqueVisitors = 0;
  totalUsers = 0;
  activeUsersCount = 0;

  // ── Aktív felhasználók sparkline ─────────────────────────────
  sparkHeights: number[] = [30, 45, 35, 60, 55, 75, 70, 85];

  // ── Rendelések / Szállítás ───────────────────────────────────
  ordersCount = 0;
  deliveredCount = 0;
  pendingCount = 0;
  shippedCount = 0;

  // ── Top termékek ─────────────────────────────────────────────
  topProducts: { partName: string; quantity: number }[] = [];

  // ── Aktív felhasználók lista ─────────────────────────────────
  activeUsers: {
    firstName: string;
    lastName: string;
    email: string;
    lastLogin: string;
    role: string;
  }[] = [];

  // ── Időszűrő ────────────────────────────────────────────────
  activeFilter = '365';
  filters = [
    { label: '7 nap', value: '7' },
    { label: '30 nap', value: '30' },
    { label: '1 év', value: '365' },
  ];

  ngOnInit(): void {
    this.loadStats(this.activeFilter);
  }

  setFilter(value: string): void {
    this.activeFilter = value;
    this.loadStats(value);
  }

  loadStats(days: string): void {
    this.isLoading = true;
    this.hasError = false;

    this.statsService.getAllStats(days).subscribe({
      next: (res) => {
        if (res.statusCode === 200 && res.result) {
          this.mapData(res.result);
          this.lastUpdated = new Date();
        } else {
          this.hasError = true;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Analytics betöltési hiba:', err);
        this.hasError = true;
        this.isLoading = false;
      },
    });
  }

  private mapData(data: GetAllStatsModel): void {
    // Stat cards
    this.pageViews = data.pageViews ?? 0;
    this.uniqueVisitors = data.uniqueVisitors ?? 0;
    this.totalUsers = data.allRegisteredUserCount ?? 0;
    this.activeUsersCount = data.activeUsers?.length ?? 0;
    this.ordersCount = data.ordersCount ?? 0;
    this.deliveredCount = data.allDeliveredOrders?.length ?? 0;

    // Rendelés státuszok
    if (data.allOrders) {
      this.pendingCount = data.allOrders.filter((o) => o.status === 'pending').length;
      this.shippedCount = data.allOrders.filter((o) => o.status === 'shipped').length;
    }

    // Top termékek (max 5, rendezve)
    this.topProducts = [...(data.mostPurchasedPart ?? [])]
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 5);

    // Aktív felhasználók
    this.activeUsers = (data.activeUsers ?? []).slice(0, 8).map((u) => ({
      firstName: u.firstName,
      lastName: u.lastName,
      email: u.email,
      lastLogin: u.lastLogin,
      role: u.role,
    }));

    // Sparkline frissítés aktív user szám alapján
    this.generateSparkline();
  }

  private generateSparkline(): void {
    // 8 véletlenszerű magasság az aktuális activeUsersCount körül
    const base = Math.max(this.activeUsersCount, 1);
    this.sparkHeights = Array.from({ length: 8 }, (_, i) => {
      const variation = (Math.sin(i * 1.3) + 1) * 0.5;
      return Math.round(20 + variation * 75);
    });
  }

  getTopBarWidth(quantity: number): number {
    if (!this.topProducts.length) return 0;
    return Math.round((quantity / this.topProducts[0].quantity) * 100);
  }

  getInitials(firstName: string, lastName: string): string {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  }
}
