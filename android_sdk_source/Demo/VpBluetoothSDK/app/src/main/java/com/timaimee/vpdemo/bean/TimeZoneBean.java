package com.timaimee.vpdemo.bean;

import java.io.Serializable;

/**
 * @author KYM
 * on 2024/4/10
 */
public class TimeZoneBean implements Serializable {
    private String abbreviation;
    private String cityName;
    private String originalTimeZoneName;
    private String shortGenericTimeZoneName;
    private int id;

    public TimeZoneBean() {
    }

    public TimeZoneBean(String abbreviation, String cityName, String originalTimeZoneName, String shortGenericTimeZoneName) {
        this.abbreviation = abbreviation;
        this.cityName = cityName;
        this.originalTimeZoneName = originalTimeZoneName;
        this.shortGenericTimeZoneName = shortGenericTimeZoneName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOriginalTimeZoneName() {
        return originalTimeZoneName;
    }

    public void setOriginalTimeZoneName(String originalTimeZoneName) {
        this.originalTimeZoneName = originalTimeZoneName;
    }

    public String getShortGenericTimeZoneName() {
        return shortGenericTimeZoneName;
    }

    public void setShortGenericTimeZoneName(String shortGenericTimeZoneName) {
        this.shortGenericTimeZoneName = shortGenericTimeZoneName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getT15Min() {
        if (abbreviation.equals("GMT")) {
            return 0;
        } else {
            if (abbreviation.contains("+")) {
                String timeStr = abbreviation.replace("GMT", "").replace("+", "").trim().toString();
                String[] dat = timeStr.split(":");
                int hour = Integer.parseInt(dat[0].toString());
                int minute = Integer.parseInt(dat[1].toString());
                return (hour * 60 + minute) / 15;
            } else {
                String timeStr = abbreviation.replace("GMT", "").replace("-", "").trim().toString();
                String[] dat = timeStr.split(":");
                int hour = Integer.parseInt(dat[0].toString());
                int minute = Integer.parseInt(dat[1].toString());
                return -(hour * 60 + minute) / 15;
            }
        }
    }
}
