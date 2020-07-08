package com.google.job.data;

/** Enumeration that represents the status of a job post. */
public enum JobStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    EXPIRED("EXPIRED");

    private final String status;

    JobStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the String id matching the enum value.
     *
     * @return String id matching the enum value.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the name of the enum value.
     *
     * @return Name of the enum value.
     */
    public String getStatusId() {
        return this.name();
    }

    /**
     * Returns the job status enum matching the provided id.
     *
     * @param id String id matching the enum value.
     * @return Status enum matching the provided id.
     * @throws IllegalArgumentException If a status cannot be found for provided id.
     */
    public static JobStatus getFromId(String id) throws IllegalArgumentException {
        return JobStatus.valueOf(id);
    }
}
