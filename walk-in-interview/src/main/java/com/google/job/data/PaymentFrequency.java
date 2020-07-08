package com.google.job.data;

/** Enumeration that represents the payment frequency. */
public enum PaymentFrequency {
    HOURLY("HOURLY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private final String frequencyId;

    PaymentFrequency(String frequencyId) {
        this.frequencyId = frequencyId;
    }

    /**
     * Gets the id string.
     *
     * @return Id string.
     */
    public String getFrequencyId() {
        return frequencyId;
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
