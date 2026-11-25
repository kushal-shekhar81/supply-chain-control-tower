package io.scct.integration.api;

import io.scct.integration.source.SupplyChainSource;
import io.scct.model.SupplyChainSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * REST API, version 1. Putting the version in the path (/api/v1) lets the UI keep working
 * when a v2 with breaking changes arrives.
 */
@RestController
@RequestMapping("/api/v1/snapshot")
public class SnapshotController {

    private final SupplyChainSource source;
    private final Clock clock;
    private final ZoneId businessZone;

    public SnapshotController(SupplyChainSource source, Clock clock,
                              @Value("${scct.business-zone:Asia/Kolkata}") String businessZone) {
        this.source = source;
        this.clock = clock;
        this.businessZone = ZoneId.of(businessZone);
    }

    /** Full canonical data: plants, suppliers, parts, stock, open POs, demand. */
    @GetMapping
    public SupplyChainSnapshot snapshot() {
        return source.fetchSnapshot();
    }

    /** Headline KPIs + watchlist for the dashboard. */
    @GetMapping("/summary")
    public SnapshotSummary summary() {
        LocalDate today = LocalDate.now(clock.withZone(businessZone));
        return SnapshotSummarizer.summarize(source.fetchSnapshot(), today);
    }
}
