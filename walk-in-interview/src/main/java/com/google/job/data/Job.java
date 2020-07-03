package com.google.job.data;

/** Class for a job post. */
public final class Job {
    // TODO(issue/9): Add the feature to create a new job post
    private final String jobTitle;

    public Job(String jobTitle) throws IllegalArgumentException {
        if (jobTitle.isEmpty()) {
            throw new IllegalArgumentException("Empty job title provided");
        }
        this.jobTitle = jobTitle;
    }

    /**
     * Gets the job title of that post. Job title cannot be empty.
     *
     * @return Job title of the post.
     */
    public String getJobTitle() {
        return jobTitle;
    }
}
