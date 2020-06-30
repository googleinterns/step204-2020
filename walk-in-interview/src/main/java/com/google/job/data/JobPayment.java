package com.google.job.data;

public final class JobPayment {
    // (int min, int max, Frequency frequency)
    private int min;
    private int max;
    private Frequency frequency;

    public JobPayment(int min, int max, Frequency frequency) {
        this.min = min;
        this.max = max;
        this.frequency = frequency;
    }
}
