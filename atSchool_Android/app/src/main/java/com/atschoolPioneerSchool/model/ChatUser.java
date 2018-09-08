package com.atschoolPioneerSchool.model;

import java.io.Serializable;

/**
 * Created by OmarA on 10/09/2017.
 */

public class ChatUser implements Serializable {

    public String USER_MASTER_Id;
    public String Name;
    public String NameA;
    public String Profile_Picture;
    public String Email1;
    public String RowNumber;

    public ChatUser(String USER_MASTER_Id, String Name, String NameA, String Profile_Picture, String Email1, String RowNumber) {
        this.USER_MASTER_Id = USER_MASTER_Id;
        this.Name = Name;
        this.NameA = NameA;
        this.Profile_Picture = Profile_Picture;
        this.Email1 = Email1;
        this.RowNumber = RowNumber;

    }


}

