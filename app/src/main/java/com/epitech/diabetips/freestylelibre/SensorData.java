package com.epitech.diabetips.freestylelibre;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

public class SensorData implements Serializable {
    public static final String ID = "id";
    public static final String START_DATE = "startDate";

    public static final long minSensorAgeInMinutes = 60; // data generated by the sensor in the first 60 minutes is not correct
    public static final long maxSensorAgeInMinutes = TimeUnit.DAYS.toMinutes(14); // data generated by the sensor after 14 days also has faults

    private String id;
    private long startDate = -1;

    public SensorData() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public SensorData(RawTagData rawTagData) {
        id = String.format(Locale.US, "sensor_%s", rawTagData.getTagId());
        startDate = rawTagData.getDate() - (rawTagData.getDate() % TimeUnit.MINUTES.toMillis(1))
                - TimeUnit.MINUTES.toMillis(rawTagData.getSensorAgeInMinutes());
    }

    public String toString() {
        return "SensorData [id=" + id + ", startDate="+ startDate + "]";
    }

    public SensorData(SensorData sensor) {
        this.id = sensor.id;
        this.startDate = sensor.getStartDate();
    }

    public String getTagId() {
        return id.substring(7);
    }

    public long getTimeLeft() {
        return max(0, startDate + TimeUnit.MINUTES.toMillis(maxSensorAgeInMinutes) - System.currentTimeMillis());
    }

    public long getStartDate() {
        return startDate;
    }

    public String getId() {
        return id;
    }
}