package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by user on 31/05/2017.
 */

public class Evaluation implements Serializable {
    private long id;

    public int Message_Id;
    public String msg;
    public String msgA;

    public String StudentId;
    public String FName;
    public String FNameA;

    public String subjectName;
    public String subjectNameA;

    public String DESCRIPTION;
    public String DESCRIPTIONA;

    public String MASTERY_EVALUATION_Name_Term1;
    public String MASTERY_EVALUATION_Name_Term1A;

    public String MASTERY_EVALUATION_Name_Term2;
    public String MASTERY_EVALUATION_Name_Term2A;

    public String MASTERY_EVALUATION_Name_Term3;
    public String MASTERY_EVALUATION_Name_Term3A;

    public boolean LastObject = false;

    public Evaluation() {

    }

    public Evaluation(long id, int Message_Id, String msg, String msgA
            , String StudentId, String FName, String FNameA
            , String subjectName, String subjectNameA
            , String DESCRIPTION, String DESCRIPTIONA
            , String MASTERY_EVALUATION_Name_Term1, String MASTERY_EVALUATION_Name_Term1A
            , String MASTERY_EVALUATION_Name_Term2, String MASTERY_EVALUATION_Name_Term2A
            , String MASTERY_EVALUATION_Name_Term3, String MASTERY_EVALUATION_Name_Term3A
            , boolean LastObject) {

        this.LastObject = LastObject;
        this.id = id;
        this.Message_Id = Message_Id;
        this.msg = msg;
        this.msgA = msgA;
        this.StudentId = StudentId;
        this.FName = FName;
        this.FNameA = FNameA;

        this.subjectName = subjectName;
        this.subjectNameA = subjectNameA;

        this.DESCRIPTION = DESCRIPTION;
        this.DESCRIPTIONA = DESCRIPTIONA;

        this.MASTERY_EVALUATION_Name_Term1 = MASTERY_EVALUATION_Name_Term1;
        this.MASTERY_EVALUATION_Name_Term1A = MASTERY_EVALUATION_Name_Term1A;

        this.MASTERY_EVALUATION_Name_Term2 = MASTERY_EVALUATION_Name_Term2;
        this.MASTERY_EVALUATION_Name_Term2A = MASTERY_EVALUATION_Name_Term2A;

        this.MASTERY_EVALUATION_Name_Term3 = MASTERY_EVALUATION_Name_Term3;
        this.MASTERY_EVALUATION_Name_Term3A = MASTERY_EVALUATION_Name_Term3A;
    }

    public long getId() {
        return id;
    }
}