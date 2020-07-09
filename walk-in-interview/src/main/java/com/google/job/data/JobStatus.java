package com.google.job.data;

/** Enumeration that represents the status of a job post. */
public enum JobStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    EXPIRED("EXPIRED");

    private final String statusId;

    JobStatus(String statusId) {
        this.statusId = statusId;
    }

    /** Returns the stable id representing the job status. Can be stored in database. */
    public String getStatusId() {
        return statusId;
    }

    /**
     * Returns the job status enum matching the provided id.
     *
     * @throws IllegalArgumentException If a status stable id cannot be found for provided id.
     */
    public static JobStatus getFromId(String id) throws IllegalArgumentException {
        for (JobStatus status: values()){
            if (status.getStatusId().equals(id)){
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid job status id: " + id);
    }
}
