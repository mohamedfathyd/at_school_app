package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by user on 11/06/2017.
 */

public class Attendance implements Serializable {

    private long id;
    public long RowNumber = 0;
    public int StudentId = 0;
    public String NameAsInPass = "";
    public String NameAsPassEng = "";

    public String FName = "";
    public String FNameA = "";

    public boolean Absence = false;
    public boolean Delay = false;
    public boolean ExcusedAbsence = false;
    public boolean ExcusedDelay = false;

    public String Hour = "";
    public String Minute = "";
    public String Description = "";
    public String GardianResidencyNo = "";
    public String USER_MASTER_Id = "";
    public String GCM_Token = "";
    public String APNS_Token = "";
    public String Datestr = "";


    public boolean LastObject = false;


    public Attendance() {

    }

    public Attendance(long id, long RowNumber, int StudentId, String NameAsInPass, String NameAsPassEng, String FNameA, boolean Absence, boolean Delay, String Datestr
            , String GCM_Token, String GardianResidencyNo, String USER_MASTER_Id, boolean LastObject) {

        this.LastObject = LastObject;
        this.RowNumber = RowNumber;
        this.StudentId = StudentId;
        this.NameAsInPass = NameAsInPass;
        this.NameAsPassEng = NameAsPassEng;
        this.FNameA = FNameA;
        this.Absence = Absence;
        this.Delay = Delay;
        this.Datestr = Datestr;
        this.GCM_Token = GCM_Token;
        this.GardianResidencyNo = GardianResidencyNo;
        this.USER_MASTER_Id = USER_MASTER_Id;
    }

    public long getId() {
        return id;
    }
}