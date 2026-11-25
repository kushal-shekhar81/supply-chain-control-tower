package io.scct.synthetic;

import io.scct.model.DemandRequirement;
import io.scct.model.Part;
import io.scct.model.SupplyChainSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyntheticDataGeneratorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 24);
    private static final Instant AS_OF = Instant.parse("2026-11-24T04:00:00Z");

    @Test
    void sameSeedGivesSameData() {
        SupplyChainSnapshot first = new SyntheticDataGenerator(7).generate(TODAY, AS_OF);
        SupplyChainSnapshot second = new SyntheticDataGenerator(7).generate(TODAY, AS_OF);
        assertEquals(first, second, "records compare by value, so equal data means equal snapshots");
    }

    @Test
    void dataContainsTheProblemsAControlTowerMustCatch() {
        SupplyChainSnapshot snapshot = new SyntheticDataGenerator().generate(TODAY, AS_OF);

        assertTrue(snapshot.parts().stream().anyMatch(Part::isSingleSourced), "single-sourced parts");
        assertTrue(snapshot.stock().stream().anyMatch(s -> s.belowSafetyStock()), "stock below safety");
        assertTrue(snapshot.openPurchaseOrders().stream().anyMatch(po -> po.deliveryDate().isBefore(TODAY)),
                "overdue purchase orders");
        assertTrue(snapshot.demand().stream().anyMatch(d -> d.source() == DemandRequirement.Source.AOG),
                "an AOG request");
    }

    @Test
    void everyStockPositionBelongsToAPlantOfTheSameIndustry() {
        SupplyChainSnapshot snapshot = new SyntheticDataGenerator().generate(TODAY, AS_OF);
        snapshot.stock().forEach(stock -> {
            var plant = snapshot.plants().stream().filter(p -> p.plantId().equals(stock.plantId())).findFirst().orElseThrow();
            var part = snapshot.parts().stream().filter(p -> p.partId().equals(stock.partId())).findFirst().orElseThrow();
            assertEquals(plant.industry(), part.industry());
        });
        assertFalse(snapshot.stock().isEmpty());
    }
}
