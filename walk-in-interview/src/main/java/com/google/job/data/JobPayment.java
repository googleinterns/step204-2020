package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private final int min;
    private final int max;
    private final PaymentFrequency paymentFrequency;

    private int hashCode;

    private void validateParameter(float min, float max) throws IllegalArgumentException {
        if (min < 0) {
            throw new IllegalArgumentException("\"min\" should not be negative");
        }

        if (max < min) {
            throw new IllegalArgumentException("\"max\" should not be less than \"min\"");
        }
    }

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

    /**
     * Gets the lower limit of the payment.
     *
     * @return Lower limit of the payment.
     */
    public float getMin() {
        return min;
    }

    /**
     * Gets the upper limit of the payment.
     *
     * @return Upper limit of the payment.
     */
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
        return Float.compare(that.min, min) == 0 &&
                Float.compare(that.max, max) == 0 &&
                paymentFrequency == that.paymentFrequency;
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
        return "JobPayment{" +
                "min=" + min +
                ", max=" + max +
                ", frequency=" + paymentFrequency +
                '}';
    }
}
