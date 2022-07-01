package com.matrix_maeny.attendance.collage.models;

import java.util.ArrayList;
import java.util.List;

public class CollageModel {

//    private String submittedDateDepartment = "No Date";
//    private String submittedDateStudents = "No Date";
//    private long submittedCountDepartment = 0;
//    private long submittedCountStudent = 0;

    private String collageName,collageCode;

    private List<YearClassModel> classes = null;
    private List<YearClassModel> years = null;
    private List<AttendanceSheetModel> attendanceSheets = null;
//    private List<AttendanceModel> personAttendances = null;


    public CollageModel() {
    }

    public CollageModel(String collageName, String collageCode) {
        this.collageName = collageName;
        this.collageCode = collageCode;

//        classes = new ArrayList<>();
//        years = new ArrayList<>();
    }

//    public long getSubmittedCountDepartment() {
//        return submittedCountDepartment;
//    }
//
//    public void setSubmittedCountDepartment(long submittedCountDepartment) {
//        this.submittedCountDepartment = submittedCountDepartment;
//    }

//    public String getSubmittedDateDepartment() {
//        return submittedDateDepartment;
//    }
//
//    public void setSubmittedDateDepartment(String submittedDateDepartment) {
//        this.submittedDateDepartment = submittedDateDepartment;
//    }

    public String getCollageName() {
        return collageName;
    }

    public void setCollageName(String collageName) {
        this.collageName = collageName;
    }

    public String getCollageCode() {
        return collageCode;
    }

    public void setCollageCode(String collageCode) {
        this.collageCode = collageCode;
    }

    public List<YearClassModel> getClasses() {
        return classes;
    }

    public void setClasses(List<YearClassModel> classes) {
        this.classes = classes;
    }

    public List<YearClassModel> getYears() {
        return years;
    }

    public void setYears(List<YearClassModel> years) {
        this.years = years;
    }

    public List<AttendanceSheetModel> getAttendanceSheets() {
        return attendanceSheets;
    }

    public void setAttendanceSheets(List<AttendanceSheetModel> attendanceSheets) {
        this.attendanceSheets = attendanceSheets;
    }

//    public String getPersonAttendances() {
//        return personAttendances;
//    }
//
//    public void setPersonAttendances(String personAttendances) {
//        this.personAttendances = personAttendances;
//    }
//


//    public void addAttendance(AttendanceModel attendanceModel){
//        if(personAttendances == null) personAttendances = new ArrayList<>();
//        this.personAttendances.add(attendanceModel);
//    }


    public void addClass(YearClassModel classModel){
        if(classes == null) classes = new ArrayList<>();
        this.classes.add(classModel);
    }
    public void addYear(YearClassModel yearModel){
        if(years == null) years = new ArrayList<>();
        this.years.add(yearModel);
    }

    public void addSheet(AttendanceSheetModel sheetModel){
        if(attendanceSheets == null) attendanceSheets = new ArrayList<>();
        this.attendanceSheets.add(sheetModel);
    }


//    public String getSubmittedDateStudents() {
//        return submittedDateStudents;
//    }
//
//    public void setSubmittedDateStudents(String submittedDateStudents) {
//        this.submittedDateStudents = submittedDateStudents;
//    }


//    public long getSubmittedCountStudent() {
//        return submittedCountStudent;
//    }
//
//    public void setSubmittedCountStudent(long submittedCountStudent) {
//        this.submittedCountStudent = submittedCountStudent;
//    }
}
