package io.scct.model;

import java.time.Instant;
import java.util.List;

/**
 * Everything the control tower knows at one moment, in one model, whatever system it came from.
 * In week 3 this is replaced by a stream of events on Kafka; a snapshot is the simplest start.
 */
public record SupplyChainSnapshot(
        Instant asOf,
        String sourceSystem,
        List<Plant> plants,
        List<Supplier> suppliers,
        List<Part> parts,
        List<StockPosition> stock,
        List<PurchaseOrderLine> openPurchaseOrders,
        List<DemandRequirement> demand) {

    public SupplyChainSnapshot {
        plants = List.copyOf(plants);
        suppliers = List.copyOf(suppliers);
        parts = List.copyOf(parts);
        stock = List.copyOf(stock);
        openPurchaseOrders = List.copyOf(openPurchaseOrders);
        demand = List.copyOf(demand);
    }
}
