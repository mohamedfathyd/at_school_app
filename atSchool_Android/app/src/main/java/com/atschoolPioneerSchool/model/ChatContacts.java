package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 09/08/2017.
 */

public class ChatContacts implements Serializable {
    private String title;

    private int image;
    private String content;
    private Channel channel;

    public String Id;
    public String Project_Id;
    public String published;
    public String Sort;
    public String Name;
    public String NameA;
    public String Description;
    public String DescriptionA;
    public String Img1;
    public String Img2;
    public String Img3;
    public String File_Download;
    public String LinkURL;
    public String published_Date;
    public String ProjectsName;
    public String ProjectsNameA;
    public String RowNumber;
    public String ChannelName;
    public String NewsType = "0";
    public String Maintenance_Location = "";
    public String Priority = "0";
    public String GCM_Token = "";
    public String APNS_Token = "";


    public String ChatTextMessage = "";
    public String ChatSend_Date = "";
    public String ChatRead_Date = "";


    //0
    //1   Activities الأنشطة
    //2   Agenda جدول أعمال المدرسه
    //3   School Managements  إدارة المدارس
    //4   School Facilities   مرافق المدرسة
    //5   Vision الرؤية
    //6   Mission المهمة
    //7   About حول
    //8   News الأخبار
    //9   Complaint
    //10   Suggestion
    //11  Maintenance
    //13 Chat Communication
    public ChatContacts() {
    }

    public ChatContacts(String title, String date, int image, String content, Channel channel) {
        this.title = title;
        this.published_Date = date;
        this.image = image;
        this.content = content;
        this.channel = channel;
    }

    public ChatContacts(String Id, String Name, String NameA, String Description, String DescriptionA
            , String Img1, String published_Date, String RowNumber, String NewsType, String ChannelName, String GCM_Token, String APNS_Token
            , String ChatTextMessage, String ChatSend_Date, String ChatRead_Date) {

        this.Id = Id;
        this.Name = Name;
        this.NameA = NameA;
        this.Description = Description;
        this.DescriptionA = DescriptionA;
        this.Img1 = Img1;
        this.published_Date = published_Date;
        this.RowNumber = RowNumber;
        this.NewsType = NewsType;
        this.ChannelName = ChannelName;
        this.GCM_Token = GCM_Token;
        this.APNS_Token = APNS_Token;
        this.ChatTextMessage = ChatTextMessage;
        this.ChatSend_Date = ChatSend_Date;
        this.ChatRead_Date = ChatRead_Date;

        this.title = Name;
        //this.image = Img1;
        this.content = Description;
        this.channel = new Channel(ChannelName, "#FF7043", 1);

    }

    public ChatContacts(String Id, String Name, String NameA, String Description, String DescriptionA
            , String Img1, String published_Date, String RowNumber, String NewsType, String ChannelName, String GCM_Token, String APNS_Token) {

        this.Id = Id;
        this.Name = Name;
        this.NameA = NameA;
        this.Description = Description;
        this.DescriptionA = DescriptionA;
        this.Img1 = Img1;
        this.published_Date = published_Date;
        this.RowNumber = RowNumber;
        this.NewsType = NewsType;
        this.ChannelName = ChannelName;
        this.GCM_Token = GCM_Token;
        this.APNS_Token = APNS_Token;

        this.title = Name;
        //this.image = Img1;
        this.content = Description;
        this.channel = new Channel(ChannelName, "#FF7043", 1);

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return published_Date;
    }

    public void setDate(String date) {
        this.published_Date = date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShort_content() {
        if (content.length() > 100) {
            return content.substring(0, 80) + "...";
        }
        return content + "...";
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
