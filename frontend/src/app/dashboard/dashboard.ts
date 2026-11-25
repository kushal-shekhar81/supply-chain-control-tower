import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ControlTowerService } from '../core/control-tower.service';
import { SnapshotSummary, WatchlistItem } from '../core/snapshot-summary.model';

type IndustryFilter = 'ALL' | 'AUTOMOTIVE' | 'AEROSPACE';

/**
 * First screen of the control tower: headline KPIs and a watchlist.
 * State lives in signals: when a signal changes, only the parts of the view that read it update.
 */
@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private readonly service = inject(ControlTowerService);

  protected readonly summary = signal<SnapshotSummary | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(true);
  protected readonly industry = signal<IndustryFilter>('ALL');

  protected readonly visibleWatchlist = computed<WatchlistItem[]>(() => {
    const items = this.summary()?.watchlist ?? [];
    const filter = this.industry();
    return filter === 'ALL' ? items : items.filter((item) => item.industry === filter);
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.getSummary().subscribe({
      next: (data) => {
        this.summary.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not reach the integration service. Is it running on port 8081?');
        this.loading.set(false);
      },
    });
  }

  protected setIndustry(value: IndustryFilter): void {
    this.industry.set(value);
  }

  /** Traffic-light rule: under 3 days of cover is red, under 7 amber. */
  protected coverLevel(item: WatchlistItem): 'red' | 'amber' | 'green' {
    if (item.aog || item.daysOfCover === null || item.daysOfCover < 3) {
      return 'red';
    }
    return item.daysOfCover < 7 ? 'amber' : 'green';
  }
}
