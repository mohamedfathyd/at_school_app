package com.atschoolPioneerSchool.model;

import java.io.Serializable;

public class MessageDetails implements Serializable {
    private long id;
    private String date;
    private Student friend;
    public String content;
    private boolean fromMe;


    public String ProgressText = "";
    public int ProgressBarLevel = 0;
    private boolean IsDocument = false;

    public String Attached_File_Name = "";
    public String Attached_File_Extension = "";

    public MessageDetails() {


    }

    public MessageDetails(long id, String date, Student friend, String content, boolean fromMe) {
        this.id = id;
        this.date = date;
        this.friend = friend;
        this.content = content;
        this.fromMe = fromMe;
    }

    public MessageDetails(long id, String date, Student friend, String content, boolean fromMe, String Attached_File_Name, String Attached_File_Extension
    ) {
        this.id = id;
        this.date = date;
        this.friend = friend;
        this.content = content;
        this.fromMe = fromMe;
        this.Attached_File_Name = Attached_File_Name;
        this.Attached_File_Extension = Attached_File_Extension;


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
        return content;
    }

    public boolean isFromMe() {
        return fromMe;
    }
}