package com.matrix_maeny.attendance.collage.models;

import java.util.ArrayList;
import java.util.List;

public class AttendanceModel {

    private String date;
    private String personId;
    private boolean dayPresent;
    private List<String> periodsPresent;
//    private double attendancePercentage = 0D;

    public AttendanceModel() {
    }

    public AttendanceModel(String date,String personId) {
        this.date = date;
        this.personId = personId;
    }

//    public double getAttendancePercentage() {
//        return attendancePercentage;
//    }
//
//    public void setAttendancePercentage(double attendancePercentage) {
//        this.attendancePercentage = attendancePercentage;
//    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDayPresent() {
        return dayPresent;
    }

    public void setDayPresent(boolean dayPresent) {
        this.dayPresent = dayPresent;
    }

    public List<String> getPeriodsPresent() {
        return periodsPresent;
    }

    public void addPeriod(String period){
        if(periodsPresent == null) periodsPresent = new ArrayList<>();

        periodsPresent.add(period);
    }

    public void setPeriodsPresent(List<String> periodsPresent) {
        this.periodsPresent = periodsPresent;
    }
}
