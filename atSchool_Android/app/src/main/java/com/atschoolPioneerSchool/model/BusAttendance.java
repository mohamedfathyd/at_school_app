package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 25/12/2017.
 */

public class BusAttendance implements Serializable {
    private String title;
    private int image;
    private String content;
    private Channel channel;
    public String Id;
    public String Student_USER_MASTER_Id;
    public String StepIn_Time;
    public String StepOut_Time;
    public String IsAbsent;
    public String SectionName;
    public String SectionNameA;
    public String className;
    public String classNameA;
    public String Img1;
    public String Track_Trans_Trip_Id;
    public String Student_Id;
    public String Created_Date;
    public String StudentNameA;
    public String StudentName;
    public String RowNumber;
    public String ChannelName;
    public String GardianMobile1 = "";
    public String GardianMobile2 = "";
    public String PicarsId = "";

    public BusAttendance() {
    }

    public BusAttendance(String Id, String date, String Student_USER_MASTER_Id, String Img1
            , String StepIn_Time, String StepOut_Time, String IsAbsent
            , String className, String classNameA
            , String SectionName, String SectionNameA
            , String StudentName, String StudentNameA
            , String RowNumber, String GardianMobile1, String PicarsId) {
        this.Id = Id;
        this.Student_USER_MASTER_Id = Student_USER_MASTER_Id;
        this.Created_Date = date;
        this.Img1 = Img1;
        this.StepIn_Time = StepIn_Time;
        this.StepOut_Time = StepOut_Time;
        this.IsAbsent = IsAbsent;
        this.className = className;
        this.classNameA = classNameA;
        this.SectionName = SectionName;
        this.SectionNameA = SectionNameA;
        this.StudentName = StudentName;
        this.StudentNameA = StudentNameA;
        this.RowNumber = RowNumber;
        this.GardianMobile1 = GardianMobile1;
        this.PicarsId = PicarsId;
    }


    public String getTitle() {
        return StudentName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return Created_Date;
    }

    public void setDate(String date) {
        this.Created_Date = date;
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
