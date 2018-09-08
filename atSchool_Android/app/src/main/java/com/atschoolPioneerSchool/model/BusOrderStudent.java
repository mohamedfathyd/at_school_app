package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 24/11/2017.
 */

public class BusOrderStudent implements Serializable {

    public String Student_USER_MASTER_Id;
    public int Id;
    public String Track_Trans_Order_Id;
    public String Student_Id;
    public String StudentName = "";
    public String className = "";
    public String SectionName = "";
    public String LAT = "";
    public String LNG = "";
    public String StudentImageName = "";
    public String GardianMobile1 = "";
    public String GardianMobile2 = "";
    public String RowNumber = "";
    public boolean IsTripEnded = false;

    public String ProgressText = "";
    public int ProgressBarLevel = 0;

    //properties from trip table
    public int Track_Trans_Trip_Id = 0;
    public int Track_Trans_Trip_Students_Id = 0;
    public String StepIn_Time = "";
    public String StepOut_Time = "";
    public int IsAbsent = 0;
    public String Description = "";
    public String GardianGCM = "";
    public String GardianAPNS = "";
    public String GuardianUSER_MASTER_Id = "";
    public String PicarsId = "";
    public int OrderArrival = 0;
    public String NotConfirm_Time = "";

    public BusOrderStudent() {


    }

    public BusOrderStudent(int Id, String Track_Trans_Order_Id, String StudentName, String Student_Id, String className, String SectionName,
                           String LAT, String LNG, String StudentImageName, String GardianMobile1, String GardianMobile2, String RowNumber
            , String PicarsId, String GuardianUSER_MASTER_Id) {
        this.Id = Id;
        this.Track_Trans_Order_Id = Track_Trans_Order_Id;
        this.StudentName = StudentName;
        this.Student_Id = Student_Id;
        this.className = className;
        this.SectionName = SectionName;
        this.LAT = LAT;
        this.LNG = LNG;
        this.StudentImageName = StudentImageName;
        this.GardianMobile1 = GardianMobile1;
        this.GardianMobile2 = GardianMobile2;
        this.RowNumber = RowNumber;
        this.PicarsId = PicarsId;
        this.GuardianUSER_MASTER_Id = GuardianUSER_MASTER_Id;
    }


    public int getId() {
        return Id;
    }
}