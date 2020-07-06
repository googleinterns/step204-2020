package com.google.job.data;

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
}
