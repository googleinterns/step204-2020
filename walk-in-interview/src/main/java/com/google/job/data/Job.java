package com.google.job.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

/** Class for a job post. */
public final class Job {
    private String jobId;
    // TODO(issue/25): merge the account stuff into job post.
    private JobStatus jobStatus;
    private String jobTitle;
    private Location location;
    private String jobDescription;
    private JobPayment jobPay;
    private List<String> requirements;
    private long postExpiry; // a timestamp
    private JobDuration jobDuration;

    private int hashCode;

    private Job(JobBuilder jobBuilder) {
        this.jobId = jobBuilder.jobId;
        this.jobStatus = jobBuilder.jobStatus;
        this.jobTitle = jobBuilder.jobTitle;
        this.location = jobBuilder.location;
        this.jobDescription = jobBuilder.jobDescription;
        this.jobPay = jobBuilder.jobPay;
        this.requirements = jobBuilder.requirements;
        this.postExpiry = jobBuilder.postExpiry;
        this.jobDuration = jobBuilder.jobDuration;
    }

    // No-argument constructor is needed to deserialize object when interacting with cloud firestore.
    public Job() {}

    public static JobBuilder newBuilder() {
        return new JobBuilder();
    }

    public static final class JobBuilder {
        private static final String DUMMY = "dummy";

        // Optional parameters - initialized to default values
        private String jobId = DUMMY;

        // TODO(issue/25): merge the account stuff into job post.

        private JobStatus jobStatus;
        private String jobTitle;
        private Location location;
        private String jobDescription;
        private JobPayment jobPay;
        private List<String> requirements;
        private long postExpiry; // a timestamp
        private JobDuration jobDuration;

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
            if (jobTitle.isEmpty()) {
                throw new IllegalArgumentException("Empty job title provided");
            }

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

        public JobBuilder setPostExpiry(long postExpiry) {
            this.postExpiry = postExpiry;
            return this;
        }

        public JobBuilder setJobDuration(JobDuration jobDuration) {
            this.jobDuration = jobDuration;
            return this;
        }

        public Job build() {
            return new Job(this);
        }
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
     * Gets the duration of the job itself.
     *
     * @return Duration of the job.
     */
    public JobDuration getJobDuration() {
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
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int c = jobId.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobStatus.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobTitle.hashCode();
        hashCode = 31 * hashCode + c;

        c = location.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobDescription.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobPay.hashCode();
        hashCode = 31 * hashCode + c;

        c = requirements.hashCode();
        hashCode = 31 * hashCode + c;

        c = ((Long)postExpiry).hashCode();
        hashCode = 31 * hashCode + c;

        c = jobDuration == null ? 0 : jobDuration.hashCode();
        hashCode = 31 * hashCode + c;

        return hashCode;
    }
}
