import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BreadcrumbService } from '../../services/breadcrumb.service';
import { BreadcrumbModel } from '../../models/breadcrumbs.model';

@Component({
  selector: 'app-dynamic-breadcrumbs',
  imports: [RouterLink],
  templateUrl: './dynamic-breadcrumbs.component.html',
  styleUrl: './dynamic-breadcrumbs.component.css',
})
export class DynamicBreadcrumbsComponent implements OnInit {
  private breadcrumbService = inject(BreadcrumbService);
  breadcrumbs: BreadcrumbModel[] = [];
  ngOnInit(): void {
    this.breadcrumbService.breadcrumbs$.subscribe((breadcrumbs) => {
      this.breadcrumbs = breadcrumbs;
    });
  }
}
