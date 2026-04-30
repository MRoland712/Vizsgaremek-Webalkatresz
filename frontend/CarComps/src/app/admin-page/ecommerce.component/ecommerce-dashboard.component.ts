import { Component, OnInit, OnDestroy, inject, ViewChild, ElementRef } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { GetAllStatsService } from '../../services/getallstats.service';
import { GetAllStatsModel } from '../../models/getallstats.model';
import { Chart, registerables } from 'chart.js';
import { Router } from '@angular/router';

Chart.register(...registerables);

interface TopProduct {
  name: string;
  count: number;
}

interface TooltipData {
  month: string;
  value: number;
}

const MONTH_NAMES: string[] = [
  'Január',
  'Február',
  'Március',
  'Április',
  'Május',
  'Június',
  'Július',
  'Augusztus',
  'Szeptember',
  'Október',
  'November',
  'December',
];
const MONTH_SHORT: string[] = [
  'Jan',
  'Feb',
  'Már',
  'Ápr',
  'Máj',
  'Jún',
  'Júl',
  'Aug',
  'Szep',
  'Okt',
  'Nov',
  'Dec',
];

@Component({
  selector: 'app-ecommerce-dashboard',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './ecommerce-dashboard.component.html',
  styleUrl: './ecommerce-dashboard.component.css',
})
export class EcommerceDashboardComponent implements OnInit, OnDestroy {
  private statsService = inject(GetAllStatsService);
  private router = inject(Router);

  @ViewChild('monthlyChart') chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  // ── UI állapot ───────────────────────────────────────────────
  isLoading = true;
  hasError = false;
  lastUpdated = new Date();
  currentYear = new Date().getFullYear();

  // ── Customers ───────────────────────────────────────────────
  totalCustomers = 0;
  activeCustomers = 0;
  customerRetentionRate = 0;

  // ── Orders ──────────────────────────────────────────────────
  totalOrders = 0;
  pendingOrders = 0;
  shippedOrders = 0;
  deliveredOrders = 0;

  // ── Chart adatok ─────────────────────────────────────────────
  private monthlyCounts: number[] = new Array(12).fill(0);

  // ── Top Products ─────────────────────────────────────────────
  topProducts: TopProduct[] = [];

  ngOnInit(): void {
    this.loadStats('1');
  }

  ngOnDestroy(): void {
    this.chartInstance?.destroy();
  }

  loadStats(days: string): void {
    this.isLoading = true;
    this.hasError = false;

    if (!this.statsService['authService']?.isAdmin?.()) {
      console.warn('⛔ E-commerce dashboard: nem admin felhasználó.');
      this.isLoading = false;
      this.hasError = true;
      return;
    }

    this.statsService.getAllStats(days).subscribe({
      next: (response) => {
        if (response.statusCode === 200 && response.result) {
          this.mapApiData(response.result);
          this.lastUpdated = new Date();
        } else {
          this.hasError = true;
        }
        this.isLoading = false;
        setTimeout(() => this.initChart(), 50);
      },
      error: (err) => {
        console.error('Hiba az adatok betöltésekor:', err);
        this.hasError = true;
        this.isLoading = false;
      },
    });
  }

  private mapApiData(data: GetAllStatsModel): void {
    // Customers
    this.totalCustomers = data.allRegisteredUserCount ?? 0;
    this.activeCustomers = Array.isArray(data.activeUsers) ? data.activeUsers.length : 0;
    this.customerRetentionRate =
      this.totalCustomers > 0 ? Math.round((this.activeCustomers / this.totalCustomers) * 100) : 0;

    // Orders
    this.totalOrders = data.ordersCount ?? 0;
    if (Array.isArray(data.allOrders)) {
      this.pendingOrders = data.allOrders.filter((o) => o.status === 'pending').length;
      this.shippedOrders = data.allOrders.filter((o) => o.status === 'shipped').length;
    }
    this.deliveredOrders = Array.isArray(data.allDeliveredOrders)
      ? data.allDeliveredOrders.length
      : 0;

    // Havi bontás
    this.monthlyCounts = new Array(12).fill(0);
    if (Array.isArray(data.allOrders)) {
      data.allOrders.forEach((order) => {
        if (order.createdAt) {
          const idx = new Date(order.createdAt).getMonth();
          if (idx >= 0 && idx < 12) this.monthlyCounts[idx]++;
        }
      });
    }

    // Top Products
    if (Array.isArray(data.mostPurchasedPart)) {
      this.topProducts = [...data.mostPurchasedPart]
        .sort((a, b) => b.quantity - a.quantity)
        .slice(0, 5)
        .map((p) => ({ name: p.partName, count: p.quantity }));
    }
  }

  private initChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;

    this.chartInstance?.destroy();

