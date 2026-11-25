package io.scct.synthetic;

import io.scct.model.Criticality;
import io.scct.model.DemandRequirement;
import io.scct.model.Industry;
import io.scct.model.Part;
import io.scct.model.Plant;
import io.scct.model.PurchaseOrderLine;
import io.scct.model.StockPosition;
import io.scct.model.Supplier;
import io.scct.model.SupplyChainSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Builds SAP-like data for two fictional companies:
 * <ul>
 *   <li><b>Aurora Mobility</b> - a Tier-1 automotive supplier (brake systems, EV modules)
 *       with plants in Pune (IN) and Saltillo (MX).</li>
 *   <li><b>Stratos Aero</b> - an aerospace MRO provider with hubs in Hyderabad (IN)
 *       and Toulouse (FR).</li>
 * </ul>
 *
 * <p>The same seed always gives the same data, so demos and tests are repeatable. The data is
 * shaped to contain the problems a control tower must catch: single-sourced parts, stock below
 * safety stock, overdue purchase orders and an AOG (aircraft-on-ground) request.
 */
public final class SyntheticDataGenerator {

    public static final long DEFAULT_SEED = 42L;

    private final Random random;

    public SyntheticDataGenerator() {
        this(DEFAULT_SEED);
    }

    public SyntheticDataGenerator(long seed) {
        this.random = new Random(seed);
    }

    public SupplyChainSnapshot generate(LocalDate today, Instant asOf) {
        List<Plant> plants = plants();
        List<Supplier> suppliers = suppliers();
        List<Part> parts = parts();

        List<StockPosition> stock = new ArrayList<>();
        List<PurchaseOrderLine> purchaseOrders = new ArrayList<>();
        List<DemandRequirement> demand = new ArrayList<>();
        long[] nextPoNumber = {4500000001L}; // SAP's default PO number range starts with 45

        for (Part part : parts) {
            for (Plant plant : plants) {
                if (plant.industry() != part.industry()) {
                    continue;
                }
                double dailyUsage = DAILY_USAGE.get(part.partId())
                        * (plant.plantId().endsWith("1") ? 1.0 : 0.6);
                stock.add(stockFor(plant, part, dailyUsage));
                demand.addAll(demandFor(plant, part, dailyUsage, today));
                purchaseOrders.addAll(purchaseOrdersFor(plant, part, dailyUsage, today, nextPoNumber));
            }
        }

        // One AOG: an airline's aircraft is grounded in Toulouse waiting for a landing gear actuator.
        demand.add(new DemandRequirement("SA02", "X-2002", today, BigDecimal.ONE,
                DemandRequirement.Source.AOG));

        return new SupplyChainSnapshot(asOf, "SYNTHETIC", plants, suppliers, parts,
                stock, purchaseOrders, demand);
    }

    // ---------------------------------------------------------------------------------------
    // Master data (in SAP: T001W, LFA1, MARA/MARC, EORD)
    // ---------------------------------------------------------------------------------------

    static List<Plant> plants() {
        return List.of(
                new Plant("AM01", "Aurora Mobility - Pune", "AM10", Industry.AUTOMOTIVE, "IN"),
                new Plant("AM02", "Aurora Mobility - Saltillo", "AM20", Industry.AUTOMOTIVE, "MX"),
                new Plant("SA01", "Stratos Aero MRO - Hyderabad", "SA10", Industry.AEROSPACE, "IN"),
                new Plant("SA02", "Stratos Aero MRO - Toulouse", "SA20", Industry.AEROSPACE, "FR"));
    }

    static List<Supplier> suppliers() {
        return List.of(
                new Supplier("S100", "Formosa Microchips", "TW", 1, 0.82),
                new Supplier("S101", "Rhein Elektronik", "DE", 1, 0.93),
                new Supplier("S102", "Deccan Castings", "IN", 1, 0.88),
                new Supplier("S103", "Bajio Harness Systems", "MX", 1, 0.91),
                new Supplier("S104", "Nippon Cell Energy", "JP", 1, 0.95),
                new Supplier("S105", "Shenzhen Sensorics", "CN", 1, 0.86),
                new Supplier("S200", "Midlands Forge", "GB", 1, 0.78),
                new Supplier("S201", "Garonne Actuation", "FR", 1, 0.90),
                new Supplier("S202", "Lone Star Avionics", "US", 1, 0.92),
                new Supplier("S203", "Ti-Fast Aerospace", "US", 1, 0.84),
                new Supplier("S204", "Hydra Pumps Europe", "DE", 1, 0.89),
                // Tier-2: supplies wafers to S100. Invisible in most ERPs - see "Tier-N blindness".
                new Supplier("S300", "Island Wafer Foundry", "TW", 2, 0.80));
    }

