package com.matrix_maeny.attendance.collage.models;

public class AttendanceSheetModel {

    private String attendanceType;
    private int noOfPeriods = 0;
    private String whom;

    public AttendanceSheetModel() {
    }

    public AttendanceSheetModel(String type, int noOfPeriods,String whom) {
        this.attendanceType = type;
        this.noOfPeriods = noOfPeriods;
        this.whom = whom;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public int getNoOfPeriods() {
        return noOfPeriods;
    }

    public void setNoOfPeriods(int noOfPeriods) {
        this.noOfPeriods = noOfPeriods;
    }

    public String getWhom() {
        return whom;
    }

    public void setWhom(String whom) {
        this.whom = whom;
    }
}
