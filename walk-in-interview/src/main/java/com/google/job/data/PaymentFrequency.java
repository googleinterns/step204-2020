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

    /** Returns the stable id representing the payment frequency. Can be stored in database. */
    public String getFrequencyId() {
        return frequencyId;
    }

    /**
     * Returns the payment frequency enum matching the provided id.
     *
     * @throws IllegalArgumentException If a frequency stable id cannot be found for provided id.
     */
    public static PaymentFrequency getFromId(String id) throws IllegalArgumentException {
        for (PaymentFrequency paymentFrequency: values()){
            if (paymentFrequency.getFrequencyId().equals(id)){
                return paymentFrequency;
            }
        }

        throw new IllegalArgumentException("Invalid payment frequency id: " + id);
    }
}
