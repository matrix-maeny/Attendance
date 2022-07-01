package com.matrix_maeny.attendance.collage.models;

public class PersonModel {

    private String personName, personId,personType;
    private String collageCode,branchCode,sectionCode;

//    private int visibility = 0;


    public PersonModel() {
    }

    public PersonModel(String personName, String personId, String personType,String collageCode,String branchCode,String sectionCode) {
        this.personName = personName;
        this.personId = personId;
        this.personType = personType;
        this.collageCode = collageCode;
        this.branchCode =branchCode;
        this.sectionCode =sectionCode;
    }

//    public double getAttendancePercentage() {
//        return attendancePercentage;
//    }
//
//    public void setAttendancePercentage(double attendancePercentage) {
//        this.attendancePercentage = attendancePercentage;
//    }

    public String getCollageCode() {
        return collageCode;
    }

    public void setCollageCode(String collageCode) {
        this.collageCode = collageCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    //    public int getVisibility() {
//        return visibility;
//    }
//
//    public void setVisibility(int visibility) {
//        this.visibility = visibility;
//    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }
}
