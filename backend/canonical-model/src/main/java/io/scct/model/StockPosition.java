package io.scct.model;

import java.math.BigDecimal;

/**
 * Stock of one part at one plant.
 *
 * <p>SAP mapping: MARD (stock per storage location, summed per plant) and MARC (plant data).
 * {@code unrestricted} = MARD-LABST, {@code qualityInspection} = MARD-INSME,
 * {@code blocked} = MARD-SPEME, {@code safetyStock} = MARC-EISBE.
 * Live view in SAP GUI: transaction MMBE.
 */
public record StockPosition(
        String plantId,
        String partId,
        BigDecimal unrestricted,
        BigDecimal qualityInspection,
        BigDecimal blocked,
        BigDecimal safetyStock) {

    /** Only unrestricted stock can be used in production right now. */
    public BigDecimal usable() {
        return unrestricted;
    }

    /** True when usable stock has fallen below the safety stock the planner set. */
    public boolean belowSafetyStock() {
        return unrestricted.compareTo(safetyStock) < 0;
    }
}
