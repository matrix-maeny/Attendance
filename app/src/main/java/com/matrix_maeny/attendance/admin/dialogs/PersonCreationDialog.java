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
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.DepartmentCreationDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.List;
import java.util.Objects;

public class PersonCreationDialog extends AppCompatDialogFragment {


    private DepartmentCreationDialogBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private String currentUid;
    private String name;
    private String code;
    private ExposeDialogs exposeDialogs;
    private List<SectionDepartmentModel> sectionDepartmentModel;

    private PersonCreationDialogListener listener;


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.department_creation_dialog, null);
        binding = DepartmentCreationDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        listener = (PersonCreationDialogListener) requireContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());

        binding.headingTv.setText("Create Person");
        binding.nameLayout.setHint("Enter name...!");


        binding.addBtn.setOnClickListener(v -> {
            if (checkName() && checkCode()) {
                addData();
            }
        });

        return builder.create();
    }

    private void addData() {
        exposeDialogs.showProgressDialog("Creating...", "wait...");
        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CollageModel collageModel = snapshot.getValue(CollageModel.class);

                if (collageModel != null) {

                    abc:
                    for (YearClassModel model : collageModel.getYears()) {

                        if (model.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                            if (DepartmentsActivity.isDepartment) {
                                sectionDepartmentModel = model.getDepartmentsList();
                            } else {
                                sectionDepartmentModel = model.getBranchList();
                            }

                            for (SectionDepartmentModel m : sectionDepartmentModel) {

                                if (DepartmentsActivity.isDepartment) {
                                    if (m.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {

                                        if (m.getPersonsList() != null) {
                                            if (checkCodeAndNameAvailability(m)) {
                                                return;
                                            }
                                        }

                                        m.addPerson(new PersonModel(name, code, "department", collageModel.getCollageCode(), m.getCode(), ""));

                                        break abc;
                                    }
                                } else {

                                    if (m.getCode().equals(DataCenter.branchCode)) {
                                        if (m.getSectionsList() != null) {

                                            for (SectionDepartmentModel sdm : m.getSectionsList()) {

                                                if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                    if (sdm.getPersonsList() != null) {
                                                        if (checkCodeAndNameAvailability(sdm)) {
                                                            return;
                                                        }
                                                    }
                                                    sdm.addPerson(new PersonModel(name, code, "section", collageModel.getCollageCode(), m.getCode(), ""));


                                                }
                                            }
                                        }
                                    }

                                }
                            }

                            break;
                        }
                    }

                    reference.setValue(collageModel).addOnCompleteListener(task -> {

                        exposeDialogs.dismissProgressDialog();
                        dismiss();

                        if (task.isSuccessful()) {
//                            exposeDialogs.showToast("Created successfully", 1);
                            listener.fetchData();
                        } else {
                            exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
                        }
                    }).addOnFailureListener(e -> {
                        exposeDialogs.dismissProgressDialog();
                        exposeDialogs.showToast(e.getMessage(), 1);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });
    }

    private boolean checkCodeAndNameAvailability(@NonNull SectionDepartmentModel m) {
        for (PersonModel p : m.getPersonsList()) {
            if (p.getPersonId().equals(code)) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast("Code already taken", 1);
                return true;
            }
        }
        return false;
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

    public interface PersonCreationDialogListener {
        void fetchData();
    }

}
