package com.example.myapplication_login_test;

import java.time.LocalDate;

public class VolunteerApplicationDTO {
    private Long id;
    private String status;
    private String applicationDate;
    private String volunteerFormTitle;

    public VolunteerApplicationDTO(Long id, String status, String applicationDate, String volunteerFormTitle) {
        this.id = id;
        this.status = status;
        this.applicationDate = applicationDate;
        this.volunteerFormTitle = volunteerFormTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getVolunteerFormTitle() {
        return volunteerFormTitle;
    }

    public void setVolunteerFormTitle(String volunteerFormTitle) {
        this.volunteerFormTitle = volunteerFormTitle;
    }
}
