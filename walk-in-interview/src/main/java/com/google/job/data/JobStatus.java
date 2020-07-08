package com.google.job.data;

/** Enumeration that represents the status of a job post. */
public enum JobStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    EXPIRED("EXPIRED");

    private final String statusId;

    JobStatus(String status) {
        this.statusId = status;
    }

    /**
     * Gets the id string.
     *
     * @return Id string.
     */
    public String getStatusId() {
        return statusId;
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
