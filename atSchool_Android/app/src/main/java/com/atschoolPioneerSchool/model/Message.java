package com.atschoolPioneerSchool.model;

import java.io.Serializable;

public class Message implements Serializable {
    private long id;
    private String date;
    private boolean read = false;
    private Student friend;
    private String snippet;

    public Message(long id, String date, boolean read, Student friend, String snippet) {
        this.id = id;
        this.date = date;
        this.read = read;
        this.friend = friend;
        this.snippet = snippet;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public boolean isRead() {
        return read;
    }

    public Student getFriend() {
        return friend;
    }

    public String getSnippet() {
        return snippet;
    }
}