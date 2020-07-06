package com.google.job.data;

import java.util.Collection;
import java.util.Date;
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
    private JobPayment jobPayment;
    private Collection<String> requirements;
    private Date jobExpiry;
    private Optional<Duration> jobDuration;

    private Job(String jobId, JobStatus jobStatus, String jobTitle,
                JobLocation jobLocation, String jobDescription,
                JobPayment jobPayment, Collection<String> requirements,
                Date jobExpiry, Optional<Duration> jobDuration) {
        this.jobId = jobId;
        // this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPayment = jobPayment;
        this.requirements = requirements;
        this.jobExpiry = jobExpiry;
        this.jobDuration = jobDuration;
    }

    // No-argument constructor is needed to deserialize object when interacting with cloud firestore.
    public Job() {

    }

    public Job(JobStatus jobStatus, String jobTitle,
               JobLocation jobLocation, String jobDescription,
               JobPayment jobPayment, Collection<String> requirements,
               Date jobExpiry, Optional<Duration> jobDuration) {
        this.jobId = DUMMY;
        // this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPayment = jobPayment;
        this.requirements = requirements;
        this.jobExpiry = jobExpiry;
        this.jobDuration = jobDuration;
    }

    public String getJobId() {
        return jobId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
//    public Job(String jobTitle) throws IllegalArgumentException {
//        if (jobTitle.isEmpty()) {
//            throw new IllegalArgumentException("Empty job title provided");
//        }
//        this.jobTitle = jobTitle;
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

    public JobPayment getJobPayment() {
        return jobPayment;
    }

    public Collection<String> getRequirements() {
        return requirements;
    }

    public Date getJobExpiry() {
        return jobExpiry;
    }

    public Optional<Duration> getJobDuration() {
        return jobDuration;
    }
}
