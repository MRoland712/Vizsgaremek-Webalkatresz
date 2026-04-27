import { Component, signal, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-advertisement',
  imports: [],
  templateUrl: './advertisement.component.html',
  styleUrl: './advertisement.component.css',
})
export class AdvertisementComponent implements OnInit, OnDestroy {
  slideIndex = signal(0);
  private prevIndex = 0;
  private slideTimeout: any = null;
  private isAnimating = false;
  private readonly SLIDE_INTERVAL = 15000;

  ngOnInit() {
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
    const dots   = Array.from(document.getElementsByClassName('dot'))     as HTMLElement[];

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
    const dots   = Array.from(document.getElementsByClassName('dot'))     as HTMLElement[];
    const total  = slides.length;
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

    // Reset összes slide — csak prev és curr animálunk
    slides.forEach((s, i) => {
      if (i !== this.prevIndex && i !== normalized) {
        s.className = 'mySlides';
      }
    });

    // Prev kimegy balra, curr bejön jobbról
    prev.className = 'mySlides out-left';
    curr.className = 'mySlides in';

    // Dots
    dots.forEach((dot, i) => dot.classList.toggle('active', i === normalized));

    // Animáció vége után reset
    setTimeout(() => {
      prev.className = 'mySlides';
      this.isAnimating = false;
      this.scheduleNext();
    }, 1000); // transition ideje
  }

  // Dot kattintásra (1-alapú)
  currentSlide(n: number) {
    this.clearTimer();
    this.goToSlide(n - 1);
  }
}