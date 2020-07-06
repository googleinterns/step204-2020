package com.google.job.data;

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
}
