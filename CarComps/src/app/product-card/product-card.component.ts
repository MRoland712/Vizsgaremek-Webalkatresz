import { Component } from '@angular/core';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  productIMG = '/assets/CarComps_Logo_BigassC.png';
  productName = '';
  articleNumber = '';
  items = [];
  price = 0;
  addToCart() {}
}
