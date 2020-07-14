package com.google.job.data;

/** Enumeration for filters that can be applied to the job listings. */
public enum Filter {
    DISTANCE("DISTANCE"),
    SALARY("SALARY");

    private final String filterId;

    Filter(String filterId) {
        this.filterId = filterId;
    }

    /**
     * Returns the stable id representing the filter. Can be stored in database.
     *
     * @return the stable id.
     */
    public String getFilterId() {
        return filterId;
    }

    /**
     * Returns the filter enum matching the provided id.
     *
     * @throws IllegalArgumentException If a status stable id cannot be found for provided id.
     */
    public static Filter getFromId(String id) throws IllegalArgumentException {
        for (Filter filter: values()){
            if (filter.getFilterId().equals(id)){
                return filter;
            }
        }

        throw new IllegalArgumentException("Invalid filter id: " + id);
    }
}
