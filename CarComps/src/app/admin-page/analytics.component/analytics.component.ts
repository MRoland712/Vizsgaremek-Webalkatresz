import {
  Component,
  OnInit,
  AfterViewInit,
  OnDestroy,
  inject,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';

import { Chart, registerables } from 'chart.js';
import { GetAllStatsService } from '../../services/getallstats.service';
import { GetAllStatsModel } from '../../models/getallstats.model';
import { Router } from '@angular/router';

Chart.register(...registerables);

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css',
})
export class AnalyticsComponent implements OnInit, OnDestroy {
  private statsService = inject(GetAllStatsService);
  private router = inject(Router);

  @ViewChild('visitorChart') chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  // ── UI állapot ───────────────────────────────────────────────
  isLoading = true;
  hasError = false;
  lastUpdated = new Date();

  // ── Stat kártyák ─────────────────────────────────────────────
  activeUsersCount = 0;
  pageViews = 0;
  uniqueVisitors = 0;
  registeredUsers = 0;

  // ── Sparkline ────────────────────────────────────────────────
  sparkHeights: number[] = [30, 45, 35, 60, 55, 75, 70, 85];

  // ── Chart adatok ─────────────────────────────────────────────
  private monthlyVisits: number[] = new Array(12).fill(0);

  private readonly MONTH_LABELS = [
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

  ngOnInit(): void {
    this.loadStats('1');
  }

  ngOnDestroy(): void {
    this.chartInstance?.destroy();
  }
  goBack(): void {
    this.router.navigate(['/admin']);
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
        // Chart inicializálás a DOM frissülése után
        setTimeout(() => this.initChart(), 50);
      },
      error: (err) => {
        console.error('Analytics hiba:', err);
        this.hasError = true;
        this.isLoading = false;
      },
    });
  }

  private mapData(data: GetAllStatsModel): void {
    this.activeUsersCount = data.activeUsers?.length ?? 0;
    this.pageViews = data.pageViews ?? 0;
    this.uniqueVisitors = data.uniqueVisitors ?? 0;
    this.registeredUsers = data.allRegisteredUserCount ?? 0;

    // Havi bontás az allOrders createdAt alapján
    this.monthlyVisits = new Array(12).fill(0);
    if (data.allOrders) {
      data.allOrders.forEach((order) => {
        if (order.createdAt) {
          const monthIndex = new Date(order.createdAt).getMonth();
          if (monthIndex >= 0 && monthIndex < 12) {
            this.monthlyVisits[monthIndex]++;
          }
        }
      });
    }

    this.generateSparkline();
  }

  private generateSparkline(): void {
    this.sparkHeights = Array.from({ length: 8 }, (_, i) =>
      Math.round(20 + (Math.sin(i * 1.3) + 1) * 0.5 * 75),
    );
  }

  private initChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;

    // Előző instance törlése ha volt
    this.chartInstance?.destroy();

    this.chartInstance = new Chart(canvas, {
      type: 'line',
      data: {
        labels: this.MONTH_LABELS,
        datasets: [
          {
            label: 'Havi rendelések',
            data: this.monthlyVisits,
            borderColor: '#ff6600',
            backgroundColor: 'rgba(255, 102, 0, 0.08)',
            borderWidth: 2.5,
            pointBackgroundColor: '#ff6600',
            pointBorderColor: '#ffffff',
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
            ticks: {
              color: '#999',
              font: { size: 12 },
              stepSize: 1,
              precision: 0,
            },
          },
        },
      },
    });
  }

  // ── PDF export ───────────────────────────────────────────────
  async exportPdf(): Promise<void> {
    const { jsPDF } = await import('jspdf');
    const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });

    const orange = [255, 102, 0] as [number, number, number];
    const dark = [43, 43, 43] as [number, number, number];
    const grey = [102, 102, 102] as [number, number, number];
    const lightBg = [249, 249, 249] as [number, number, number];
    const pageW = 210;
    const margin = 20;

    // Fejléc
    doc.setFillColor(...orange);
    doc.rect(0, 0, pageW, 28, 'F');
    doc.setTextColor(255, 255, 255);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.text('CarComps — Analitika Riport', margin, 18);

    // Dátum
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(...grey);
    doc.text(`Generálva: ${this.lastUpdated.toLocaleString('hu-HU')}`, margin, 36);

    doc.setDrawColor(...orange);
    doc.setLineWidth(0.5);
    doc.line(margin, 40, pageW - margin, 40);

    // Stat kártyák
    const cards = [
      { label: 'Aktiv felhasznalok', value: String(this.activeUsersCount) },
      { label: 'Oldalmegtekintsek', value: String(this.pageViews) },
      { label: 'Egyedi latogatók', value: String(this.uniqueVisitors) },
      { label: 'Regisztralt userek', value: String(this.registeredUsers) },
    ];

    const cardW = (pageW - margin * 2 - 15) / 4;
    const cardH = 36;
    const cardY = 48;

    cards.forEach((card, i) => {
      const x = margin + i * (cardW + 5);
      doc.setFillColor(...lightBg);
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

    // Havi adat táblázat
    const tableY = cardY + cardH + 16;
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(...dark);
    doc.text('Havi rendelések áttekintése', margin, tableY);

    doc.setDrawColor(...orange);
    doc.line(margin, tableY + 3, pageW - margin, tableY + 3);

    const colW = (pageW - margin * 2) / 12;
    this.MONTH_LABELS.forEach((label, i) => {
      const x = margin + i * colW;
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(8);
      doc.setTextColor(...grey);
      doc.text(label, x + colW / 2, tableY + 12, { align: 'center' });
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(11);
      doc.setTextColor(...dark);
      doc.text(String(this.monthlyVisits[i]), x + colW / 2, tableY + 22, { align: 'center' });
    });

    // Lábléc
    const footerY = 280;
    doc.setDrawColor(220, 220, 220);
    doc.setLineWidth(0.3);
    doc.line(margin, footerY, pageW - margin, footerY);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8);
    doc.setTextColor(...grey);
    doc.text('CarComps Analytics Dashboard', margin, footerY + 6);
    doc.text('carcomps.hu', pageW - margin, footerY + 6, { align: 'right' });

    doc.save(`carcomps-analytics-${new Date().toISOString().slice(0, 10)}.pdf`);
  }
}
