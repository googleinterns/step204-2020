package com.google.account.business.data;

import com.google.account.UserType;
import com.google.common.collect.ImmutableList;
import com.google.job.data.Job;

import javax.annotation.Nullable;
import java.util.List;

/** Class for a business account. */
public final class Business {
    private final UserType userType;
    private final String name;
    private final List<String> jobs; // List of jobId

    // For serialization
    public Business() {
        this.userType = UserType.BUSINESS;
        this.name = "";
        this.jobs = ImmutableList.of();
    }

    private Business(BusinessBuilder businessBuilder) {
        this.userType = businessBuilder.userType;
        this.name = businessBuilder.name;
        this.jobs = businessBuilder.jobs;
    }

    /** Returns a builder by copying all the fields of an existing Job. */
    public BusinessBuilder toBuilder() {
        BusinessBuilder businessBuilder = new BusinessBuilder();

        businessBuilder.userType = this.userType;
        businessBuilder.name = this.name;
        businessBuilder.jobs = this.jobs;

        return businessBuilder;
    }

    /** Returns a builder. */
    public static BusinessBuilder newBuilder() {
        return new BusinessBuilder();
    }

    public static final class BusinessBuilder {
        // Initializes new business account with BUSINESS user type and empty job post made.
        private UserType userType = UserType.BUSINESS;
        private List<String> jobs = ImmutableList.of();

        // Required parameters
        @Nullable
        private String name;

        private BusinessBuilder() {}

        public BusinessBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public Business build() {
            if (userType != UserType.BUSINESS) {
                throw new IllegalArgumentException("Business account should have userType BUSINESS");
            }

            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Company Name should be an non-empty string");
            }

            if (jobs == null || !jobs.isEmpty()) {
                // Forces to be am empty list
                this.jobs = ImmutableList.of();
            }

            return new Business(this);
        }
    }

    /** Returns the user type. */
    public UserType getUserType() {
        return userType;
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
