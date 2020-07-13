package com.google.job.data;

/** Class that represents the location of a job or an applicant. */
public final class Location {
    // TODO(issue/23): Add a postalCode attribute to Location; address is only optional for applicant
    private final String address;
    private final String postalCode;
    private final double latitude;
    private final double longitude;

    private volatile int hashCode;

    // For serialization
    public Location() {
        this(/* address= */"", /* postalCode= */"dummy",
                /* latitude= */0, /* longitude= */0);
    }

    public Location(String address, String postalCode, double latitude, double longitude) {
        // TODO(issue/23): add check for user identity, it is only optional for applicant.
        this.address = address;

        if (postalCode.isEmpty()) {
            throw new IllegalArgumentException("Postal Code should be an non-empty string");
        }
        this.postalCode = postalCode;

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** Returns address description of a place. */
    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    /** Returns the latitude of the place. */
    public double getLatitude() {
        return latitude;
    }

    /** Returns the longitude of the place. */
    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                postalCode.equals(that.postalCode) &&
                address.equals(that.address);
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }

        int result = 0;

        int c = ((Double) latitude).hashCode();
        result = 31 * result + c;

        c = ((Double) longitude).hashCode();
        result = 31 * result + c;

        c = address.hashCode();
        result = 31 * result + c;

        c = postalCode.hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobLocation{address=%s, postalCode=%s, latitude=%f, longitude=%f}",
                address, postalCode, latitude, longitude);
    }
}
