package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private float min;
    private float max;
    private Frequency frequency;

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

    public JobPayment(float min, float max, Frequency frequency) {
        validateParameter(min, max);

        this.min = min;
        this.max = max;
        this.frequency = frequency;
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
    public Frequency getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPayment that = (JobPayment) o;
        return Float.compare(that.min, min) == 0 &&
                Float.compare(that.max, max) == 0 &&
                frequency == that.frequency;
    }

    @Override
    public int hashCode() {
        int result = 0;

        int c = ((Float)min).hashCode();
        result = 31 * result + c;

        c = ((Float)max).hashCode();
        result = 31 * result + c;

        c = frequency.hashCode();
        result = 31 * result + c;

        return result;
    }

    @Override
    public String toString() {
        return "JobPayment{" +
                "min=" + min +
                ", max=" + max +
                ", frequency=" + frequency +
                '}';
    }
}
