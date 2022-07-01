package com.matrix_maeny.attendance.collage.models;

import java.util.ArrayList;
import java.util.List;

public class SectionDepartmentModel {

    private String name, code,type;

    private List<PersonModel> personsList;
    private List<SectionDepartmentModel> sectionsList=null;
    private List<AttendanceModel> attendances = null;
//    private String attendances = "";

    public SectionDepartmentModel() {
    }

    public SectionDepartmentModel(String sectionName, String sectionCode,String type) {
        this.name = sectionName;
        this.code = sectionCode;
        this.type = type;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<PersonModel> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(List<PersonModel> personsList) {
        this.personsList = personsList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttendanceModel> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceModel> attendances) {
        this.attendances = attendances;
    }

    public void addAttendance(AttendanceModel attendanceModel){
        if(attendances == null) attendances = new ArrayList<>();
        attendances.add(attendanceModel);
    }

    public void addPerson(PersonModel personModel){
        if(personsList == null) personsList = new ArrayList<>();
        personsList.add(personModel);
    }
    public void addSection(SectionDepartmentModel sectionModel){
        if(sectionsList == null) sectionsList = new ArrayList<>();
        sectionsList.add(sectionModel);
    }

    public List<SectionDepartmentModel> getSectionsList() {
        return sectionsList;
    }

    public void setSectionsList(List<SectionDepartmentModel> sectionsList) {
        this.sectionsList = sectionsList;
    }
}
