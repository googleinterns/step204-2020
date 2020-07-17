package com.google.job.data;

/** Enumeration for the different regions of Singapore. */
public enum SingaporeRegion {
    ENTIRE("ENTIRE"),
    CENTRAL("CENTRAL"),
    WEST("WEST"),
    NORTH("NORTH"),
    EAST("EAST"),
    NORTH_EAST("NORTH_EAST");

    private final String regionId;

    SingaporeRegion(String regionId) {
        this.regionId = regionId;
    }

    /** Returns the stable id representing the region. Can be stored in database. */
    public String getRegionId() {
        return regionId;
    }

    /**
     * Returns the region enum matching the provided id.
     *
     * @throws IllegalArgumentException If a status stable id cannot be found for provided id.
     */
    public static SingaporeRegion getFromId(String id) throws IllegalArgumentException {
        for (SingaporeRegion region: values()){
            if (region.getRegionId().equals(id)){
                return region;
            }
        }

        throw new IllegalArgumentException("Invalid region id: " + id);
    }
}
