package com.example.myapplication_login_test;

import java.io.Serializable;

public class VolunteerEvent implements Serializable {
    private  String volunteerName;
    private  int volunteerHour;
    private  int volunteerPersons;

    public VolunteerEvent(String volunteerName, int volunteerHour, int volunteerPersons) {
        this.volunteerName = volunteerName;
        this.volunteerHour = volunteerHour;
        this.volunteerPersons = volunteerPersons;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public int getVolunteerHour() {
        return volunteerHour;
    }

    public int getVolunteerPersons() {
        return volunteerPersons;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public void setVolunteerHour(int volunteerHour) {
        this.volunteerHour = volunteerHour;
    }

    public void setVolunteerPersons(int volunteerPersons) {
        this.volunteerPersons = volunteerPersons;
    }

    public VolunteerEvent() {
    }
}
