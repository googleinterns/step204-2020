package com.google.job.data;

import java.util.Collection;
import java.util.Optional;

/** Class for a job post. */
public final class Job {
    private static final String DUMMY = "dummy";

    private String jobId;
    // private long businessAccountId; // account is not involved so far
    private JobStatus jobStatus;
    private String jobTitle;
    private JobLocation jobLocation;
    private String jobDescription;
    private JobPayment jobPay;
    private Collection<String> requirements;
    private JobPostExpiry postExpiry;
    private Optional<Duration> jobDuration;

    private Job(String jobId, JobStatus jobStatus, String jobTitle,
                JobLocation jobLocation, String jobDescription,
                JobPayment jobPayment, Collection<String> requirements,
                JobPostExpiry postExpiry, Optional<Duration> jobDuration) {
        this.jobId = jobId;
        // this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPay = jobPayment;
        this.requirements = requirements;
        this.postExpiry = postExpiry;
        this.jobDuration = jobDuration;
    }

    private void validateParameters(String jobTitle, String jobDescription) throws IllegalArgumentException {
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
               JobLocation jobLocation, String jobDescription,
               JobPayment jobPayment, Collection<String> requirements,
               JobPostExpiry jobExpiry, Optional<Duration> jobDuration) throws IllegalArgumentException {
        validateParameters(jobTitle, jobDescription);

        this.jobId = DUMMY;
        // this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPay = jobPayment;
        this.requirements = requirements;
        this.postExpiry = jobExpiry;
        this.jobDuration = jobDuration;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobId() {
        return jobId;
    }

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

    public JobLocation getJobLocation() {
        return jobLocation;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public JobPayment getJobPay() {
        return jobPay;
    }

    public Collection<String> getRequirements() {
        return requirements;
    }

    public JobPostExpiry getPostExpiry() {
        return postExpiry;
    }

    public Optional<Duration> getJobDuration() {
        return jobDuration;
    }
}
