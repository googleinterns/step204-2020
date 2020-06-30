package com.google.job.data;

public final class JobPayment {
    // (int min, int max, Frequency frequency)
    private float min;
    private float max;
    private Frequency frequency;

    public JobPayment(float min, float max, Frequency frequency) {
        this.min = min;
        this.max = max;
        this.frequency = frequency;
    }
}