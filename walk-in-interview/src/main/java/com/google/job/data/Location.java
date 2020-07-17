package com.google.job.data;

/** Class that represents the location of a job or an applicant. */
public final class Location {
    // TODO(issue/23): Add a postalCode attribute to Location; address is only optional for applicant
    private final String address;
    private final String postalCode;
    private final SingaporeRegion region;
    private final double latitude;
    private final double longitude;

    private volatile int hashCode;

    // For serialization
    public Location() {
        this(/* address= */"", /* postalCode= */"01dummy", /* region */ SingaporeRegion.CENTRAL,
                /* latitude= */0, /* longitude= */0);
    }

    public Location(String address, String postalCode, SingaporeRegion region, 
        double latitude, double longitude) {
        // TODO(issue/23): add check for user identity, it is only optional for applicant.
        if (postalCode.isEmpty()) {
            throw new IllegalArgumentException("Postal Code should be an non-empty string");
        }

        if (latitude < 0) {
            throw new IllegalArgumentException("Latitude should be non-negative");
        }

        if (longitude < 0) {
            throw new IllegalArgumentException("Longitude should be non-negative");
        }

        this.address = address;
        this.postalCode = postalCode;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** Returns address description of a place. */
    public String getAddress() {
        return address;
    }

    /** Returns postal code of a place. */
    public String getPostalCode() {
        return postalCode;
    }

    /** Returns the region. */
    public SingaporeRegion getRegion() {
        return region;
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
                address.equals(that.address) &&
                region.equals(that.region);
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

        c = region.hashCode();
        result = 31 * result + c;

        this.hashCode = result;

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("JobLocation{address=%s, postalCode=%s, region=%s, latitude=%f, longitude=%f}",
                address, postalCode, region.name(), latitude, longitude);
    }
}
