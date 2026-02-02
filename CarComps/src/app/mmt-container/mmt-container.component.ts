import { Component } from '@angular/core';
import { MMTSelectorComponent } from '../mmt-selector/mmt-selector.component';

@Component({
  selector: 'app-mmt-container',
  imports: [MMTSelectorComponent],
  templateUrl: './mmt-container.component.html',
  styleUrl: './mmt-container.component.css',
})
export class MmtContainerComponent {}
