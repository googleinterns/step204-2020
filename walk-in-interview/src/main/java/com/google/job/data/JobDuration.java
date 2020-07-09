package com.google.job.data;

/** Enumeration for duration of a job. */
public enum JobDuration {
    ONE_WEEK("ONE_WEEK"),
    TWO_WEEKS("TWO_WEEKS"),
    ONE_MONTH("ONE_MONTH"),
    SIX_MONTHS("SIX_MONTHS"),
    ONE_YEAR("ONE_YEAR"),
    OTHER("OTHER");

    private final String durationId;

    JobDuration(String durationId) {
        this.durationId = durationId;
    }

    /** Returns the stable id representing the job duration. Can be stored in database. */
    public String getDurationId() {
        return durationId;
    }

    /**
     * Returns the job duration enum matching the provided id.
     *
     * @throws IllegalArgumentException If a status stable id cannot be found for provided id.
     */
    public static JobDuration getFromId(String id) throws IllegalArgumentException {
        for (JobDuration duration: values()){
            if (duration.getDurationId().equals(id)){
                return duration;
            }
        }

        throw new IllegalArgumentException("Invalid job status id: " + id);
    }
}
