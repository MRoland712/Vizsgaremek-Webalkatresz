import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { GetAllStatsService } from '../../services/getallstats.service';
import { GetAllStatsModel } from '../../models/getallstats.model';

// ── Belső interfészek ────────────────────────────────────────
interface MonthlySalesData {
  month: string;
  shortLabel: string;
  value: number;
  orders: number;
}

interface TopProduct {
  name: string;
  count: number;
}

interface ChartPoint {
  x: number;
  y: number;
}

interface TooltipData {
  month: string;
  value: number;
}

// Hónapnév map (createdAt string → hónap index)
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
export class EcommerceDashboardComponent implements OnInit {
  private statsService = inject(GetAllStatsService);

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

  // ── Monthly Sales Chart ──────────────────────────────────────
  monthLabels: string[] = [];
  monthlySales: MonthlySalesData[] = [];

  chartPoints: ChartPoint[] = [];
  salesLinePath = '';
  salesAreaPath = '';
  gridLines: number[] = [44, 88, 132, 176];
  yAxisLabels: string[] = [];

  // ── Tooltip ──────────────────────────────────────────────────
  tooltipVisible = false;
  tooltipX = 0;
  tooltipY = 0;
  tooltipData: TooltipData | null = null;

  // ── Top Products ─────────────────────────────────────────────
  topProducts: TopProduct[] = [];

  // ── Lifecycle ────────────────────────────────────────────────
  ngOnInit(): void {
    this.loadStats('365'); // alapértelmezett: utolsó 1 év
  }

  // ── Adatok betöltése ─────────────────────────────────────────
  loadStats(days: string): void {
    this.isLoading = true;
    this.hasError = false;

    // Admin ellenőrzés még a kérés előtt
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
      },
      error: (err) => {
        console.error('Hiba az adatok betöltésekor:', err);
        this.hasError = true;
        this.isLoading = false;
      },
    });
  }

  // ── API válasz → komponens mezők ────────────────────────────
  private mapApiData(data: GetAllStatsModel): void {
    // ── Customers ──────────────────────────────────────────────
    this.totalCustomers = data.allRegisteredUserCount ?? 0;
    this.activeCustomers = Array.isArray(data.activeUsers) ? data.activeUsers.length : 0;
    this.customerRetentionRate =
      this.totalCustomers > 0 ? Math.round((this.activeCustomers / this.totalCustomers) * 100) : 0;

    // ── Orders ────────────────────────────────────────────────
    this.totalOrders = data.ordersCount ?? 0;

    if (Array.isArray(data.allOrders)) {
      this.pendingOrders = data.allOrders.filter((o) => o.status === 'PENDING').length;
      this.shippedOrders = data.allOrders.filter((o) => o.status === 'SHIPPED').length;
    }
    this.deliveredOrders = Array.isArray(data.allDeliveredOrders)
      ? data.allDeliveredOrders.length
      : 0;

    // ── Monthly Sales (rendelések havi bontása) ────────────────
    this.buildMonthlySales(data);

    // ── Top Products ──────────────────────────────────────────
    if (Array.isArray(data.mostPurchasedPart)) {
      this.topProducts = [...data.mostPurchasedPart]
        .sort((a, b) => b.quantity - a.quantity)
        .slice(0, 5)
        .map((p) => ({
          name: p.partName,
          count: p.quantity,
        }));
    }
  }

  // ── Havi forgalom diagram felépítése ──────────────────────
  private buildMonthlySales(data: GetAllStatsModel): void {
    // Inicializálás: 12 hónapos tömb nullákkal
    const monthlyCounts = new Array(12).fill(0);

    // allOrders havi bontás (createdAt mező alapján)
    if (Array.isArray(data.allOrders)) {
      data.allOrders.forEach((order) => {
        if (order.createdAt) {
          const monthIndex = new Date(order.createdAt).getMonth();
          if (monthIndex >= 0 && monthIndex < 12) {
            monthlyCounts[monthIndex]++;
          }
        }
      });
    }

    // allTransactions string → szám konverzió (ha elérhető)
    const totalRevenue = parseFloat(data.allTransactions) || 0;
    const totalOrdersForRatio = data.ordersCount || 1;
    const avgOrderValue = totalRevenue / totalOrdersForRatio;

    // MonthlySalesData tömb összeállítása
    this.monthlySales = MONTH_NAMES.map((name, i) => ({
      month: name,
      shortLabel: MONTH_SHORT[i],
      orders: monthlyCounts[i],
      value: Math.round(monthlyCounts[i] * avgOrderValue),
    }));

    this.monthLabels = MONTH_SHORT;

    // Y-tengely feliratok
    const maxVal = Math.max(...this.monthlySales.map((d) => d.value), 1);
    this.yAxisLabels = [
      this.formatYLabel(maxVal),
      this.formatYLabel(maxVal * 0.66),
      this.formatYLabel(maxVal * 0.33),
      '0',
    ];

    this.buildChartPaths();
  }

  // ── SVG útvonalak kiszámítása ─────────────────────────────
  private buildChartPaths(): void {
    const svgW = 700;
    const svgH = 220;
    const padLeft = 10;
    const padRight = 10;
    const padTop = 15;
    const padBottom = 10;

    const values = this.monthlySales.map((d) => d.value);
    const maxVal = Math.max(...values, 1);
    const n = this.monthlySales.length;

    const toX = (i: number) => padLeft + (i / (n - 1)) * (svgW - padLeft - padRight);
    const toY = (v: number) => padTop + (1 - v / maxVal) * (svgH - padTop - padBottom);

    this.chartPoints = this.monthlySales.map((d, i) => ({
      x: toX(i),
      y: toY(d.value),
    }));

    const pts = this.chartPoints;

    this.salesLinePath = pts
      .map((p, i) => (i === 0 ? `M${p.x},${p.y}` : `L${p.x},${p.y}`))
      .join(' ');

    this.salesAreaPath =
      `M${pts[0].x},${svgH} ` +
      pts.map((p) => `L${p.x},${p.y}`).join(' ') +
      ` L${pts[pts.length - 1].x},${svgH} Z`;
  }

  // ── Tooltip kezelés ───────────────────────────────────────
  showTooltip(index: number, event: MouseEvent): void {
    const target = event.target as SVGCircleElement;
    const rect = target.closest('.chart-area')?.getBoundingClientRect();
    const targetRect = target.getBoundingClientRect();

    if (rect) {
      this.tooltipX = targetRect.left - rect.left;
      this.tooltipY = targetRect.top - rect.top - 55;
    }

    this.tooltipData = {
      month: this.monthlySales[index].month,
      value: this.monthlySales[index].value,
    };
    this.tooltipVisible = true;
  }

  hideTooltip(): void {
    this.tooltipVisible = false;
  }

  // ── Segédmetódusok ────────────────────────────────────────
  private formatYLabel(value: number): string {
    if (value >= 1_000_000) return (value / 1_000_000).toFixed(1).replace('.0', '') + 'M';
    if (value >= 1_000) return (value / 1_000).toFixed(0) + 'K';
    return value.toFixed(0);
  }

  // Template helper: top termék sávszélesség %
  getBarWidth(count: number): number {
    if (!this.topProducts.length) return 0;
    return Math.round((count / this.topProducts[0].count) * 100);
  }
}
