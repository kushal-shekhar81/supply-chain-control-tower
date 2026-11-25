package io.scct.model;

/** The two industries this control tower covers. */
public enum Industry {
    /** Tier-1 auto supplier / OEM: high volume, JIT / JIS, line-stop risk. */
    AUTOMOTIVE,
    /** Aerospace MRO: low volume, long lead times, AOG (aircraft-on-ground) risk. */
    AEROSPACE
}
