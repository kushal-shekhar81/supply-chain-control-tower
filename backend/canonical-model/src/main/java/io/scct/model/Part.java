package io.scct.model;

import java.util.List;

/**
 * A purchased part (material).
 *
 * <p>SAP mapping: MARA (general) + MAKT (description). {@code partId} = MATNR,
 * {@code description} = MAKTX, {@code unitOfMeasure} = MEINS.
 * {@code approvedSupplierIds} comes from the source list (EORD) or purchasing info records (EINA).
 *
 * @param leadTimeDays planned delivery time in days (MARC-PLIFZ)
 */
public record Part(
        String partId,
        String description,
        Industry industry,
        String unitOfMeasure,
        Criticality criticality,
        int leadTimeDays,
        List<String> approvedSupplierIds) {

    public Part {
        approvedSupplierIds = List.copyOf(approvedSupplierIds);
    }

    /**
     * Single-sourced = only one approved supplier. A classic resilience risk: if that supplier
     * fails (fire, flood, insolvency, export ban), there is no quick alternative.
     */
    public boolean isSingleSourced() {
        return approvedSupplierIds.size() == 1;
    }
}
