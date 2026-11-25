import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SnapshotSummary } from './snapshot-summary.model';

/**
 * Single place where the UI talks to the backend.
 * In dev, /api is forwarded to the integration service on :8081 by proxy.conf.json.
 * From week 8 the same path goes to the API gateway instead - no component changes needed.
 */
@Injectable({ providedIn: 'root' })
export class ControlTowerService {
  private readonly http = inject(HttpClient);

  getSummary(): Observable<SnapshotSummary> {
    return this.http.get<SnapshotSummary>('/api/v1/snapshot/summary');
  }
}
