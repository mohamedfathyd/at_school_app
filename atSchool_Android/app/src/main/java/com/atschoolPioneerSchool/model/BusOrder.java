package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 24/11/2017.
 */

public class BusOrder implements Serializable {

    public int Id;
    public int Track_Buss_Info_Id;
    public int Track_Trans_Direction_Id;
    public int Driver_USER_MASTER_Id;
    public int Assistant_USER_MASTER_Id;

    public String Description;
    public String Start_Time;
    public String End_Time;
    public String Tracking_Device_Id;

    public String Buss_Information;
    public String DirectionA;
    public String Direction;
    public String DriverA;
    public String Driver;
    public String AssistantA;
    public String Assistant;

    private int image;
    private String content;
    private Channel channel;

    public String Img1;
    public String RowNumber;

    public BusOrder() {
    }

    public BusOrder(int Id, String Description, String Start_Time, String End_Time, String Buss_Information,
                    String Direction, String Driver, String Assistant) {
        this.Id = Id;
        this.Description = Description;
        this.Start_Time = Start_Time;
        this.End_Time = End_Time;
        this.Buss_Information = Buss_Information;
        this.Direction = Direction;
        this.Driver = Driver;
        this.Assistant = Assistant;

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
