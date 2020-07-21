package com.google.job.data;

import java.util.*;
import org.apache.commons.lang3.Range;
import com.google.common.collect.ImmutableList;

/** Class that represents the details of a page to show jobs. */
public final class JobPage {
    private final List<Job> jobList;
    private final long totalCount;
    private final Range<Integer> range;

    private volatile int hashCode;

    // For serialization
    public JobPage() {
        this(/* jobList= */ ImmutableList.of(), /* totalCount= */0,
                Range.between(/* inclusive= */ 0, /* inclusive= */ 0));
    }

    public JobPage(List<Job> jobList, long totalCount, Range<Integer> range) {
        validateParameters(jobList, totalCount, range);

        this.jobList = jobList;
        this.totalCount = totalCount;
        this.range = range;
    }

    /** Returns list of jobs. */
    public List<Job> getJobList() {
        return jobList;
    }

    /**
     * Returns totalCount, which is the total number of jobs that fit the params.
     * e.g., viewing 30-70 out of 100 (then total count will be 100).
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Returns the range, which indicates which jobs we are seeing.
     * e.g., viewing 30-70 out of 100 (then range will be 30-70).
     */
    public Range<Integer> getRange() {
        return range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPage that = (JobPage) o;
        return this.jobList.equals(that.jobList) &&
                this.totalCount == that.totalCount &&
                this.range.equals(that.range);
    }

    /**
     * Must override hashCode() if equals() is being overriden.
     *
     * @return The hashcode.
     */
    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int result = 0;

        int c = jobList.hashCode();
        result = 31 * result + c;

        c = ((Long) totalCount).hashCode();
        result = 31 * result + c;

        c = range.hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobPage{jobList=%s, totalCount=%d, range=%s}",
                jobList, totalCount, range);
    }

    private static void validateParameters(List<Job> jobList, long totalCount, Range<Integer> range)
            throws IllegalArgumentException {
        if (jobList.size() < 0) {
            throw new IllegalArgumentException("jobList should not have negative size");
        }

        if (totalCount < 0 || totalCount < jobList.size()) {
            throw new IllegalArgumentException("totalCount should not be negative or less than jobList size");
        }

        if (range.getMinimum() < 0 || range.getMaximum() > totalCount ||
                range.getMaximum() < range.getMinimum()) {
            throw new IllegalArgumentException("range should not have invalid max/min values");
        }
    }
}
