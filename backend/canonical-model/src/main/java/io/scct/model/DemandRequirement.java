package io.scct.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A need for a part on a date.
 *
 * <p>SAP mapping: dependent requirements from production orders (RESB), customer call-offs
 * from scheduling agreements (SD, VBEP), MRO work orders (PM, RESB). The planner's live view
 * of supply vs demand is transaction MD04 (stock/requirements list).
 */
public record DemandRequirement(
        String plantId,
        String partId,
        LocalDate requirementDate,
        BigDecimal quantity,
        Source source) {

    public enum Source {
        /** Component needed by a production order (auto assembly line). */
        PRODUCTION_ORDER,
        /** OEM call-off against a scheduling agreement (JIT / JIS). */
        CUSTOMER_CALLOFF,
        /** Part needed for a planned aircraft repair or overhaul. */
        MRO_WORK_ORDER,
        /** Aircraft on ground: an airline needs the part now. Highest urgency. */
        AOG
    }
}
