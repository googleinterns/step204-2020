package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private int min;
    private int max;
    private PaymentFrequency paymentFrequency;

    private void validateParameter(float min, float max) throws IllegalArgumentException {
        if (min < 0) {
            throw new IllegalArgumentException("\"min\" should not be negative");
        }

        if (max < min) {
            throw new IllegalArgumentException("\"max\" should not be less than \"min\"");
        }
    }

    // For serialization
    public JobPayment() {}

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
        int result = 0;

        int c = ((Integer)min).hashCode();
        result = 31 * result + c;

        c = ((Integer)max).hashCode();
        result = 31 * result + c;

        c = paymentFrequency.hashCode();
        result = 31 * result + c;

        return result;
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
