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
import com.matrix_maeny.attendance.databinding.YearClassDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.Objects;

public class YearClassDialog extends AppCompatDialogFragment {


    private YearClassDialogBinding binding;
    public static boolean isYear = true;

    private FirebaseDatabase firebaseDatabase;
    private ExposeDialogs exposeDialogs;

    private YearClassDialogListener listener;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.year_class_dialog, null);
        binding = YearClassDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());
        listener = (YearClassDialogListener) requireContext();

        if (isYear) {
            binding.depTv.setText("Departments");
            binding.secTv.setText("Branches");
        } else {
            binding.depTv.setText("Faculty");
            binding.secTv.setText("Sections");
        }

        binding.depTv.setOnClickListener(v -> {
            // go to departments activity
            if (isYear) {
                DepartmentsActivity.isDepartment = true;
                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), DepartmentsActivity.class));
            } else {
                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), PersonsListActivity.class));
            }
            dismiss();
        });
        binding.secTv.setOnClickListener(v -> {
            // go to sections activity
            if (isYear) {
                DepartmentsActivity.isDepartment = false;
                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), DepartmentsActivity.class));
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


                        if (collageModel.getYears() != null) {

                            for (int i = 0; i < collageModel.getYears().size(); i++) {

                                if (collageModel.getYears().get(i).getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                    collageModel.getYears().remove(i);
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

                        // we can have many years,, so how you can keep days count or periods count
//                        collageModel.setSubmittedCountStudent(0);
//                        collageModel.setSubmittedCountDepartment(0);
                        reference.setValue(collageModel).addOnCompleteListener(task -> {

                            exposeDialogs.dismissProgressDialog();
                            if (task.isSuccessful()) {
                                exposeDialogs.showToast("Deleted successfully", 1);

                                listener.fetchData();
                                dismiss();
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


    public interface YearClassDialogListener {
        void fetchData();
    }

}
