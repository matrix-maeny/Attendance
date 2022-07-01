package com.matrix_maeny.attendance.admin.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import com.matrix_maeny.attendance.collage.activities.PersonsListActivity;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.DepartmentDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.List;
import java.util.Objects;

public class DepartmentDialog extends AppCompatDialogFragment {

    private DepartmentDialogBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private ExposeDialogs exposeDialogs;

    private DepartmentDialogListener listener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.department_dialog, null);
        binding = DepartmentDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());

        listener = (DepartmentDialogListener) requireContext();

        if (DepartmentsActivity.isDepartment) {
            binding.showFacTv.setText("Show Faculty");
        } else if (DepartmentsActivity.isSections) {
            binding.showFacTv.setText("Show Students");

        } else {
            binding.showFacTv.setText("Show Sections");
        }

        binding.showFacTv.setOnClickListener(v -> {
            // show all faculty
            if (DepartmentsActivity.isDepartment) {
                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), PersonsListActivity.class));
            } else {
//                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), SectionsActivity.class));
                if (DepartmentsActivity.isSections) {
//                    Toast.makeText(requireContext(), "in sections", Toast.LENGTH_SHORT).show();
                    requireContext().startActivity(new Intent(requireContext().getApplicationContext(), PersonsListActivity.class));
                } else {
                    listener.fetchSections();
                }
            }
            dismiss();

        });

        binding.scTv.setOnClickListener(v -> {
            // show code
            dismiss();
            ShowCodeDialog dialog = new ShowCodeDialog();
            dialog.show(requireActivity().getSupportFragmentManager(), "Show code dialog");
        });

        binding.deleteTv.setOnClickListener(v -> {
            deleteData();
        });

        return builder.create();
    }

    private void deleteData() {
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

                                            for (int i = 0; i < tempM.size(); i++) {


                                                if (DepartmentsActivity.isDepartment) {
                                                    if (tempM.get(i).getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {

                                                        tempM.remove(i);

                                                        break;
                                                    }

                                                } else {

                                                    if (tempM.get(i).getCode().equals(DataCenter.branchCode)) {
                                                        if (DepartmentsActivity.isSections) {
                                                            if (tempM.get(i).getSectionsList() != null) {
                                                                for (int j = 0; j < tempM.get(i).getSectionsList().size(); j++) {
                                                                    if (tempM.get(i).getSectionsList().get(j).getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                                        tempM.get(i).getSectionsList().remove(j);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        } else
                                                            tempM.remove(i);
                                                        break;
                                                    }
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
                                if (DepartmentsActivity.isSections) {
                                    listener.fetchSections();
                                } else
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


    public interface DepartmentDialogListener {
        void fetchSections();

        void fetchData();
    }
}
