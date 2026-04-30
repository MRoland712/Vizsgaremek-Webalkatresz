import { Component, signal, AfterViewInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-advertisement',
  imports: [],
  templateUrl: './advertisement.component.html',
  styleUrl: './advertisement.component.css',
})
export class AdvertisementComponent implements AfterViewInit, OnDestroy {
  slideIndex = signal(0);
  private prevIndex = 0;
  private slideTimeout: any = null;
  private isAnimating = false;
  private readonly SLIDE_INTERVAL = 15000;

  // ⭐ AfterViewInit — garantáltan le van renderelve a DOM
  ngAfterViewInit() {
    this.initSlides();
    this.scheduleNext();
  }

  ngOnDestroy() {
    this.clearTimer();
  }

  private clearTimer() {
    if (this.slideTimeout !== null) {
      clearTimeout(this.slideTimeout);
      this.slideTimeout = null;
    }
  }

  private scheduleNext() {
    this.clearTimer();
    this.slideTimeout = setTimeout(() => {
      const slides = document.getElementsByClassName('mySlides');
      const next = (this.slideIndex() + 1) % slides.length;
      this.goToSlide(next);
    }, this.SLIDE_INTERVAL);
  }

  private initSlides() {
    const slides = Array.from(document.getElementsByClassName('mySlides')) as HTMLElement[];
    const dots = Array.from(document.getElementsByClassName('dot')) as HTMLElement[];

    slides.forEach((slide, i) => {
      slide.className = 'mySlides';
      if (i === 0) slide.classList.add('in');
    });
    dots.forEach((dot, i) => {
      dot.classList.toggle('active', i === 0);
    });
  }

  private goToSlide(nextIdx: number) {
    if (this.isAnimating) return;
    const slides = Array.from(document.getElementsByClassName('mySlides')) as HTMLElement[];
    const dots = Array.from(document.getElementsByClassName('dot')) as HTMLElement[];
    const total = slides.length;
    const normalized = ((nextIdx % total) + total) % total;

    if (normalized === this.slideIndex()) {
      this.scheduleNext();
      return;
    }

    this.isAnimating = true;
    this.prevIndex = this.slideIndex();
    this.slideIndex.set(normalized);

    const prev = slides[this.prevIndex];
    const curr = slides[normalized];

    slides.forEach((s, i) => {
      if (i !== this.prevIndex && i !== normalized) s.className = 'mySlides';
    });

    prev.className = 'mySlides out-left';
    curr.className = 'mySlides in';

    dots.forEach((dot, i) => dot.classList.toggle('active', i === normalized));

    setTimeout(() => {
      prev.className = 'mySlides';
      this.isAnimating = false;
      this.scheduleNext();
    }, 1000);
  }

  currentSlide(n: number) {
    this.clearTimer();
    this.goToSlide(n - 1);
  }
}
