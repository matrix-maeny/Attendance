package com.matrix_maeny.attendance;

import com.matrix_maeny.attendance.collage.models.AttendanceSheetModel;
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;

import java.security.PublicKey;

public class DataCenter {

    public static String submittedDateDepartment = "No Date";
    public static String submittedDateStudent = "No Date";
//    public static String attendanceType = "";
    public static String branchCode = null;
    public static SectionDepartmentModel branchModel = null;

    public static UserModel model = null;
    public static YearClassModel selectedYearClassModel = null;
    public static SectionDepartmentModel selectedDepartSectionModel = null;
    public static PersonModel selectedPersonModel = null;

    public static AttendanceSheetModel attendanceSheetModel = null;
    public static String collageCode = null;
}
