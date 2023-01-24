package com.example.secquralsetechno;

public class DatabaseClass {
    public DatabaseClass(String imageuri) {
        this.imageuri = imageuri;
    }

    public DatabaseClass() {
    }

    String imageuri;

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    boolean internetstatus;
    boolean chargingstatus;
    int charge;
    float locationLatitude;
    float locationLongitude;
    String timestamp;

    public DatabaseClass(boolean internetstatus, boolean chargingstatus, int charge, float locationLatitude, float locationLongitude, String timestamp) {
        this.internetstatus = internetstatus;
        this.chargingstatus = chargingstatus;
        this.charge = charge;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.timestamp = timestamp;
    }

    public boolean isInternetstatus() {
        return internetstatus;
    }

    public void setInternetstatus(boolean internetstatus) {
        this.internetstatus = internetstatus;
    }

    public boolean isChargingstatus() {
        return chargingstatus;
    }

    public void setChargingstatus(boolean chargingstatus) {
        this.chargingstatus = chargingstatus;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public float getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(float locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public float getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(float locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
