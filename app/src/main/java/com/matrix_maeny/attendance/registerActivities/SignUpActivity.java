package com.matrix_maeny.attendance.registerActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.UserModel;
import com.matrix_maeny.attendance.databinding.ActivitySignUpBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;
import com.matrix_maeny.attendance.collage.activities.ChooserActivity;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {


    private ActivitySignUpBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ExposeDialogs exposeDialogs;

    private String username = null, email = null, password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Objects.requireNonNull(getSupportActionBar()).hide(); // for hiding toolbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // to create a translucent status of status bar

        FirebaseApp.initializeApp(SignUpActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        // a function to initialize everything
        initialize();
    }


    private void initialize() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        exposeDialogs = new ExposeDialogs(SignUpActivity.this);

        binding.suLoginTv.setOnClickListener(suLoginTvListener);
        binding.suSignUpBtn.setOnClickListener(suSignUpBtnListener);
    }


    View.OnClickListener suSignUpBtnListener = v -> signUp();
    View.OnClickListener suLoginTvListener = v -> {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    };


    private void signUp(){

        if(checkUsername() && checkEmail() && checkPassword()){

           exposeDialogs.showProgressDialog("Creating Account...","Please wait few seconds");
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    UserModel model = new UserModel(username, email, password);
                    DataCenter.model = model;
                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();

                    firebaseDatabase.getReference().child("Users").child(uid).setValue(model).addOnCompleteListener(task1 -> {
                        try {
                            exposeDialogs.dismissProgressDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (task1.isSuccessful()) {

                            Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, ChooserActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    });
                }

            }).addOnFailureListener(e -> {
                Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                   exposeDialogs.dismissProgressDialog();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }


    private boolean checkUsername() {
        try {
            username = Objects.requireNonNull(binding.suUserNameEt.getText()).toString();
            if (!username.equals("")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();

        return false;
    }

    private boolean checkEmail() {
        try {
            email = Objects.requireNonNull(binding.suEmailEt.getText()).toString();
            if (!email.equals("")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkPassword() {
        try {
            password = Objects.requireNonNull(binding.suPasswordEt.getText()).toString();
            if (!password.equals("")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
        return false;
    }
}