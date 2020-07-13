package com.google.job.data;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

/** Class for a job post. */
public final class Job {
    private final String jobId;
    // TODO(issue/25): merge the account stuff into job post.
    private final JobStatus jobStatus;
    private final String jobTitle;
    private final Location jobLocation;
    private final String jobDescription;
    private final JobPayment jobPay;
    private final List<String> requirements;
    private final long postExpiryTimestamp;
    private final JobDuration jobDuration;

    private volatile int hashCode;

    private Job(JobBuilder jobBuilder) {
        this.jobId = jobBuilder.jobId;
        this.jobStatus = jobBuilder.jobStatus;
        this.jobTitle = jobBuilder.jobTitle;
        this.jobLocation = jobBuilder.location;
        this.jobDescription = jobBuilder.jobDescription;
        this.jobPay = jobBuilder.jobPay;
        this.requirements = jobBuilder.requirements;
        this.postExpiryTimestamp = jobBuilder.postExpiryTimestamp;
        this.jobDuration = jobBuilder.jobDuration;
    }

    // No-argument constructor is needed to deserialize object when interacting with cloud firestore.
    public Job() {
        this.jobId = "";
        this.jobStatus = JobStatus.ACTIVE;
        this.jobTitle = "";
        this.jobLocation = new Location();
        this.jobDescription = "";
        this.jobPay = new JobPayment();
        this.requirements = ImmutableList.of();
        this.postExpiryTimestamp = 0;
        this.jobDuration = JobDuration.OTHER;
    }

    public static JobBuilder newBuilder() {
        return new JobBuilder();
    }

    public static final class JobBuilder {
        // Optional parameters - initialized to default values
        private String jobId = "";
        private JobStatus jobStatus = JobStatus.ACTIVE;
        private List<String> requirements = ImmutableList.of();
        private JobDuration jobDuration = JobDuration.OTHER;
        private long postExpiryTimestamp = 0;

        // TODO(issue/25): merge the account stuff into job post.

        @Nullable
        private String jobTitle;

        @Nullable
        private Location location;

        @Nullable
        private String jobDescription;

        @Nullable
        private JobPayment jobPay;

        private JobBuilder() {}

        // TODO(issue/19): add more validation check

        public JobBuilder setJobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public JobBuilder setJobStatus(JobStatus jobStatus) {
            this.jobStatus = jobStatus;
            return this;
        }

        public JobBuilder setJobTitle(String jobTitle) throws IllegalArgumentException {
            this.jobTitle = jobTitle;
            return this;
        }

        public JobBuilder setLocation(Location location) {
            this.location = location;
            return this;
        }

        public JobBuilder setJobDescription(String jobDescription) throws IllegalArgumentException {
            if (jobDescription.isEmpty()) {
                throw new IllegalArgumentException("Empty job description provided");
            }

            this.jobDescription = jobDescription;
            return this;
        }

        public JobBuilder setJobPay(JobPayment jobPay) {
            this.jobPay = jobPay;
            return this;
        }

        public JobBuilder setRequirements(List<String> requirements) {
            this.requirements = ImmutableList.copyOf(requirements);
            return this;
        }

        public JobBuilder setPostExpiry(long postExpiryTimestamp) {
            this.postExpiryTimestamp = postExpiryTimestamp;
            return this;
        }

        public JobBuilder setJobDuration(JobDuration jobDuration) {
            this.jobDuration = jobDuration;
            return this;
        }

        public Job build() {
            if (jobTitle == null || jobTitle.isEmpty()) {
                throw new IllegalArgumentException("Job Title should be an non-empty string");
            }

            if (location == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }

            if (jobDescription == null || jobDescription.isEmpty()) {
                throw new IllegalArgumentException("Job Description should be an non-empty string");
            }

            if (jobPay == null) {
                throw new IllegalArgumentException("Job Payment cannot be null");
            }

            if (postExpiryTimestamp == 0) {
                throw new IllegalArgumentException("Timestamp cannot be 0. Please provide a valid timestamp");
            }
            
            return new Job(this);
        }
    }

    /** Returns the id for the job post. */
    public String getJobId() {
        return jobId;
    }

    /** Returns the status of the job post, either ACTIVE, DELETED, or EXPIRED. */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /** Returns the job title of that post. Job title cannot be empty. */
    public String getJobTitle() {
        return jobTitle;
    }

    /** Returns the location details of the job post. */
    public Location getJobLocation() {
        return jobLocation;
    }

    /** Returns the job description of that post. Job description cannot be empty. */
    public String getJobDescription() {
        return jobDescription;
    }

    /** Returns the payment details of the job post. */
    public JobPayment getJobPay() {
        return jobPay;
    }

    /** Returns a list of requirements of the job post. */
    public List<String> getRequirements() {
        return requirements;
    }

    /** Returns the date when the job post will expire. */
    public long getPostExpiryTimestamp() {
        return postExpiryTimestamp;
    }

    /** Returns the duration of the job itself. */
    public JobDuration getJobDuration() {
        return jobDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return postExpiryTimestamp == job.postExpiryTimestamp &&
                jobId.equals(job.jobId) &&
                jobStatus == job.jobStatus &&
                jobTitle.equals(job.jobTitle) &&
                jobLocation.equals(job.jobLocation) &&
                jobDescription.equals(job.jobDescription) &&
                jobPay.equals(job.jobPay) &&
                requirements.equals(job.requirements) &&
                jobDuration.equals(job.jobDuration);
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int result = 0;

        int c = jobId.hashCode();
        result = 31 * result + c;

        c = jobStatus.hashCode();
        result = 31 * result + c;

        c = jobTitle.hashCode();
        result = 31 * result + c;

        c = jobLocation.hashCode();
        result = 31 * result + c;

        c = jobDescription.hashCode();
        result = 31 * result + c;

        c = jobPay.hashCode();
        result = 31 * result + c;

        c = requirements.hashCode();
        result = 31 * result + c;

        c = ((Long)postExpiryTimestamp).hashCode();
        result = 31 * result + c;

        c = jobDuration == null ? 0 : jobDuration.hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }
}
