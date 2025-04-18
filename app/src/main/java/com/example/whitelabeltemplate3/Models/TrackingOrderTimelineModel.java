package com.example.whitelabeltemplate3.Models;

public class TrackingOrderTimelineModel {
    private String stageName;
    private String status;
    private int markerDrawable;  // Drawable resource for marker (optional)

    public TrackingOrderTimelineModel(String stageName, String status, int markerDrawable) {
        this.stageName = stageName;
        this.status = status;
        this.markerDrawable = markerDrawable;
    }

    public String getStageName() {
        return stageName;
    }

    public String getStatus() {
        return status;
    }

    public int getMarkerDrawable() {
        return markerDrawable;
    }
}