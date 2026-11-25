package io.scct.integration.source;

import io.scct.model.SupplyChainSnapshot;
import io.scct.synthetic.SyntheticDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/** Serves the fictional companies' data, generated fresh for "today" on every call. */
@Component
public class SyntheticSource implements SupplyChainSource {

    private final Clock clock;
    private final long seed;
    private final ZoneId businessZone;

    public SyntheticSource(Clock clock,
                           @Value("${scct.synthetic.seed:42}") long seed,
                           @Value("${scct.business-zone:Asia/Kolkata}") String businessZone) {
        this.clock = clock;
        this.seed = seed;
        this.businessZone = ZoneId.of(businessZone);
    }

    @Override
    public SupplyChainSnapshot fetchSnapshot() {
        LocalDate today = LocalDate.now(clock.withZone(businessZone));
        return new SyntheticDataGenerator(seed).generate(today, clock.instant());
    }
}
