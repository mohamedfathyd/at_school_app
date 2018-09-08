package com.atschoolPioneerSchool.model;

/**
 * Created by OmarA on 13/10/2017.
 */

import java.io.Serializable;

/**
 * Created by OmarA on 29/09/2017.
 */
import java.io.Serializable;

public class WeeklyPlan implements Serializable {
    public long id;
    public String SCH_STUDY_DAY_ID;
    public String Description_A;
    public String Description_B;
    public String Class_Name;
    public String CLASS_SECTION_NAME;
    public String SCH_STUDY_DAY_NAME;
    public String SUBJECT_NAMEA;
    public String SUBJECT_NAME;
    public String ProgressText = "";
    public int ProgressBarLevel = 0;
    public String File_Attached;

    private boolean IsDocument = false;

    public WeeklyPlan() {


    }

    public WeeklyPlan(long Id, String SCH_STUDY_DAY_ID, String Description_A, String Description_B, String Class_Name, String CLASS_SECTION_NAME
            , String SCH_STUDY_DAY_NAME, String SUBJECT_NAMEA, String SUBJECT_NAME, String File_Attached) {
        this.id = Id;
        this.File_Attached = File_Attached;
        this.SCH_STUDY_DAY_ID = SCH_STUDY_DAY_ID;
        this.Description_A = Description_A;
        this.Description_B = Description_B;
        this.Class_Name = Class_Name;
        this.CLASS_SECTION_NAME = CLASS_SECTION_NAME;
        this.SCH_STUDY_DAY_NAME = SCH_STUDY_DAY_NAME;
        this.SUBJECT_NAMEA = SUBJECT_NAMEA;
        this.SUBJECT_NAME = SUBJECT_NAME;

    }

    public long getId() {
        return id;
    }
}