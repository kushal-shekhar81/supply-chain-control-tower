package io.scct.integration.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * What the dashboard's first screen needs: a few headline numbers and a watchlist.
 * A response shaped for one screen like this is called a "backend for frontend" view.
 */
public record SnapshotSummary(
        Instant asOf,
        String sourceSystem,
        int plants,
        int suppliers,
        int parts,
        int singleSourcedParts,
        int positionsBelowSafetyStock,
        int overduePurchaseOrderLines,
        int openAogRequests,
        List<WatchlistItem> watchlist) {

    /**
     * One part at one plant that deserves a look.
     *
     * @param daysOfCover usable stock divided by average daily demand; null when there is no demand
     */
    public record WatchlistItem(
            String plantId,
            String partId,
            String description,
            String industry,
            String criticality,
            BigDecimal usableStock,
            BigDecimal safetyStock,
            BigDecimal daysOfCover,
            boolean singleSourced,
            int overduePurchaseOrderLines,
            boolean aog) {
    }
}
