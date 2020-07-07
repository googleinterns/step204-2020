package com.google.job.data;

/** Class that represents the location of a job or an applicant. */
public final class Location {
    // TODO(issue/23): add "postalCode" and deal with optional address
    private String address;
    private double lat;
    private double lon;

    // For serialization
    public Location() {}

    public Location(String address, double lat, double lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Gets address description of a place.
     *
     * @return Address description.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the latitude of the place.
     *
     * @return Latitude of the place.
     */
    public double getLat() {
        return lat;
    }

    /**
     * Gets the longitude of the place.
     *
     * @return Longitude of the place.
     */
    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0 &&
                address.equals(that.address);
    }

    @Override
    public int hashCode() {
        int result = 0;

        int c = ((Double)lat).hashCode();
        result = 31 * result + c;

        c = ((Double)lon).hashCode();
        result = 31 * result + c;

        c = address.hashCode();
        result = 31 * result + c;

        return result;
    }

    @Override
    public String toString() {
        return "JobLocation{" +
                "address='" + address + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
