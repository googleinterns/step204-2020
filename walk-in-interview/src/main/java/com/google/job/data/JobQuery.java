package com.google.job.data;

/** Class for the job listings query using builder pattern. */
public final class JobQuery {
    private int minLimit = 0;
    private int maxLimit = Integer.MAX_VALUE;
    private SingaporeRegion region = SingaporeRegion.ENTIRE;
    private Filter sortBy = Filter.SALARY;
    private Order order = Order.DESCENDING;
    private int pageSize = 100;
    private int pageIndex = 0;

    public JobQuery() {}	

    public JobQuery setMinLimit(int minLimit) {
        if (minLimit < 0) {
            throw new IllegalArgumentException("minLimit should not be negative");
        }

        this.minLimit = minLimit;
        return this;
    }

    public JobQuery setMaxLimit(int maxLimit) {
        if (maxLimit <= 0 || maxLimit < this.minLimit) {
            throw new IllegalArgumentException("maxLimit should not be negative or zero or less than minLimit");
        }

        this.maxLimit = maxLimit;
        return this;
    }

    public JobQuery setRegion(SingaporeRegion region) {
        this.region = region;
        return this;
    }

    public JobQuery setSortBy(Filter sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public JobQuery setOrder(Order order) {
        this.order = order;
        return this;
    }

    public JobQuery setPageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize should not be less than 1");
        }

        this.pageSize = pageSize;
        return this;
    }

    public JobQuery setPageIndex(int pageIndex) {
        if (pageIndex < 0) {
            throw new IllegalArgumentException("pageIndex should not be less than 0");
        }

        this.pageIndex = pageIndex;
        return this;
    }

    /** Returns the lower limit for the filter. */
    public int getMinLimit() {
        return minLimit;
    }

    /** Returns the upper limit for the filter. */
    public int getMaxLimit() {
        return maxLimit;
    }

    /** Returns the region in Singapore. */
    public SingaporeRegion getRegion() {
        return region;
    }

    /** Returns the sorting. */
    public Filter getSortBy() {
        return sortBy;
    }

    /** Returns the ordering of the sorting. */
    public Order getOrder() {
        return order;
    }

    /** Returns the page size. */
    public int getPageSize() {
        return pageSize;
    }

    /** Returns a page number (or index). */
    public int getPageIndex() {
        return pageIndex;
    }
}
