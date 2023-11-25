package com.example.myapplication_login_test;

public class VolunteerForminfo {
    private String title;
    private String location;
    private String description;
    private String start_date;
    private String end_date;
    private String slots;
    private String recruitment_hours;

    public VolunteerForminfo(String title, String location, String description, String start_date, String end_date, String slots, String recruitment_hours, String priority) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.slots = slots;
        this.recruitment_hours = recruitment_hours;
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    private String priority;

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getRecruitment_hours() {
        return recruitment_hours;
    }

    public void setRecruitment_hours(String recruitment_hours) {
        this.recruitment_hours = recruitment_hours;
    }
}
