package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 13/10/2017.
 */

public class WeekNumbers implements Serializable {

    public String Id;
    public String Name;
    public String RowNumber;
    public String NameA;
    public String Title;
    public Boolean HideDate = false;
    public String SCH_STUDY_DAY_ID = "0";
    public String SCH_SUBJECT_ID = "0";

    public WeekNumbers() {
    }

    public WeekNumbers(String Id, String Name, String NameA, String RowNumber, String Title, Boolean HideDate) {
        this.Id = Id;
        this.Name = Name;
        this.NameA = NameA;
        this.RowNumber = RowNumber;
        this.Title = Title;
        this.HideDate = HideDate;

    }
}