    this.chartInstance = new Chart(canvas, {
      type: 'line',
      data: {
        labels: MONTH_SHORT,
        datasets: [
          {
            label: 'Rendelések',
            data: this.monthlyCounts,
            borderColor: '#ff6600',
            backgroundColor: 'rgba(255, 102, 0, 0.08)',
            borderWidth: 2.5,
            pointBackgroundColor: '#ff6600',
            pointBorderColor: '#fffafa',
            pointBorderWidth: 2,
            pointRadius: 5,
            pointHoverRadius: 7,
            fill: true,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: '#2b2b2b',
            titleColor: '#aaa',
            bodyColor: '#ff6600',
            bodyFont: { weight: 'bold', size: 14 },
            padding: 10,
            cornerRadius: 8,
            callbacks: {
              label: (ctx) => ` ${ctx.parsed.y} rendelés`,
            },
          },
        },
        scales: {
          x: {
            grid: { color: '#f0f0f0' },
            ticks: { color: '#999', font: { size: 12 } },
          },
          y: {
            beginAtZero: true,
            grid: { color: '#f0f0f0' },
            ticks: { color: '#999', font: { size: 12 }, stepSize: 1, precision: 0 },
          },
        },
      },
    });
  }
  goBack(): void {
    this.router.navigate(['/admin']);
  }

  getBarWidth(count: number): number {
    if (!this.topProducts.length) return 0;
    return Math.round((count / this.topProducts[0].count) * 100);
  }

  // ── PDF export ───────────────────────────────────────────────
  async exportPdf(): Promise<void> {
    const { jsPDF } = await import('jspdf');
    const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });

    const orange = [255, 102, 0] as [number, number, number];
    const dark = [43, 43, 43] as [number, number, number];
    const grey = [102, 102, 102] as [number, number, number];
    const light = [249, 249, 249] as [number, number, number];
    const pageW = 210;
    const margin = 20;

    // Fejléc
    doc.setFillColor(...orange);
    doc.rect(0, 0, pageW, 28, 'F');
    doc.setTextColor(255, 255, 255);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.text('CarComps — E-Commerce Riport', margin, 18);

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(...grey);
    doc.text(`Generálva: ${this.lastUpdated.toLocaleString('hu-HU')}`, margin, 36);

    doc.setDrawColor(...orange);
    doc.setLineWidth(0.5);
    doc.line(margin, 40, pageW - margin, 40);

    // Stat kártyák
    const cards = [
      { label: 'Regisztralt ugyfelek', value: String(this.totalCustomers) },
      { label: 'Aktiv ugyfelek', value: String(this.activeCustomers) },
      { label: 'Osszes rendeles', value: String(this.totalOrders) },
      { label: 'Teljesitett', value: String(this.deliveredOrders) },
    ];

    const cardW = (pageW - margin * 2 - 15) / 4;
    const cardH = 36;
    const cardY = 48;

    cards.forEach((card, i) => {
      const x = margin + i * (cardW + 5);
      doc.setFillColor(...light);
      doc.roundedRect(x, cardY, cardW, cardH, 3, 3, 'F');
      doc.setFillColor(...orange);
      doc.roundedRect(x, cardY, 3, cardH, 1, 1, 'F');
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(7);
      doc.setTextColor(...grey);
      doc.text(card.label, x + 6, cardY + 11);
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(18);
      doc.setTextColor(...dark);
      doc.text(card.value, x + 6, cardY + 27);
    });

    // Rendelés státuszok
    const statusY = cardY + cardH + 14;
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(...dark);
    doc.text('Rendelések státusza', margin, statusY);
    doc.setDrawColor(...orange);
    doc.line(margin, statusY + 3, pageW - margin, statusY + 3);

    const statuses = [
      { label: 'Fuggoben', value: this.pendingOrders },
      { label: 'Szallitas', value: this.shippedOrders },
      { label: 'Teljesitett', value: this.deliveredOrders },
    ];
    const sCardW = (pageW - margin * 2 - 10) / 3;
    statuses.forEach((s, i) => {
      const x = margin + i * (sCardW + 5);
      doc.setFillColor(...light);
      doc.roundedRect(x, statusY + 8, sCardW, 28, 3, 3, 'F');
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(8);
      doc.setTextColor(...grey);
      doc.text(s.label, x + 6, statusY + 19);
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(16);
      doc.setTextColor(...dark);
      doc.text(String(s.value), x + 6, statusY + 30);
    });

    // Havi rendelések táblázat
    const tableY = statusY + 50;
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(...dark);
    doc.text('Havi rendelések', margin, tableY);
    doc.setDrawColor(...orange);
    doc.line(margin, tableY + 3, pageW - margin, tableY + 3);

    const colW = (pageW - margin * 2) / 12;
    MONTH_SHORT.forEach((label, i) => {
      const x = margin + i * colW;
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(8);
      doc.setTextColor(...grey);
      doc.text(label, x + colW / 2, tableY + 12, { align: 'center' });
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(11);
      doc.setTextColor(...dark);
      doc.text(String(this.monthlyCounts[i]), x + colW / 2, tableY + 22, { align: 'center' });
    });

    // Top termékek
    if (this.topProducts.length > 0) {
      const topY = tableY + 36;
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(12);
      doc.setTextColor(...dark);
      doc.text('Top termekek', margin, topY);
      doc.setDrawColor(...orange);
      doc.line(margin, topY + 3, pageW - margin, topY + 3);

      this.topProducts.forEach((p, i) => {
        const y = topY + 12 + i * 10;
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(9);
        doc.setTextColor(...dark);
        doc.text(`${i + 1}. ${p.name}`, margin, y);
        doc.setTextColor(...orange);
        doc.text(`${p.count} db`, pageW - margin, y, { align: 'right' });
      });
    }

    // Lábléc
    doc.setDrawColor(220, 220, 220);
    doc.setLineWidth(0.3);
    doc.line(margin, 280, pageW - margin, 280);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8);
    doc.setTextColor(...grey);
    doc.text('CarComps E-Commerce Dashboard', margin, 286);
    doc.text('carcomps.hu', pageW - margin, 286, { align: 'right' });

    doc.save(`carcomps-ecommerce-${new Date().toISOString().slice(0, 10)}.pdf`);
  }
}
