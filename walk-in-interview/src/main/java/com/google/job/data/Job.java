package com.google.job.data;

import jdk.internal.jline.internal.Nullable;

import java.util.List;
import java.util.Optional;

/** Class for a job post. */
public final class Job {
    private static final String DUMMY = "dummy";

    private String jobId;
    // TODO(issue/25): merge the account stuff into job post.
    private JobStatus jobStatus;
    private String jobTitle;
    private Location location;
    private String jobDescription;
    private JobPayment jobPay;
    private List<String> requirements;
    private long postExpiry; // a timestamp
    @Nullable
    private JobDuration jobDuration;

    private void validateParameters(String jobTitle, String jobDescription) throws IllegalArgumentException {
        // TODO(issue/19): add more validation check
        if (jobTitle.isEmpty()) {
            throw new IllegalArgumentException("Empty job title provided");
        }

        if (jobDescription.isEmpty()) {
            throw new IllegalArgumentException("Empty job description provided");
        }
    }

    // No-argument constructor is needed to deserialize object when interacting with cloud firestore.
    public Job() {}

    public Job(String jobTitle, JobStatus jobStatus,
               Location location, String jobDescription,
               JobPayment jobPayment, List<String> requirements,
               long jobExpiry, Optional<JobDuration> jobDuration) throws IllegalArgumentException {
        validateParameters(jobTitle, jobDescription);

        // Assigns to it a dummy id first,
        // so later can reassign it with the firestore generated id
        // when retrieve a Job from database and display on the website
        this.jobId = DUMMY;
        this.jobStatus = jobStatus;
        this.jobTitle = jobTitle;
        this.location = location;
        this.jobDescription = jobDescription;
        this.jobPay = jobPayment;
        this.requirements = requirements;
        this.postExpiry = jobExpiry;

        // Cloud firestore cannot handle Optional, so simply store null into it.
        this.jobDuration = jobDuration.get();
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * Gets the id for the job post.
     *
     * @return Id of the job post.
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Gets the status of the job post, either ACTIVE, DELETED, or EXPIRED.
     *
     * @return Status of the job post.
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * Gets the job title of that post. Job title cannot be empty.
     *
     * @return Job title of the post.
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Gets the location details of the job post.
     *
     * @return Location details of the post.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the job description of that post. Job description cannot be empty.
     *
     * @return Job description of the post.
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Gets the payment details of the job post.
     *
     * @return Payment details of the post.
     */
    public JobPayment getJobPay() {
        return jobPay;
    }

    /**
     * Gets a list of requirements of the job post.
     *
     * @return Requirement list.
     */
    public List<String> getRequirements() {
        return requirements;
    }

    /**
     * Gets the date when the job post will expire.
     *
     * @return Expiry date of the post.
     */
    public long getPostExpiry() {
        return postExpiry;
    }

    /**
     * Gets the duration of the job itself. This field is optional, so it can be null.
     *
     * @return Duration of the job.
     */
    public @Nullable
    JobDuration getJobDuration() {
        return jobDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return postExpiry == job.postExpiry &&
                jobId.equals(job.jobId) &&
                jobStatus == job.jobStatus &&
                jobTitle.equals(job.jobTitle) &&
                location.equals(job.location) &&
                jobDescription.equals(job.jobDescription) &&
                jobPay.equals(job.jobPay) &&
                requirements.equals(job.requirements) &&
                jobDuration.equals(job.jobDuration);
    }

    @Override
    public int hashCode() {
        int result = 0;

        int c = jobId.hashCode();
        result = 31 * result + c;

        c = jobStatus.hashCode();
        result = 31 * result + c;

        c = jobTitle.hashCode();
        result = 31 * result + c;

        c = location.hashCode();
        result = 31 * result + c;

        c = jobDescription.hashCode();
        result = 31 * result + c;

        c = jobPay.hashCode();
        result = 31 * result + c;

        c = requirements.hashCode();
        result = 31 * result + c;

        c = ((Long)postExpiry).hashCode();
        result = 31 * result + c;

        c = jobDuration == null ? 0 : jobDuration.hashCode();
        result = 31 * result + c;

        return result;
    }
}
