package com.atschoolPioneerSchool.model;

import java.io.Serializable;

public class Notif implements Serializable {
    private long id;
    private String date;
    private Student friend;
    private String content;

    public Notif(long id, String date, Student friend, String content) {
        this.id = id;
        this.date = date;
        this.friend = friend;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public Student getFriend() {
        return friend;
    }

    public String getContent() {
        return "<b>" + friend.getName() + "</b> " + content;
    }
}