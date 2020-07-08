package com.google.job.data;

/** Enumeration that represents the payment frequency. */
public enum PaymentFrequency {
    HOURLY("HOURLY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private final String frequency;

    PaymentFrequency(String duration) {
        this.frequency = duration;
    }

    /**
     * Gets the String id matching the enum value.
     *
     * @return String id matching the enum value.
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Gets the name of the enum value.
     *
     * @return Name of the enum value.
     */
    public String getFrequencyId() {
        return this.name();
    }

    /**
     * Returns the payment frequency enum matching the provided id.
     *
     * @param id String id matching the enum value.
     * @return Frequency enum matching the provided id.
     * @throws IllegalArgumentException If a payment frequency cannot be found for provided id.
     */
    public static PaymentFrequency getFromId(String id) throws IllegalArgumentException {
        return PaymentFrequency.valueOf(id);
    }
}
