import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type CheckoutStep = 'cart' | 'delivery' | 'payment' | 'summary';

interface Step {
  id: CheckoutStep;
  label: string;
  number: number;
}

@Component({
  selector: 'app-checkout-progress',
  imports: [CommonModule],
  templateUrl: './checkoutprogress.component.html',
  styleUrl: './checkoutprogress.component.css',
})
export class CheckoutProgressComponent {
  @Input() currentStep: CheckoutStep = 'cart';

  steps: Step[] = [
    { id: 'cart', label: 'Kosár', number: 1 },
    { id: 'delivery', label: 'Szállítás Adatok', number: 2 },
    { id: 'payment', label: 'Fizetés', number: 3 },
    { id: 'summary', label: 'Nyugtázás', number: 4 },
  ];

  getStepState(step: Step): 'completed' | 'active' | 'inactive' {
    const currentIndex = this.steps.findIndex((s) => s.id === this.currentStep);
    const stepIndex = this.steps.findIndex((s) => s.id === step.id);
    if (stepIndex < currentIndex) return 'completed';
    if (stepIndex === currentIndex) return 'active';
    return 'inactive';
  }

  isLineActive(index: number): boolean {
    const currentIndex = this.steps.findIndex((s) => s.id === this.currentStep);
    return index < currentIndex;
  }
}
