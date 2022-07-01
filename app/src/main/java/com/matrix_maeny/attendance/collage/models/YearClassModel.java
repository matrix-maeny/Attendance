package com.matrix_maeny.attendance.collage.models;

import java.util.ArrayList;
import java.util.List;

public class YearClassModel {

    private String name, code,type;

    private long submittedCountDepartment = 0;
    private long submittedCountStudent = 0;

    private List<SectionDepartmentModel> branchList;
    private List<SectionDepartmentModel> departmentsList;

    public YearClassModel() {
    }

    public YearClassModel(String yearName, String yearCode,String type) {
        this.name = yearName;
        this.code = yearCode;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public List<SectionDepartmentModel> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<SectionDepartmentModel> branchList) {
        this.branchList = branchList;
    }

    public List<SectionDepartmentModel> getDepartmentsList() {
        return departmentsList;
    }

    public void setDepartmentsList(List<SectionDepartmentModel> departmentsList) {
        this.departmentsList = departmentsList;
    }

    public void addDepartment(SectionDepartmentModel departmentModel){
        if(departmentsList == null) departmentsList = new ArrayList<>();
        this.departmentsList.add(departmentModel);
    }

    public void addBranch(SectionDepartmentModel branchModel){
        if(branchList == null) branchList = new ArrayList<>();
        this.branchList.add(branchModel);
    }

    public long getSubmittedCountDepartment() {
        return submittedCountDepartment;
    }

    public void setSubmittedCountDepartment(long submittedCountDepartment) {
        this.submittedCountDepartment = submittedCountDepartment;
    }

    public long getSubmittedCountStudent() {
        return submittedCountStudent;
    }

    public void setSubmittedCountStudent(long submittedCountStudent) {
        this.submittedCountStudent = submittedCountStudent;
    }
}
