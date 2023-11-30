package com.example.myapplication_login_test;

public class VolunteerApplication {
    private long id;
    private long volunteerFormId;
    private long infoId;
    private String applicationDate;
    private String status;

    public long getVolunteerFormId() {
        return volunteerFormId;
    }

    public void setVolunteerFormId(long volunteerFormId) {
        this.volunteerFormId = volunteerFormId;
    }

    public long getInfoId() {
        return infoId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
