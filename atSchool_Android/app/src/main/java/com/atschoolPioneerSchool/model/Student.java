package com.atschoolPioneerSchool.model;

/**
 * Created by user on 30/05/2017.
 */

import java.io.Serializable;


public class Student implements Serializable {


    private long id;

    public int Message_Id = 0;
    public String msg = "";
    public String msgA = "";

    public int STUDY_YEARS_HDR_ID = 0;
    public String STUDY_YEARS = "";
    public String STUDY_YEARSA = "";

    public int StudentId = 0;
    public String StuName = "123";
    public String StuNameA = "123";
    public String SectionId = "";
    public String CLASS_SECTION_ID = "0";

    private String _ClassId = "0";

    public String getClassId() {

        return _ClassId.equals("") == true ? "0" : _ClassId;
    }

    public void setClassId(String prmClassId) {

        _ClassId = prmClassId;
    }

    public String ClassNameA = "";
    public String ClassName = "";

    public String SectionName = "";
    public String SectionNameA = "";

    public int StudStateId = 1;
    public String ImagePath = "";
    private int photo = 1;
    public String GenderId = "";
    public String StudentImageName = "";

    public String USER_MASTER_Id_Student = "";
    public String ResidencyNo_Student = "";
    public String GCM_Token = "";
    public String APNS_Token = "";
    public String PicarsId = "";

    public Student() {

    }

    public Student(long id, int Message_Id, String msg, String msgA,
                   int STUDY_YEARS_HDR_ID, String STUDY_YEARS, String STUDY_YEARSA,
                   int StudentId, String StuName, String StuNameA,
                   String CLASS_SECTION_ID, String ClassId,
                   String ClassNameA, String ClassName,
                   String SectionName, String SectionNameA,
                   int StudStateId, String ImagePath, int photo,
                   String GenderId) {


        this.id = id;
        this.Message_Id = Message_Id;
        this.msg = msg;
        this.msgA = msgA;

        this.STUDY_YEARS_HDR_ID = STUDY_YEARS_HDR_ID;
        this.STUDY_YEARS = STUDY_YEARS;
        this.STUDY_YEARSA = STUDY_YEARSA;

        this.StudentId = StudentId;
        this.StuName = StuName;
        this.StuNameA = StuNameA;

        this.CLASS_SECTION_ID = CLASS_SECTION_ID;
        this._ClassId = ClassId;

        this.ClassNameA = ClassNameA;
        this.ClassName = ClassName;

        this.SectionName = SectionName;
        this.SectionNameA = SectionNameA;

        this.StudStateId = StudStateId;
        this.ImagePath = ImagePath;
        this.photo = photo;
        this.GenderId = GenderId; //False  = Female
    }

    public Student(long id, String name, int photo) {
        this.id = id;
        this.StuName = name;
        this.photo = photo;
    }

    public Student(String name, int photo) {
        this.StuName = name;
        this.photo = photo;
    }

    public Student(String name, int photo, int StudentsId, String PicarsId) {
        this.StuName = name;
        this.photo = photo;
        this.StudentId = StudentsId;
        this.PicarsId = PicarsId;
    }


    public Student(String name, int photo, int StudentsId) {
        this.StuName = name;
        this.photo = photo;
        this.StudentId = StudentsId;
        this.PicarsId = "";
    }

    public String getName() {
        return StuName;
    }

    public int getStudentsId() {
        return StudentId;
    }


    public long getId() {
        return id;
    }

    public int getPhoto() {
        return photo;
    }

}
