package com.atschoolPioneerSchool.model;

/**
 * Created by user on 10/06/2017.
 */

public class Classes {

    private long id;

    public int Message_Id = 0;
    public String msg = "";
    public String msgA = "";

    public int STUDY_YEARS_HDR_ID = 0;
    public String STUDY_YEARS = "";
    public String STUDY_YEARSA = "";


    public String CLASS_SECTION_ID = "";
    public String ClassId = "";

    public String ClassNameA = "";
    public String ClassName = "";

    public String SectionName = "";
    public String SectionNameA = "";

    public String ImagePath = "";
    private int photo = 1;
    public String GenderId = "";

    public Classes() {

    }

    public Classes(long id, int Message_Id, String msg, String msgA,
                   int STUDY_YEARS_HDR_ID, String STUDY_YEARS, String STUDY_YEARSA,

                   String CLASS_SECTION_ID, String ClassId,
                   String ClassNameA, String ClassName,
                   String SectionName, String SectionNameA,
                   String ImagePath, int photo,
                   String GenderId) {


        this.id = id;
        this.Message_Id = Message_Id;
        this.msg = msg;
        this.msgA = msgA;

        this.STUDY_YEARS_HDR_ID = STUDY_YEARS_HDR_ID;
        this.STUDY_YEARS = STUDY_YEARS;
        this.STUDY_YEARSA = STUDY_YEARSA;


        this.CLASS_SECTION_ID = CLASS_SECTION_ID;
        this.ClassId = ClassId;

        this.ClassNameA = ClassNameA;
        this.ClassName = ClassName;

        this.SectionName = SectionName;
        this.SectionNameA = SectionNameA;

        this.ImagePath = ImagePath;
        this.photo = photo;
        this.GenderId = GenderId; //False  = Female
    }

    public long getId() {
        return id;
    }

    public int getPhoto() {
        return photo;
    }

}
