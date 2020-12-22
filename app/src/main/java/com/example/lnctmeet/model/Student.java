package com.example.lnctmeet.model;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("Branch")
   private  String Branch;
    @SerializedName("College")
    private  String College;
    @SerializedName("Gender")
    private  String Gender;
    @SerializedName("Name")
    private  String Name;
    @SerializedName("Semseter")
    private  String Semseter;

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public String getCollege() {
        return College;
    }

    public void setCollege(String college) {
        College = college;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSemseter() {
        return Semseter;
    }

    public void setSemseter(String semester) {
        Semseter = semester;
    }
}
