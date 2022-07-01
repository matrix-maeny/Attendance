package com.matrix_maeny.attendance.collage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.UserModel;
import com.matrix_maeny.attendance.admin.AdminActivity;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.databinding.FragmentCreateCollageBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.Objects;


public class CreateCollageFragment extends Fragment {

    private FragmentCreateCollageBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private String collageName = null, collageCode = null;
    private ExposeDialogs exposeDialogs;

    private CreateCollageFragmentListener listener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateCollageBinding.inflate(inflater, container, false);

        initialize();

        return binding.getRoot();
    }

    private void initialize() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        listener = (CreateCollageFragmentListener) requireContext();
        exposeDialogs = new ExposeDialogs(requireContext());

        binding.createBtn.setOnClickListener(createBtnListener);

    }

    View.OnClickListener createBtnListener = v -> {
        if (checkCollageName() && checkCollageCode()) {
            exposeDialogs.showProgressDialog("Checking code...", "Please wait few seconds");
            firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(collageCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    exposeDialogs.dismissProgressDialog();
                    if (snapshot.exists()) {
                        exposeDialogs.showToast("Collage code exists, try another code", 1);
                    } else {
                        createCollage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            createCollage();

        }
    };

    private void createCollage() {
        exposeDialogs.showProgressDialog("Creating collage", "Please wait few seconds");

        CollageModel collageModel = new CollageModel(collageName, collageCode);
        firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(collageCode).setValue(collageModel)
                .addOnCompleteListener(task -> {

//                    exposeDialogs.dismissProgressDialog();

                    if (task.isSuccessful()) {
                        firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(firebaseAuth.getUid()))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                UserModel model = snapshot.getValue(UserModel.class);
                                                if(model != null){
                                                    model.setCollageCode(collageCode);
                                                    model.setUserType("admin");
                                                    DataCenter.model = model;

                                                    firebaseDatabase.getReference().child("Users")
                                                            .child(Objects.requireNonNull(firebaseAuth.getUid()))
                                                            .setValue(model).addOnSuccessListener(unused -> {
                                                                exposeDialogs.dismissProgressDialog();

                                                                exposeDialogs.showToast("Collage created successfully", 1);
                                                                requireContext().startActivity(new Intent(requireContext().getApplicationContext(), AdminActivity.class));
                                                                listener.finishActivity();
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                exposeDialogs.showToast(error.getMessage(), 0);
                                            }
                                        });


                    } else
                        exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 0);

                }).addOnFailureListener(e -> exposeDialogs.showToast(e.getMessage(), 0));
    }


    private boolean checkCollageName() {
        try {
            collageName = Objects.requireNonNull(binding.collageNameEt.getText()).toString();

            if (!collageName.equals(""))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        exposeDialogs.showToast("Please enter Collage name", 0);
        return false;
    }

    private boolean checkCollageCode() {
        try {
            collageCode = Objects.requireNonNull(binding.collageCodeEt.getText()).toString();
            if (collageCode.length() < 6) {
                exposeDialogs.showToast("Collage code must be greater than 6 characters", 1);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter Collage code", 0);

        return false;
    }

    public interface CreateCollageFragmentListener {
        void finishActivity();
    }

}