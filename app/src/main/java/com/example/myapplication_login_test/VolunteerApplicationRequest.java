package com.example.myapplication_login_test;

public class VolunteerApplicationRequest {
    private Long volunteerFormId;
    private String applicantUsername;
    public Long getVolunteerFormId() {
        return volunteerFormId;
    }

    public String getApplicantUsername() {
        return applicantUsername;
    }

    public void setApplicantUsername(String applicantUsername) {
        this.applicantUsername = applicantUsername;
    }

    public void setVolunteerFormId(Long volunteerFormId) {
        this.volunteerFormId = volunteerFormId;
    }
}
