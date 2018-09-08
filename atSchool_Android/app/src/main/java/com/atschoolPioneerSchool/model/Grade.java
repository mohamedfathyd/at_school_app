package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by user on 30/05/2017.
 */

public class Grade implements Serializable {
    private long id;

    public int Message_Id;
    public String msg;
    public String msgA;

    public String SCORENameA;
    public String SCOREName;
    public String SUBJECTSName;
    public String SUBJECTSNameA;
    public String AVGmainScore;
    public String Score;
    public String MainScore;
    public String totalAVGmainScore;

    public boolean LastObject = false;


    public Grade() {

    }

    public Grade(long id, int Message_Id, String msg, String msgA
            , String SCORENameA, String SCOREName, String SUBJECTSName, String SUBJECTSNameA
            , String AVGmainScore, String Score, String MainScore, String totalAVGmainScore, boolean LastObject) {

        this.LastObject = LastObject;
        this.id = id;
        this.Message_Id = Message_Id;
        this.msg = msg;
        this.msgA = msgA;
        this.SCORENameA = SCORENameA;
        this.SCOREName = SCOREName;
        this.SUBJECTSName = SUBJECTSName;
        this.SUBJECTSNameA = SUBJECTSNameA;
        this.AVGmainScore = AVGmainScore;
        this.Score = Score;
        this.MainScore = MainScore;
        this.totalAVGmainScore = totalAVGmainScore;
    }

    public long getId() {
        return id;
    }
}