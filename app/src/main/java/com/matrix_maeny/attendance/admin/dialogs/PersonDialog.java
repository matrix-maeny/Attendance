package com.matrix_maeny.attendance.admin.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.collage.activities.DepartmentsActivity;
import com.matrix_maeny.attendance.collage.models.AttendanceModel;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.PersonDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.List;
import java.util.Objects;

public class PersonDialog extends AppCompatDialogFragment {

    private PersonDialogBinding binding;

    private FirebaseDatabase firebaseDatabase;
    //    private String currentUid;
//    private String name;
//    private String code;
    private ExposeDialogs exposeDialogs;

    private PersonDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.person_dialog, null);
        binding = PersonDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());

        listener = (PersonDialogListener) requireContext();

        binding.showAttendanceTv.setOnClickListener(v -> {
            // go to attendance activity
//            dismiss();
//            AttendanceSheetDialog dialog = new AttendanceSheetDialog();

//            dismiss();
            setPercentage();

        });

        binding.scTv.setOnClickListener(v -> {
            // showCode
            dismiss();
            ShowCodeDialog.isPerson = true;
            ShowCodeDialog showCodeDialog = new ShowCodeDialog();
            showCodeDialog.show(requireActivity().getSupportFragmentManager(), "Show Code Dialog");
        });

        binding.deleteTv.setOnClickListener(v -> {
            // delete user
            new AlertDialog.Builder(requireContext())
                    .setTitle("Are you sure..?")
                    .setMessage("User record will be deleted permanently..!")
                    .setPositiveButton("ok", (dialog, which) -> {
                        deletePerson();
                        dialog.dismiss();
                    }).setNegativeButton("cancel", (dialog, which) -> {
                        dialog.dismiss();
                        dismiss();
                    }).create().show();
        });

        return builder.create();
    }

    private void setPercentage() {
        exposeDialogs.showProgressDialog("Fetching...", "wait...");

        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    CollageModel collageModel = snapshot.getValue(CollageModel.class);

                    if (collageModel != null) {

                        long totalDays = 0;

                        boolean periods = DataCenter.attendanceSheetModel.getAttendanceType().equals("p");
                        long presentCount = 0;

                        if (DataCenter.selectedYearClassModel.getType().equals("year")) {

                            if (collageModel.getYears() != null) {

                                abc:
                                for (YearClassModel ycm : collageModel.getYears()) {

                                    if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                        List<SectionDepartmentModel> tempM;

                                        if (DepartmentsActivity.isDepartment) {
                                            totalDays = ycm.getSubmittedCountDepartment();
                                            tempM = ycm.getDepartmentsList();
                                        } else {
                                            totalDays = ycm.getSubmittedCountStudent();
                                            tempM = ycm.getBranchList();
                                        }

                                        if (tempM != null) {

                                            for (SectionDepartmentModel sdm : tempM) {

                                                if (DepartmentsActivity.isDepartment) {
                                                    if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {

                                                        if (sdm.getAttendances() != null) {
                                                            for (AttendanceModel am : sdm.getAttendances()) {

                                                                if (am.getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {
                                                                    if (periods) {

                                                                        if (am.getPeriodsPresent() != null) {
                                                                            presentCount += am.getPeriodsPresent().size();
                                                                        }

                                                                    } else {
                                                                        presentCount += 1;

                                                                    }
                                                                }
                                                            }

                                                            break abc;

                                                        }

                                                        break abc;
                                                    }

                                                } else {

                                                    if (sdm.getCode().equals(DataCenter.branchCode)) {
                                                        for (SectionDepartmentModel section : sdm.getSectionsList()) {
                                                            if (section.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {

                                                                if (section.getAttendances() != null) {
                                                                    for (AttendanceModel am : section.getAttendances()) {

                                                                        if (am.getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {
                                                                            if (periods) {

                                                                                presentCount += am.getPeriodsPresent().size();

                                                                            } else {
                                                                                presentCount += 1;

                                                                            }
                                                                        }
                                                                    }

                                                                    break abc;

                                                                }

                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }

//                                        break abc;
                                        break;
                                    }
                                }
                            }

                        }
//                        else {
//                            // classes
//
//                            if (collageModel.getClasses() != null) {
//
//                                for (YearClassModel ycm : collageModel.getClasses()) {
//                                    if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {
//
//                                    }
//                                }
//                            }
//                        }

                        double percentage;

                        if (periods) {
                            totalDays = totalDays * DataCenter.attendanceSheetModel.getNoOfPeriods();
                            ShowAttendanceDialog.isPeriods = true;
                        }
                        if (totalDays != 0) {
                            percentage = (double) presentCount * 100.0 / totalDays;
                        } else percentage = 0;
                        exposeDialogs.dismissProgressDialog();
                        ShowAttendanceDialog attendanceDialog = new ShowAttendanceDialog();
                        ShowAttendanceDialog.percentage = Double.parseDouble(String.format("%.1f", percentage));
                        ShowAttendanceDialog.presentDays = presentCount;
                        ShowAttendanceDialog.totalDays = totalDays;
                        attendanceDialog.show(requireActivity().getSupportFragmentManager(), "percentage dialog");
                        dismiss();


                    }
                } else {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast("Collage not found", 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);

            }
        });

    }

    private void deletePerson() {
        exposeDialogs.showProgressDialog("Deleting...", "wait...");

        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    CollageModel collageModel = snapshot.getValue(CollageModel.class);

                    if (collageModel != null) {


                        if (DataCenter.selectedYearClassModel.getType().equals("year")) {

                            if (collageModel.getYears() != null) {

                                abc:
                                for (YearClassModel ycm : collageModel.getYears()) {

                                    if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                        List<SectionDepartmentModel> tempM;

                                        if (DepartmentsActivity.isDepartment) {
                                            tempM = ycm.getDepartmentsList();
                                        } else tempM = ycm.getBranchList();


                                        if (tempM != null) {

                                            for (SectionDepartmentModel sdm : tempM) {


                                                if (DepartmentsActivity.isDepartment) {
                                                    if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {


                                                        if (sdm.getPersonsList() != null) {

                                                            for (int i = 0; i < sdm.getPersonsList().size(); i++) {

                                                                if (sdm.getPersonsList().get(i).getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {

                                                                    if (sdm.getAttendances() != null) {
                                                                        for (int j = 0; j < sdm.getAttendances().size(); j++) {
                                                                            if (sdm.getAttendances().get(i).getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {
                                                                                sdm.getAttendances().remove(i);
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    sdm.getPersonsList().remove(i);

                                                                    break;
                                                                }
                                                            }
                                                            break abc;
                                                        }

                                                        break abc;
                                                    }

                                                } else {

                                                    if (sdm.getCode().equals(DataCenter.branchCode)) {
                                                        for (SectionDepartmentModel section : sdm.getSectionsList()) {
                                                            if (section.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {


                                                                if (section.getPersonsList() != null) {

                                                                    for (int i = 0; i < sdm.getPersonsList().size(); i++) {

                                                                        if (sdm.getPersonsList().get(i).getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {

                                                                            if (sdm.getAttendances() != null) {
                                                                                for (int j = 0; j < sdm.getAttendances().size(); j++) {
                                                                                    if (sdm.getAttendances().get(i).getPersonId().equals(DataCenter.selectedPersonModel.getPersonId())) {
                                                                                        sdm.getAttendances().remove(i);
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }
                                                                            sdm.getPersonsList().remove(i);

                                                                            break;
                                                                        }
                                                                    }
                                                                    break abc;
                                                                }

                                                            }
                                                            break abc;
                                                        }
                                                    }
                                                    break abc;
                                                }
//                                                if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
//
//
//
//                                                    break abc;
//                                                }
                                            }
                                        }

                                        break;
                                    }
                                    break;
                                }
                            }

                        }

//                        else {
//                            // classes
//
//                            if (collageModel.getClasses() != null) {
//
//                                for (YearClassModel ycm : collageModel.getClasses()) {
//                                    if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {
//
//                                    }
//                                }
//                            }
//                        }

                        reference.setValue(collageModel).addOnCompleteListener(task -> {

                            exposeDialogs.dismissProgressDialog();
                            if (task.isSuccessful()) {
                                exposeDialogs.showToast("Deleted successfully", 1);
                                listener.fetchData();
                            } else {
                                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
                            }
                            dismiss();

                        }).addOnFailureListener(e -> {
                            exposeDialogs.dismissProgressDialog();
                            exposeDialogs.showToast(e.getMessage(), 1);
                        });

                    }

                } else {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast("Collage not found", 1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });
    }


    public interface PersonDialogListener {
        void fetchData();
    }
}
