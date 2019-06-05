package com.example.chris.finaltask.Class;

public class ConferenceInfo {

    private String name;
    private String date;
    private String city;
    private String sponsor;

    public ConferenceInfo(String name,String date,String city,String sponsor){
        this.name = name;
        this.date = date;
        this.city = city;
        this.sponsor = sponsor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public String getSponsor() {
        return sponsor;
    }
}
