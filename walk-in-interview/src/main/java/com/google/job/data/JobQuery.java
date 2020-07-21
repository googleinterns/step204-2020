package com.google.job.data;

import javax.annotation.Nullable;

/** Class for the job listings query. */
public final class JobQuery {
    private final int minLimit;
    private final int maxLimit;
    private final SingaporeRegion region;
    private final Filter sortBy;
    private final Order order;
    private final int pageSize;
    private final int pageIndex;

    private volatile int hashCode;

    private JobQuery(JobQueryBuilder jobQueryBuilder) {
        this.minLimit = jobQueryBuilder.minLimit;
        this.maxLimit = jobQueryBuilder.maxLimit;
        this.region = jobQueryBuilder.region;
        this.sortBy = jobQueryBuilder.sortBy;
        this.order = jobQueryBuilder.order;
        this.pageSize = jobQueryBuilder.pageSize;
        this.pageIndex = jobQueryBuilder.pageIndex;
    }

    // No-argument constructor is needed to deserialize object when interacting with cloud firestore.
    public JobQuery() {
        this.minLimit = 0;
        this.maxLimit = 0;
        this.region = SingaporeRegion.ENTIRE;
        this.sortBy = Filter.SALARY;
        this.order = Order.DESCENDING;
        this.pageSize = 0;
        this.pageIndex = 0;
    }

    /** Returns a builder by copying all the fields of an existing job query. */
    public JobQueryBuilder toBuilder() {
        JobQueryBuilder jobQueryBuilder = new JobQueryBuilder();

        jobQueryBuilder.minLimit = this.minLimit;
        jobQueryBuilder.maxLimit = this.maxLimit;
        jobQueryBuilder.region = this.region;
        jobQueryBuilder.sortBy = this.sortBy;
        jobQueryBuilder.order = this.order;
        jobQueryBuilder.pageSize = this.pageSize;
        jobQueryBuilder.pageIndex = this.pageIndex;

        return jobQueryBuilder;
    }

    /** Returns a builder. */
    public static JobQueryBuilder newBuilder() {
        return new JobQueryBuilder();
    }

    public static final class JobQueryBuilder {
        // Optional parameters - initialized to default values
        private int minLimit = 0;
        private int maxLimit = Integer.MAX_VALUE;
        private SingaporeRegion region = SingaporeRegion.ENTIRE;
        private Filter sortBy = Filter.SALARY;
        private Order order = Order.DESCENDING;
        private int pageSize = 20;
        private int pageIndex = 0;

        private JobQueryBuilder() {}

        public JobQueryBuilder setMinLimit(int minLimit) {
            this.minLimit = minLimit;
            return this;
        }

        public JobQueryBuilder setMaxLimit(int maxLimit) {
            this.maxLimit = maxLimit;
            return this;
        }

        public JobQueryBuilder setRegion(SingaporeRegion region) {
            this.region = region;
            return this;
        }

        public JobQueryBuilder setSortBy(Filter sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public JobQueryBuilder setOrder(Order order) {
            this.order = order;
            return this;
        }

        public JobQueryBuilder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public JobQueryBuilder setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public JobQuery build() {     
            return new JobQuery(this);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobQuery jobQuery = (JobQuery) o;
        return minLimit == jobQuery.minLimit &&
            maxLimit == jobQuery.maxLimit &&
            region.equals(jobQuery.region) &&
            sortBy.equals(jobQuery.sortBy) &&
            order.equals(jobQuery.order) &&
            pageSize == jobQuery.pageSize &&
            pageIndex == jobQuery.pageIndex;
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int result = 0;

        int c = ((Integer) minLimit).hashCode();
        result = 31 * result + c;

        c = ((Integer) maxLimit).hashCode();
        result = 31 * result + c;

        c = region.hashCode();
        result = 31 * result + c;

        c = sortBy.hashCode();
        result = 31 * result + c;

        c = order.hashCode();
        result = 31 * result + c;

        c = ((Integer) pageSize).hashCode();
        result = 31 * result + c;

        c = ((Integer) pageIndex).hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobQuery{minLimit=%d, maxLimit=%d, region=%s, sortBy=%s, "
                + "order=%s, pageSize=%d, pageIndex=%d}",
                minLimit, maxLimit, region, sortBy,
                order, pageSize, pageIndex);
    }
}
