package io.scct.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartTest {

    @Test
    void onlyOneApprovedSupplierMeansSingleSourced() {
        Part single = new Part("X-2001", "Turbine blade", Industry.AEROSPACE, "EA", Criticality.HIGH, 180, List.of("S200"));
        Part dual = new Part("A-1003", "Harness", Industry.AUTOMOTIVE, "EA", Criticality.MEDIUM, 21, List.of("S103", "S101"));
        assertTrue(single.isSingleSourced());
        assertFalse(dual.isSingleSourced());
    }

    @Test
    void stockBelowSafetyStockIsFlagged() {
        StockPosition low = new StockPosition("AM01", "A-1001", new BigDecimal("100"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("6000"));
        assertTrue(low.belowSafetyStock());
    }

    @Test
    void supplierRejectsImpossibleOnTimeRate() {
        assertThrows(IllegalArgumentException.class, () -> new Supplier("S1", "X", "IN", 1, 1.5));
    }
}
