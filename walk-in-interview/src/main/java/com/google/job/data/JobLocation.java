package com.google.job.data;

/** Class that represents the location of a job. */
public final class JobLocation {
    private String address;
    private double lat;
    private double lon;

    // For serialization
    public JobLocation() {

    }

    public JobLocation(String address, double lat, double lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobLocation that = (JobLocation) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0 &&
                address.equals(that.address);
    }
}
