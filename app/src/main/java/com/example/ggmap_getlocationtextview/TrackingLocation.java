package com.example.ggmap_getlocationtextview;

public class TrackingLocation {
    private String email, uid, token;
    private double lat, lng;

    public TrackingLocation(String email, String uid, String token, double lat, double lng) {
        this.email = email;
        this.uid = uid;
        this.token = token;
        this.lat = lat;
        this.lng = lng;
    }

    public TrackingLocation() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
