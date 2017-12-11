package com.bt.nextgen.api.tracking.model;

public class TrackingKey {
    private Long trackingKey;

    public TrackingKey(Long trackingKey) {
        this.trackingKey = trackingKey;
    }

    public TrackingKey() {
    }

    public Long getTrackingKey() {
        return trackingKey;
    }

    public void setTrackingKey(Long trackingKey) {
        this.trackingKey = trackingKey;
    }

    @Override
    public String toString() {
        return "Tracking("+ getTrackingKey()+")";
    }
}
