import { Component, signal, OnInit } from '@angular/core';

@Component({
  selector: 'app-advertisement',
  imports: [],
  templateUrl: './advertisement.component.html',
  styleUrl: './advertisement.component.css',
})
export class AdvertisementComponent implements OnInit {
  slideIndex = signal(1);
  slideTimeout: any;

  ngOnInit() {
    this.showSlides();
  }

  showSlides() {
    if (this.slideTimeout) {
      clearTimeout(this.slideTimeout);
    }

    const slides = Array.from(document.getElementsByClassName('mySlides')) as HTMLElement[];
    const dots = Array.from(document.getElementsByClassName('dot')) as HTMLElement[];

    // wrap index
    if (this.slideIndex() > slides.length) {
      this.slideIndex.set(1);
    }
    if (this.slideIndex() < 1) {
      this.slideIndex.set(slides.length);
    }

    // assign classes based on current index
    slides.forEach((slide, idx) => {
      slide.classList.remove('in', 'out-left', 'out-right');
      if (idx === this.slideIndex() - 1) {
        slide.classList.add('in');
      } else if (idx < this.slideIndex() - 1) {
        slide.classList.add('out-left');
      } else {
        slide.classList.add('out-right');
      }
    });

    dots.forEach((dot, idx) => {
      dot.classList.toggle('active', idx === this.slideIndex() - 1);
    });

    this.slideTimeout = setTimeout(() => {
      this.slideIndex.update((val) => val + 1);
      this.showSlides();
    }, 15000);
  }

  currentSlide(n: number) {
    this.slideIndex.set(n);
    this.showSlides();
  }
}
