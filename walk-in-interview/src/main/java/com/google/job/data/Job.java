package com.google.job.data;

import com.google.common.collect.ImmutableList;

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

    private int hashCode;

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
        this.jobDuration = JobDuration.SIX_MONTHS;
    }

    public static JobBuilder newBuilder() {
        return new JobBuilder();
    }

    public static final class JobBuilder {
        // Optional parameters - initialized to default values
        private String jobId = "";

        // TODO(issue/25): merge the account stuff into job post.

        private JobStatus jobStatus;
        private String jobTitle;
        private Location location;
        private String jobDescription;
        private JobPayment jobPay;
        private List<String> requirements;
        private long postExpiryTimestamp;
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
            validateParameter(this.jobStatus, this.jobTitle, this.location,
                    this.jobDescription, this.jobPay, this.requirements, this.jobDuration);
            
            return new Job(this);
        }

        private static void validateParameter(JobStatus jobStatus, String jobTitle,
                                       Location location, String jobDescription,
                                       JobPayment  jobPay, List<String> requirements,
                                       JobDuration jobDuration) throws IllegalArgumentException {
            if (jobStatus == null) {
                throw new IllegalArgumentException("Job Status cannot be null");
            }

            if (jobTitle == null || jobTitle.isEmpty()) {
                throw new IllegalArgumentException("Job Title should be an non-empty string");
            }

            if (location == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }

            if (jobDescription == null) {
                throw new IllegalArgumentException("Job Description should be an non-empty string");
            }

            if (jobPay == null) {
                throw new IllegalArgumentException("Job Payment cannot be null");
            }

            if (requirements == null) {
                throw new IllegalArgumentException("Requirements cannot be null");
            }

            if (jobDuration == null) {
                throw new IllegalArgumentException("Job Duration cannot be null");
            }
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
    public Location getJobLocation() {
        return jobLocation;
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
    public long getPostExpiryTimestamp() {
        return postExpiryTimestamp;
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

        int c = jobId.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobStatus.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobTitle.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobLocation.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobDescription.hashCode();
        hashCode = 31 * hashCode + c;

        c = jobPay.hashCode();
        hashCode = 31 * hashCode + c;

        c = requirements.hashCode();
        hashCode = 31 * hashCode + c;

        c = ((Long)postExpiryTimestamp).hashCode();
        hashCode = 31 * hashCode + c;

        c = jobDuration == null ? 0 : jobDuration.hashCode();
        hashCode = 31 * hashCode + c;

        return hashCode;
    }
}
