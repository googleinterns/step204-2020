package com.google.job.data;

/** Enumeration for duration of a job. */
public enum JobDuration {
    ONE_WEEK("ONE_WEEK"),
    TWO_WEEKS("TWO_WEEKS"),
    ONE_MONTH("ONE_MONTH"),
    SIX_MONTHS("SIX_MONTHS"),
    ONE_YEAR("ONE_YEAR"),
    OTHER("OTHER");

    private final String duration;

    JobDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Gets the String id matching the enum value.
     *
     * @return String id matching the enum value.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Gets the name of the enum value.
     *
     * @return Name of the enum value.
     */
    public String getDurationId() {
        return this.name();
    }

    /**
     * Returns the duration enum matching the provided id.
     *
     * @param id String id matching the enum value.
     * @return Duration enum matching the provided id.
     * @throws IllegalArgumentException If a duration cannot be found for provided id.
     */
    public static JobDuration getFromId(String id) throws IllegalArgumentException {
        return JobDuration.valueOf(id);
    }
}
