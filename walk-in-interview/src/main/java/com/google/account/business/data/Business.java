package com.google.account.business.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Business {
    private final String name;
    private final List<String> jobs; // A list of id belonging to job posts made by this business

    // For serialization
    public Business() {
        this("dummy", ImmutableList.of());
    }

    public Business(String name, List<String> jobs) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Company name should be an non-empty string");
        }

        this.name = name;
        this.jobs = ImmutableList.copyOf(jobs);
    }
}
