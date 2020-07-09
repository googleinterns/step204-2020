package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private final int min;
    private final int max;
    private final PaymentFrequency paymentFrequency;

    private int hashCode;

    // For serialization
    public JobPayment() {
        this.min = 0;
        this.max = 0;
        this.paymentFrequency = PaymentFrequency.HOURLY;
    }

    public JobPayment(int min, int max, PaymentFrequency paymentFrequency) {
        validateParameter(min, max);

        this.min = min;
        this.max = max;
        this.paymentFrequency = paymentFrequency;
    }

    /** Returns the lower limit of the payment, never negative. */
    public float getMin() {
        return min;
    }

    /** Returns the upper limit of the payment, not less than the lower limit. */
    public float getMax() {
        return max;
    }

    /**
     * Gets the payment frequency.
     *
     * @return Payment frequency.
     */
    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPayment that = (JobPayment) o;
        return this.min == that.min && this.max == that.max && paymentFrequency.equals(that.paymentFrequency);
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int c = ((Integer)min).hashCode();
        hashCode = 31 * hashCode + c;

        c = ((Integer)max).hashCode();
        hashCode = 31 * hashCode + c;

        c = paymentFrequency.hashCode();
        hashCode = 31 * hashCode + c;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobPayment{min=%d, max=%d, paymentFrequency=%s}",
                min, max, paymentFrequency.name());
    }

    private static void validateParameter(float min, float max) throws IllegalArgumentException {
        if (min < 0) {
            throw new IllegalArgumentException("\"min\" should not be negative");
        }

        if (max < min) {
            throw new IllegalArgumentException("\"max\" should not be less than \"min\"");
        }
    }
}
