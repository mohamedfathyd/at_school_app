package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 29/09/2017.
 */
import java.io.Serializable;

public class MessageNotification implements Serializable {
    private long id;
    private String date;
    public String Text_Message;

    public MessageNotification() {


    }

    public MessageNotification(long id, String date, String Text_Message) {
        this.id = id;
        this.date = date;
        this.Text_Message = Text_Message;
    }


    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return Text_Message;
    }
}