    static List<Part> parts() {
        return List.of(
                // Automotive (Aurora Mobility)
                new Part("A-1001", "Brake ECU microcontroller", Industry.AUTOMOTIVE, "EA", Criticality.HIGH, 84, List.of("S100")),
                new Part("A-1002", "Brake caliper casting", Industry.AUTOMOTIVE, "EA", Criticality.HIGH, 35, List.of("S102")),
                new Part("A-1003", "Wiring harness - EV platform", Industry.AUTOMOTIVE, "EA", Criticality.MEDIUM, 21, List.of("S103", "S101")),
                new Part("A-1004", "Li-ion cell module 48V", Industry.AUTOMOTIVE, "EA", Criticality.HIGH, 56, List.of("S104", "S105")),
                new Part("A-1005", "Wheel-speed (ABS) sensor", Industry.AUTOMOTIVE, "EA", Criticality.MEDIUM, 28, List.of("S105", "S101")),
                new Part("A-1006", "Die-cast motor housing", Industry.AUTOMOTIVE, "EA", Criticality.LOW, 30, List.of("S102")),
                // Aerospace (Stratos Aero MRO)
                new Part("X-2001", "HPT turbine blade (forged)", Industry.AEROSPACE, "EA", Criticality.HIGH, 180, List.of("S200")),
                new Part("X-2002", "Main landing gear actuator", Industry.AEROSPACE, "EA", Criticality.HIGH, 120, List.of("S201")),
                new Part("X-2003", "Flight management computer (LRU)", Industry.AEROSPACE, "EA", Criticality.HIGH, 90, List.of("S202")),
                new Part("X-2004", "Titanium fastener kit", Industry.AEROSPACE, "KIT", Criticality.MEDIUM, 60, List.of("S203", "S201")),
                new Part("X-2005", "Engine-driven hydraulic pump", Industry.AEROSPACE, "EA", Criticality.HIGH, 100, List.of("S204")),
                new Part("X-2006", "Seal and O-ring kit", Industry.AEROSPACE, "KIT", Criticality.LOW, 30, List.of("S204", "S203")));
    }

    /** Average consumption per day at the main plant. Auto runs high volume; MRO runs low volume. */
    private static final Map<String, Double> DAILY_USAGE = Map.ofEntries(
            Map.entry("A-1001", 1200.0), Map.entry("A-1002", 2400.0), Map.entry("A-1003", 600.0),
            Map.entry("A-1004", 300.0), Map.entry("A-1005", 2400.0), Map.entry("A-1006", 300.0),
            Map.entry("X-2001", 0.8), Map.entry("X-2002", 0.1), Map.entry("X-2003", 0.15),
            Map.entry("X-2004", 2.0), Map.entry("X-2005", 0.1), Map.entry("X-2006", 4.0));

    // ---------------------------------------------------------------------------------------
    // Transactional data
    // ---------------------------------------------------------------------------------------

    private StockPosition stockFor(Plant plant, Part part, double dailyUsage) {
        // Safety stock: about 5 days of cover in auto, a couple of units in aero.
        double safety = part.industry() == Industry.AUTOMOTIVE ? dailyUsage * 5 : 2;
        // Days of cover on hand varies from 1 to 20, so some positions fall below safety stock.
        int coverDays = 1 + random.nextInt(20);
        double onHand = part.industry() == Industry.AUTOMOTIVE
                ? dailyUsage * coverDays
                : random.nextInt(6); // 0 to 5 units: MRO often holds very few expensive spares
        double inQualityInspection = onHand * (random.nextDouble() < 0.25 ? 0.1 : 0.0);
        return new StockPosition(plant.plantId(), part.partId(),
                qty(onHand), qty(inQualityInspection), BigDecimal.ZERO, qty(safety));
    }

    private List<DemandRequirement> demandFor(Plant plant, Part part, double dailyUsage, LocalDate today) {
        List<DemandRequirement> result = new ArrayList<>();
        if (part.industry() == Industry.AUTOMOTIVE) {
            // Production orders consume components every working day for the next 3 weeks.
            for (int day = 0; day < 21; day++) {
                LocalDate date = today.plusDays(day);
                if (date.getDayOfWeek().getValue() >= 6) {
                    continue; // no weekend shifts in this simple model
                }
                double variation = 0.85 + random.nextDouble() * 0.3; // +/-15% day to day
                result.add(new DemandRequirement(plant.plantId(), part.partId(), date,
                        qty(dailyUsage * variation), DemandRequirement.Source.PRODUCTION_ORDER));
            }
        } else {
            // MRO: a few planned work orders over the next 60 days, one or two units each.
            int workOrders = Math.max(1, (int) Math.round(dailyUsage * 60 / 1.5));
            for (int i = 0; i < Math.min(workOrders, 12); i++) {
                result.add(new DemandRequirement(plant.plantId(), part.partId(),
                        today.plusDays(random.nextInt(60)), qty(1 + random.nextInt(2)),
                        DemandRequirement.Source.MRO_WORK_ORDER));
            }
        }
        return result;
    }

    private List<PurchaseOrderLine> purchaseOrdersFor(Plant plant, Part part, double dailyUsage,
                                                      LocalDate today, long[] nextPoNumber) {
        List<PurchaseOrderLine> result = new ArrayList<>();
        int lines = 1 + random.nextInt(3);
        for (int i = 0; i < lines; i++) {
            String supplierId = part.approvedSupplierIds()
                    .get(random.nextInt(part.approvedSupplierIds().size()));
            // Some deliveries are already overdue (negative offset) - the classic expediting case.
            int offset = -5 + random.nextInt(Math.max(10, part.leadTimeDays() / 2));
            double quantity = part.industry() == Industry.AUTOMOTIVE
                    ? dailyUsage * (5 + random.nextInt(10))
                    : 1 + random.nextInt(3);
            PurchaseOrderLine.PoStatus status = offset < 0
                    ? PurchaseOrderLine.PoStatus.OPEN
                    : PurchaseOrderLine.PoStatus.values()[random.nextInt(3)];
            result.add(new PurchaseOrderLine(String.valueOf(nextPoNumber[0]++), 10,
                    supplierId, plant.plantId(), part.partId(), qty(quantity),
                    today.plusDays(offset), status));
        }
        return result;
    }

    private static BigDecimal qty(double value) {
        return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
    }
}
