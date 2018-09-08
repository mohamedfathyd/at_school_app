package com.atschoolPioneerSchool.model;

import java.io.Serializable;

public class RegisterStudentsAttendance  implements Serializable {


    public int Id;
    public String StudentId;
    public String NameAsInPass = "";
    public String NameAsPassEng = "";
    public String Absence = "";
    public String Delay = "";
    public String ExcusedAbsence = "";
    public String ExcusedDelay = "";

    public String GCM_Token = "";
    public String APNS_Token = "";

    public String StudentImageName = "";
    public String GardianMobile1 = "";
    public String GardianMobile2 = "";
    public String RowNumber = "";

    public String ProgressText = "";
    public int ProgressBarLevel = 0;

    //properties from trip table

    public String Description = "";
    public String GardianGCM = "";
    public String GardianAPNS = "";
    public String GuardianUSER_MASTER_Id = "";
    public String USER_MASTER_Id = "";



    public RegisterStudentsAttendance() {


    }

//    {"StudentId":"3470","NameAsInPass":"احمد ابراهيم رزق مكحل","NameAsPassEng":"احمد ابراهيم رزق مكحل","FName":"احمد","FNameA":"احمد",
//            "GardianResidencyNo":"9721024605","USER_MASTER_Id":"5042",
//            "GCM_Token":"dBiq5eV_A-0: - " +
//            " ","APNS_Token":"","Absence":"False","Delay":"False","ExcusedAbsence":"False","ExcusedDelay":"False","Hour":"","Minute":"","Description":"",
//            "StudentImageName":"","GardianMobile1":"962795862683","GardianMobile2":"962795862683","GuardianUSER_MASTER_Id":"5042"},


            public RegisterStudentsAttendance(
            int Id, String StudentId, String NameAsInPass, String NameAsPassEng,
            String Absence, String Delay,   String ExcusedAbsence, String ExcusedDelay,
            String StudentImageName, String GardianMobile1, String GardianMobile2,
            String GCM_Token, String APNS_Token, String GuardianUSER_MASTER_Id  , String USER_MASTER_Id ) {

            this.Id = Id;
            this.StudentId = StudentId;
            this.NameAsInPass = NameAsInPass;
            this.NameAsPassEng = NameAsPassEng;

            this.Absence = Absence;
            this.Delay = Delay;

            this.GCM_Token = GCM_Token;
            this.APNS_Token = APNS_Token;

            this.ExcusedAbsence = ExcusedAbsence;
            this.ExcusedDelay = ExcusedDelay;

            this.StudentImageName = StudentImageName;
            this.GardianMobile1 = GardianMobile1;
            this.GardianMobile2 = GardianMobile2;

            this.GuardianUSER_MASTER_Id = GuardianUSER_MASTER_Id;
            this.USER_MASTER_Id = USER_MASTER_Id;
            }


    public int getId() {
        return Id;
    }
}