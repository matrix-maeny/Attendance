package com.matrix_maeny.attendance.admin.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CompoundButton;

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
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.CreationDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.Objects;

public class CreationDialog extends AppCompatDialogFragment {

    private CreationDialogBinding binding;


    private FirebaseDatabase firebaseDatabase;
    private String currentUid;
    private String name;
    private String code;
    private ExposeDialogs exposeDialogs;

    private CreationDialogListener listener;

    private boolean isYear = true;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.creation_dialog, null);
        binding = CreationDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        listener = (CreationDialogListener) requireContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        exposeDialogs = new ExposeDialogs(requireContext());
//        binding.yearRadioBtn.setChecked(true);
//
//        binding.yearRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
//
//            if(isChecked){
//                isYear = true;
//                binding.classRadioBtn.setChecked(false);
//                binding.nameLayout.setHint(getString(R.string.enter_year_name_ex_1st_year));
//            }
//
//        });
//        binding.classRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked){
//                isYear = false;
//                binding.yearRadioBtn.setChecked(false);
//                binding.nameLayout.setHint(getString(R.string.enter_class_name_ex_1st_class));
//            }
//        });


        binding.addBtn.setOnClickListener(v -> {
            if (checkName() && checkCode()) {
                addData();
            }
        });


        return builder.create();
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
            name = Objects.requireNonNull(binding.createYCEt.getText()).toString().trim();
            if (!name.equals("")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter name", 1);

        return false;
    }

    private void addData() {
        exposeDialogs.showProgressDialog("Creating...", "wait...");
        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CollageModel collageModel = snapshot.getValue(CollageModel.class);

                if (collageModel != null) {


                    if (collageModel.getYears() != null) {
                        for (YearClassModel model : collageModel.getYears()) {
                            if (model.getName().equalsIgnoreCase(name)) {
                                exposeDialogs.dismissProgressDialog();
                                exposeDialogs.showToast("Name already taken", 1);
                                return;
                            }
                            if (model.getCode().equals(code)) {
                                exposeDialogs.dismissProgressDialog();
                                exposeDialogs.showToast("Code already taken", 1);
                                return;
                            }

                        }
                    }
                    collageModel.addYear(new YearClassModel(name, code, "year"));

                    addToFirebase(collageModel);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });

//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String collageCode = null;
//                if (snapshot.exists()) {
//
//                    for (DataSnapshot s : snapshot.getChildren()) {
//                        collageCode = s.getKey();
//                    }
//
//                    if (collageCode != null) {
//                        String finalCollageCode = collageCode;
//                    }
//                } else exposeDialogs.dismissProgressDialog();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                exposeDialogs.dismissProgressDialog();
//                exposeDialogs.showToast(error.getMessage(), 1);
//            }
//        });

    }

    private void addToFirebase(CollageModel collageModel) {
        firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode)
                .setValue(collageModel).addOnCompleteListener(task -> {
                    exposeDialogs.dismissProgressDialog();
                    dismiss();

                    if (task.isSuccessful()) {
                        exposeDialogs.showToast("Created successfully", 1);
                        listener.fetchData();
                    } else {
                        exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
                    }

                }).addOnFailureListener(e -> {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast(e.getMessage(), 0);
                });
    }

    public interface CreationDialogListener {
        void fetchData();
    }


}
