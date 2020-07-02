package com.google.job.data;

/** Class for a job post. */
public final class Job {
    // TODO(issue/9);
    private final String jobTitle;

    public Job(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}
