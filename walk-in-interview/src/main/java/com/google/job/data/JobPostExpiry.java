package com.google.job.data;

import java.time.LocalDate;

/**
 * Class that represents the expire date of the job post.
 * This class can be serialized in cloud firestore.
 */
public final class JobPostExpiry {
    private LocalDate jobPostExpiry;

    // For serialization
    public JobPostExpiry() {

    }

    public JobPostExpiry(String jobPostExpiryStr) {
        this.jobPostExpiry = LocalDate.parse(jobPostExpiryStr);
    }

    public LocalDate getJobPostExpiry() {
        return this.jobPostExpiry;
    }

    @Override
    public boolean equals(Object obj) {
        JobPostExpiry other = (JobPostExpiry) obj;

        if (!this.jobPostExpiry.isEqual(other.jobPostExpiry)) {
            return false;
        }

        return true;
    }
}
