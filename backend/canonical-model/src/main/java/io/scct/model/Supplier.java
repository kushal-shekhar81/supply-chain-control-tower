package io.scct.model;

/**
 * A supplier (vendor).
 *
 * <p>SAP mapping: business partner / vendor master LFA1. {@code supplierId} = LIFNR,
 * {@code name} = NAME1, {@code country} = LAND1.
 * {@code tier} and {@code onTimeDeliveryRate} are NOT in SAP standard tables: they come from
 * supplier evaluation (ME61/ME6H), a supplier portal or the control tower itself. That gap is
 * one reason control towers exist - the ERP alone does not hold the full picture.
 *
 * @param tier               1 = supplies us directly, 2 = supplies our Tier-1, and so on
 * @param onTimeDeliveryRate share of deliveries on time over the last 12 months, 0.0 to 1.0
 */
public record Supplier(
        String supplierId,
        String name,
        String country,
        int tier,
        double onTimeDeliveryRate) {

    public Supplier {
        if (tier < 1) {
            throw new IllegalArgumentException("tier must be 1 or higher");
        }
        if (onTimeDeliveryRate < 0.0 || onTimeDeliveryRate > 1.0) {
            throw new IllegalArgumentException("onTimeDeliveryRate must be between 0 and 1");
        }
    }
}
