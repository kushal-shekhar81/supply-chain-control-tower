package io.scct.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One purchase order item = open supply on its way to us.
 *
 * <p>SAP mapping: EKKO (header) + EKPO (item) + EKET (schedule line).
 * {@code poNumber} = EBELN, {@code item} = EBELP, {@code supplierId} = EKKO-LIFNR,
 * {@code plantId} = EKPO-WERKS, {@code partId} = EKPO-MATNR,
 * {@code openQuantity} = EKET-MENGE minus EKET-WEMNG (ordered minus received),
 * {@code deliveryDate} = EKET-EINDT. Live view in SAP GUI: ME2M / ME23N.
 */
public record PurchaseOrderLine(
        String poNumber,
        int item,
        String supplierId,
        String plantId,
        String partId,
        BigDecimal openQuantity,
        LocalDate deliveryDate,
        PoStatus status) {

    public enum PoStatus {
        /** Sent to the supplier, no confirmation yet. */
        OPEN,
        /** Supplier confirmed date and quantity (SAP: confirmation control, EKES). */
        CONFIRMED,
        /** Goods are in transit (SAP: inbound delivery exists). */
        SHIPPED
    }
}
