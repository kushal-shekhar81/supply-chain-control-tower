/**
 * Mirrors the Java record io.scct.integration.api.SnapshotSummary.
 * Keeping front-end types identical to the API contract avoids silent mismatches.
 */
export interface WatchlistItem {
  plantId: string;
  partId: string;
  description: string;
  industry: 'AUTOMOTIVE' | 'AEROSPACE';
  criticality: 'HIGH' | 'MEDIUM' | 'LOW';
  usableStock: number;
  safetyStock: number;
  /** null when the part has no demand in the horizon */
  daysOfCover: number | null;
  singleSourced: boolean;
  overduePurchaseOrderLines: number;
  aog: boolean;
}

export interface SnapshotSummary {
  asOf: string;
  sourceSystem: string;
  plants: number;
  suppliers: number;
  parts: number;
  singleSourcedParts: number;
  positionsBelowSafetyStock: number;
  overduePurchaseOrderLines: number;
  openAogRequests: number;
  watchlist: WatchlistItem[];
}
