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
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.DepartmentCreationDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.List;
import java.util.Objects;

public class DepartmentCreationDialog extends AppCompatDialogFragment {

    DepartmentCreationDialogBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private String currentUid;
    private String name;
    private String code;
    private ExposeDialogs exposeDialogs;

    private DepartmentCreationDialogListener listener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.department_creation_dialog, null);
        binding = DepartmentCreationDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        listener = (DepartmentCreationDialogListener) requireContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());

        if (DepartmentsActivity.isDepartment) {
            binding.headingTv.setText("Create Department");

        } else if (DepartmentsActivity.isSections) {
            // create sections
            binding.headingTv.setText("Create Section");
            binding.nameLayout.setHint("Enter name..! (Ex: A, B etc.)");
        } else {
            binding.headingTv.setText("Create Branch");
            binding.nameLayout.setHint("Enter name..! (Ex: CSE, ECM etc.)");
        }

        binding.addBtn.setOnClickListener(v -> {
            if (checkName() && checkCode()) {
                addData();
            }
        });

        return builder.create();

    }

    private void addData() {
        exposeDialogs.showProgressDialog("Creating...", "wait...");
        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);//.child("years");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CollageModel collageModel = snapshot.getValue(CollageModel.class);

                if (collageModel != null) {
                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {

                        for (YearClassModel model : collageModel.getYears()) {

                            if (model.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                List<SectionDepartmentModel> tempM;

                                if (DepartmentsActivity.isDepartment) {
                                    tempM = model.getDepartmentsList();
                                } else tempM = model.getBranchList();

                                if (tempM != null) {

                                    if (DepartmentsActivity.isSections) {
                                        for (SectionDepartmentModel sm : tempM) {
                                            if (sm.getCode().equals(DataCenter.branchCode)) {
                                                if (sm.getSectionsList() != null) {
                                                    tempM = sm.getSectionsList();

                                                    if (!checkNameAndCodeAvailability(tempM)) {
                                                        return;
                                                    }
                                                }

                                                sm.addSection(new SectionDepartmentModel(name, code, "section"));
                                                uploadData(reference, collageModel);
                                                return;
                                            }
                                        }

                                        return;
                                    }

                                    if (!checkNameAndCodeAvailability(tempM)) {
                                        return;
                                    }
//                                    for (SectionDepartmentModel m : tempM) {
//
//                                        if (m.getName().equalsIgnoreCase(name)) {
//                                            exposeDialogs.dismissProgressDialog();
//                                            exposeDialogs.showToast("Name already taken", 1);
//                                            return;
//                                        }
//                                        if (m.getCode().equals(code)) {
//                                            exposeDialogs.dismissProgressDialog();
//                                            exposeDialogs.showToast("Code already taken", 1);
//                                            return;
//                                        }
//                                    }
                                }

                                if (DepartmentsActivity.isDepartment) {
                                    model.addDepartment(new SectionDepartmentModel(name, code, "department"));
                                } else {
                                    model.addBranch(new SectionDepartmentModel(name, code, "branch"));
                                }
                                break;
                            }
                        }
                    }// else class

                    uploadData(reference, collageModel);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 0);
            }
        });


    }

    private void uploadData(DatabaseReference reference, CollageModel collageModel) {
        reference.setValue(collageModel).addOnCompleteListener(task -> {

            exposeDialogs.dismissProgressDialog();
            dismiss();

            if (task.isSuccessful()) {
                exposeDialogs.showToast("Created successfully", 1);
                if (DepartmentsActivity.isSections) {
                    listener.fetchSections();
                } else {
                    listener.fetchData();
                }
            } else {
                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
            }
        }).addOnFailureListener(e -> {
            exposeDialogs.dismissProgressDialog();
            exposeDialogs.showToast(e.getMessage(), 0);
        });
    }

//    private void addSection() {
//        exposeDialogs.showProgressDialog("Creating...", "wait...");
//        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(DataCenter.collageCode);//.child("years");
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                CollageModel collageModel = snapshot.getValue(CollageModel.class);
//
//                if (collageModel != null) {
//                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {
//
//                        for (YearClassModel model : collageModel.getYears()) {
//
//                            if (model.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {
//
//                                List<SectionDepartmentModel> tempM;
//
//                                if (DepartmentsActivity.isDepartment) {
//                                    tempM = model.getDepartmentsList();
//                                } else tempM = model.getBranchList();
//
//
//                                if (tempM != null) {
//
//                                    checkNameAndCodeAvailability(tempM);
//                                }
//
//                                if (DepartmentsActivity.isDepartment) {
//                                    model.addDepartment(new SectionDepartmentModel(name, code, "department"));
//                                } else if (DepartmentsActivity.isSections) {
//
//                                } else {
//                                    model.addBranch(new SectionDepartmentModel(name, code, "branch"));
//                                }
//                                break;
//                            }
//                        }
//                    }// else class
//
//                    reference.setValue(collageModel).addOnCompleteListener(task -> {
//
//                        exposeDialogs.dismissProgressDialog();
//                        dismiss();
//
//                        if (task.isSuccessful()) {
//                            exposeDialogs.showToast("Created successfully", 1);
//                            listener.fetchData();
//                        } else {
//                            exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
//                        }
//                    }).addOnFailureListener(e -> {
//                        exposeDialogs.dismissProgressDialog();
//                        exposeDialogs.showToast(e.getMessage(), 0);
//                    });
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                exposeDialogs.dismissProgressDialog();
//                exposeDialogs.showToast(error.getMessage(), 0);
//            }
//        });
//
//
//    }

    private boolean checkNameAndCodeAvailability(@NonNull List<SectionDepartmentModel> tempM) {
        for (SectionDepartmentModel m : tempM) {

            if (m.getName().equalsIgnoreCase(name)) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast("Name already taken", 1);
                return false;
            }
            if (m.getCode().equals(code)) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast("Code already taken", 1);
                return false;
            }
        }

        return true;

    }


    private boolean checkCode() {
        code = null;

        try {
            code = Objects.requireNonNull(binding.createCodeEt.getText()).toString().trim();
            if (code.length() < 6) {
                exposeDialogs.showToast("Code must be greater than 6 characters", 1);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter code", 1);

        return false;
    }

    private boolean checkName() {
        name = null;

        try {
            name = Objects.requireNonNull(binding.createDEt.getText()).toString().trim();
            if (!name.equals("")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter name", 1);

        return false;
    }

    public interface DepartmentCreationDialogListener {
        void fetchData();

        void fetchSections();
    }
}
