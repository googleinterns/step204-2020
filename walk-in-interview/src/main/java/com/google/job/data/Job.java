package com.google.job.data;

import java.util.Collection;
import java.util.Optional;

public final class Job {
    private static final String DUMMY = "dummy";

    private String jobId;
    private long businessAccountId;
    private JobStatus jobStatus;
    private String jobName;
    private JobLocation jobLocation;
    private String jobDescription;
    private JobPayment jobPayment;
    private Collection<String> requirements;
    private Duration postDuration;
    private Optional<Duration> jobDuration;

    private Job(String jobId, long businessAccountId,
               JobStatus jobStatus, String jobName,
               JobLocation jobLocation, String jobDescription,
               JobPayment jobPayment, Collection<String> requirements,
               Duration postDuration, Optional<Duration> jobDuration) {
        this.jobId = jobId;
        this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobName = jobName;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPayment = jobPayment;
        this.requirements = requirements;
        this.postDuration = postDuration;
        this.jobDuration = jobDuration;
    }

    public Job(long businessAccountId,
                JobStatus jobStatus, String jobName,
                JobLocation jobLocation, String jobDescription,
                JobPayment jobPayment, Collection<String> requirements,
                Duration postDuration, Optional<Duration> jobDuration) {
        this.jobId = DUMMY;
        this.businessAccountId = businessAccountId;
        this.jobStatus = jobStatus;
        this.jobName = jobName;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.jobPayment = jobPayment;
        this.requirements = requirements;
        this.postDuration = postDuration;
        this.jobDuration = jobDuration;
    }
}
