package com.example.myapplication_login_test;

import java.io.Serializable;

public class VolunteerList implements Serializable {
    private Long id;
    private String volunteerName;
    private int volunteerHour;
    private int volunteerPersons;

    // ... (다른 필드 및 생성자, getter, setter 등 필요한 코드 추가)

    public VolunteerList() {
        // 기본 생성자
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public int getVolunteerHour() {
        return volunteerHour;
    }

    public void setVolunteerHour(int volunteerHour) {
        this.volunteerHour = volunteerHour;
    }

    public int getVolunteerPersons() {
        return volunteerPersons;
    }

    public void setVolunteerPersons(int volunteerPersons) {
        this.volunteerPersons = volunteerPersons;
    }

    // ... (다른 필드 및 생성자, getter, setter 등 필요한 코드 추가)
}