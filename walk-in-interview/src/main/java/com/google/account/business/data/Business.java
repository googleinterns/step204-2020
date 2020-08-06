package com.google.account.business.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

/** Class for a business account. */
public final class Business {
    private final String name;
    private final List<String> jobs; // List of jobId

    // For serialization
    public Business() {
        this(/* name= */ "dummy", ImmutableList.of());
    }

    public Business(String name, List<String> jobs) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Company name should be an non-empty string");
        }

        this.name = name;
        this.jobs = ImmutableList.copyOf(jobs);
    }

    /** Returns the company name. */
    public String getName() {
        return name;
    }

    /** Returns all the job posts made by this business account. */
    public List<String> getJobs() {
        return jobs;
    }

    @Override
    public String toString() {
        return String.format("Business{name='%s', jobs=%s}", name, jobs);
    }
}
