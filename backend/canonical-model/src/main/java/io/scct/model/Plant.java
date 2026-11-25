package io.scct.model;

/**
 * A plant, warehouse or MRO hub.
 *
 * <p>SAP mapping: table T001W. {@code plantId} = WERKS, {@code name} = NAME1,
 * {@code companyCode} = BUKRS (via T001K), {@code country} = LAND1.
 */
public record Plant(
        String plantId,
        String name,
        String companyCode,
        Industry industry,
        String country) {
}
