package com.example.myapplication_login_test;

import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {
    private long id;
    private String name;
    private String username;
    private String phoneNumber;
    private String password;
    private String userType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User() {
    }

    public User(long id, String name, String username, String phoneNumber, String password, String userType) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.userType = userType;
    }

    public boolean hasChanges(User updatedUser) {
        // 변경된 값이 있는지 확인
        return !Objects.equals(this.username, updatedUser.username) ||
                !Objects.equals(this.name, updatedUser.name) ||
                !Objects.equals(this.phoneNumber, updatedUser.phoneNumber) ||
                !Objects.equals(this.password, updatedUser.password);
    }
    public User getChangedFields(User updatedUser) {
        User changedFieldsUser = new User();
        if (!Objects.equals(this.username, updatedUser.username)) {
            changedFieldsUser.setUsername(updatedUser.username);
        }

        if (!Objects.equals(this.name, updatedUser.name)) {
            changedFieldsUser.setName(updatedUser.name);
        }
        if (!Objects.equals(this.phoneNumber, updatedUser.phoneNumber)) {
            changedFieldsUser.setPhoneNumber(updatedUser.phoneNumber);
        }
        if (!Objects.equals(this.password, updatedUser.password)) {
            changedFieldsUser.setPassword(updatedUser.password);
        }
        return changedFieldsUser;
    }
}


