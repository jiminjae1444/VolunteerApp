package com.example.myapplication_login_test;

public class VolunteerForm {
    private long id;
    private String title;
    private String location;
    private String description;
    private String start_date;
    private String end_date;
    private int slots;
    private int recruitment_hours;
    private String priority;

    public VolunteerForm(long id, String title, String location, String description, String start_date, String end_date, int slots, int recruitment_hours, String priority) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.slots = slots;
        this.recruitment_hours = recruitment_hours;
        this.priority = priority;
    }

    public VolunteerForm() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public int getRecruitment_hours() {
        return recruitment_hours;
    }

    public void setRecruitment_hours(int recruitment_hours) {
        this.recruitment_hours = recruitment_hours;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
