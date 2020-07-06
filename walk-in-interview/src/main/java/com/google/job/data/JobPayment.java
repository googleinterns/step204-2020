package com.google.job.data;

/** Class that represents the payment details of a job. */
public final class JobPayment {
    private float min;
    private float max;
    private Frequency frequency;

    // For serialization
    public JobPayment() {

    }

    public JobPayment(float min, float max, Frequency frequency) {
        this.min = min;
        this.max = max;
        this.frequency = frequency;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
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
}
