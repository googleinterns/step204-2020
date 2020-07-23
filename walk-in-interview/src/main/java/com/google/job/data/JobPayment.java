package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private final int min;
    private final int max;
    private final PaymentFrequency paymentFrequency;
    private final long annualMax;

    private static final int HOURS_PER_YEAR = 8760;
    /* Note that is an approximate value */
    private static final int WEEKS_PER_YEAR = 52;
    private static final int MONTHS_PER_YEAR = 12;

    private volatile int hashCode;

    // For serialization
    public JobPayment() {
        this(/* min= */0, /* max= */0, PaymentFrequency.HOURLY, /* annualMax= */ 0);
    }

    public JobPayment(int min, int max, PaymentFrequency paymentFrequency, long annualMax) throws IllegalArgumentException {
        if (min < 0) {
            throw new IllegalArgumentException("\"min\" should not be negative");
        }

        if (max < min) {
            throw new IllegalArgumentException("\"max\" should not be less than \"min\"");
        }

        long expectedAnnualMax = findExpectedAnnualMax(max, paymentFrequency);

        if (annualMax != expectedAnnualMax) {
            throw new IllegalArgumentException("annualMax was : " + annualMax + ", but should be: " + expectedAnnualMax);
        }

        this.min = min;
        this.max = max;
        this.paymentFrequency = paymentFrequency;
        this.annualMax = annualMax;
    }

    /** Returns the lower limit of the payment, never negative. */
    public int getMin() {
        return min;
    }

    /** Returns the upper limit of the payment, not less than the lower limit. */
    public int getMax() {
        return max;
    }

    /** Returns the payment frequency. */
    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    /** Returns the annual pay given the upper limit of the payment and its frequency */
    public long getAnnualMax() {
        return annualMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPayment that = (JobPayment) o;
        return this.min == that.min && this.max == that.max && paymentFrequency.equals(that.paymentFrequency) &&
                this.annualMax == that.annualMax;
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int result = 0;

        int c = ((Integer)min).hashCode();
        result = 31 * result + c;

        c = ((Integer)max).hashCode();
        result = 31 * result + c;

        c = paymentFrequency.hashCode();
        result = 31 * result + c;

        c = ((Long) annualMax).hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobPayment{min=%d, max=%d, paymentFrequency=%s, annualMax=%d}",
                min, max, paymentFrequency.name(), annualMax);
    }

    /**
     * Calculates the annual max based on max pay and pay freuquency.
     */
    private static long findExpectedAnnualMax(int max, PaymentFrequency paymentFrequency) {
        switch(paymentFrequency) {
            case HOURLY:
                return max * HOURS_PER_YEAR;
            case WEEKLY:
                return max * WEEKS_PER_YEAR;
            case MONTHLY:
                return max * MONTHS_PER_YEAR;
            case YEARLY:
                return max;
        }
        
        throw new IllegalArgumentException("invalid paymentFrequency: " + paymentFrequency);
    }
}
