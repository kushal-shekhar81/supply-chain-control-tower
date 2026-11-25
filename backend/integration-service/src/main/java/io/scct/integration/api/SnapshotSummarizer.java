package io.scct.integration.api;

import io.scct.model.DemandRequirement;
import io.scct.model.Industry;
import io.scct.model.Part;
import io.scct.model.PurchaseOrderLine;
import io.scct.model.StockPosition;
import io.scct.model.SupplyChainSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Turns a raw snapshot into the dashboard summary.
 *
 * <p>Plain Java on purpose (no Spring): pure logic is the easiest code to test and reason about.
 * In week 4 this logic moves into the visibility-service.
 *
 * <p><b>Days of cover</b> = usable stock / average daily demand. It answers the question every
 * plant manager asks first: "How long can we keep running with what we have?"
 */
public final class SnapshotSummarizer {

    /** Demand horizon used for the daily average: 3 weeks for auto, 60 days for MRO. */
    private static final int AUTO_HORIZON_DAYS = 21;
    private static final int AERO_HORIZON_DAYS = 60;

    private SnapshotSummarizer() {
    }

    public static SnapshotSummary summarize(SupplyChainSnapshot snapshot, LocalDate today) {
        Map<String, Part> partsById = snapshot.parts().stream()
                .collect(Collectors.toMap(Part::partId, Function.identity()));

        List<SnapshotSummary.WatchlistItem> watchlist = snapshot.stock().stream()
                .map(stock -> toWatchlistItem(stock, partsById.get(stock.partId()), snapshot, today))
                .filter(SnapshotSummarizer::needsAttention)
                .sorted(Comparator
                        .comparing(SnapshotSummary.WatchlistItem::aog).reversed()
                        .thenComparing(item -> item.daysOfCover() == null ? BigDecimal.valueOf(9999) : item.daysOfCover()))
                .toList();

        int overdue = (int) snapshot.openPurchaseOrders().stream()
                .filter(po -> po.deliveryDate().isBefore(today)).count();
        int aog = (int) snapshot.demand().stream()
                .filter(d -> d.source() == DemandRequirement.Source.AOG).count();

        return new SnapshotSummary(
                snapshot.asOf(),
                snapshot.sourceSystem(),
                snapshot.plants().size(),
                snapshot.suppliers().size(),
                snapshot.parts().size(),
                (int) snapshot.parts().stream().filter(Part::isSingleSourced).count(),
                (int) snapshot.stock().stream().filter(StockPosition::belowSafetyStock).count(),
                overdue,
                aog,
                watchlist);
    }

    private static SnapshotSummary.WatchlistItem toWatchlistItem(StockPosition stock, Part part,
                                                                 SupplyChainSnapshot snapshot, LocalDate today) {
        int horizon = part.industry() == Industry.AUTOMOTIVE ? AUTO_HORIZON_DAYS : AERO_HORIZON_DAYS;
        BigDecimal totalDemand = snapshot.demand().stream()
                .filter(d -> d.plantId().equals(stock.plantId()) && d.partId().equals(stock.partId()))
                .filter(d -> d.source() != DemandRequirement.Source.AOG)
                .map(DemandRequirement::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal dailyDemand = totalDemand.divide(BigDecimal.valueOf(horizon), 4, RoundingMode.HALF_UP);
        BigDecimal daysOfCover = dailyDemand.signum() == 0
                ? null
                : stock.usable().divide(dailyDemand, 1, RoundingMode.HALF_UP);

        int overdueLines = (int) snapshot.openPurchaseOrders().stream()
                .filter(po -> po.plantId().equals(stock.plantId()) && po.partId().equals(stock.partId()))
                .map(PurchaseOrderLine::deliveryDate)
                .filter(date -> date.isBefore(today))
                .count();
        boolean aog = snapshot.demand().stream()
                .anyMatch(d -> d.source() == DemandRequirement.Source.AOG
                        && d.plantId().equals(stock.plantId()) && d.partId().equals(stock.partId()));

        return new SnapshotSummary.WatchlistItem(
                stock.plantId(), part.partId(), part.description(), part.industry().name(),
                part.criticality().name(), stock.usable(), stock.safetyStock(), daysOfCover,
                part.isSingleSourced(), overdueLines, aog);
    }

    /** Watch a position if it is AOG, below safety stock, or a single-sourced part with an overdue PO. */
    private static boolean needsAttention(SnapshotSummary.WatchlistItem item) {
        boolean belowSafety = item.usableStock().compareTo(item.safetyStock()) < 0;
        return item.aog() || belowSafety || (item.singleSourced() && item.overduePurchaseOrderLines() > 0);
    }
}
