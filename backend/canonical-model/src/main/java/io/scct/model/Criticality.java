package io.scct.model;

/**
 * How badly a missing part hurts. Planners usually combine ABC (value) with XYZ (demand
 * variability); here we keep one simple business view.
 */
public enum Criticality {
    /** A missing part stops a line (auto) or keeps an aircraft on the ground (aero). */
    HIGH,
    /** Causes rework, overtime or a delayed shipment. */
    MEDIUM,
    /** Annoying but absorbable. */
    LOW
}
