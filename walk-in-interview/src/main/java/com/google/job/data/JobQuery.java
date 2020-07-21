package com.google.job.data;

import javax.annotation.Nullable;

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
        assert minLimit >= 0;
        this.minLimit = minLimit;
        return this;
    }

    public JobQuery setMaxLimit(int maxLimit) {
        assert maxLimit > 0 && maxLimit >= this.minLimit;
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
        assert pageSize >= 1;
        this.pageSize = pageSize;
        return this;
    }

    public JobQuery setPageIndex(int pageIndex) {
        assert pageIndex >= 0;
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
