package com.matrix_maeny.attendance.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.AboutActivity;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.UserModel;
import com.matrix_maeny.attendance.admin.dialogs.CreationDialog;
import com.matrix_maeny.attendance.admin.dialogs.YearClassDialog;
import com.matrix_maeny.attendance.collage.activities.ChooserActivity;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.ActivityAdminBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;
import com.matrix_maeny.attendance.registerActivities.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements AdminAdapter.AdminAdapterListener, CreationDialog.CreationDialogListener
        , YearClassDialog.YearClassDialogListener {

    private ActivityAdminBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private String currentUid;
    private ExposeDialogs exposeDialogs;

    private List<YearClassModel> list;
    private AdminAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        FirebaseApp.initializeApp(AdminActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        initialize();

    }

    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(AdminActivity.this);

        list = new ArrayList<>();
        adapter = new AdminAdapter(AdminActivity.this, list);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(AdminActivity.this, 2));
        binding.recyclerView.setAdapter(adapter);


        fetchData();
    }

    @Override
    public void fetchData() {
        exposeDialogs.setAllCancellable(false);
        exposeDialogs.showProgressDialog("Fetching data..", "wait...");
        final DatabaseReference collageReference = firebaseDatabase.getReference().child("Collages").child(currentUid);
        final DatabaseReference userReference = firebaseDatabase.getReference().child("Users").child(currentUid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String collageCode;
                if (snapshot.exists()) {
                    UserModel model = snapshot.getValue(UserModel.class);

                    if (model != null) {
                        collageCode = model.getCollageCode();
                        DataCenter.collageCode = collageCode;

                        if (collageCode != null) {
                            collageReference.child(collageCode).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    list.clear();
                                    if (snapshot.exists()) {
                                        CollageModel collageModel = snapshot.getValue(CollageModel.class);
                                        if (collageModel != null) {

//                                            if (!DataCenter.submittedDateDepartment.equals(collageModel.getSubmittedDateDepartment())) {
//                                                DataCenter.submittedDateDepartment = collageModel.getSubmittedDateDepartment();
//                                            }

                                            if (collageModel.getClasses() != null) {
                                                list.addAll(collageModel.getClasses());
                                            }
                                            if (collageModel.getYears() != null) {
                                                list.addAll(collageModel.getYears());
                                            }
                                        }
                                    } else {
                                        exposeDialogs.dismissProgressDialog();
                                        exposeDialogs.showToast("No collage found", 1);
                                        startActivity(new Intent(AdminActivity.this, ChooserActivity.class));
                                        finish();
                                    }
                                    exposeDialogs.dismissProgressDialog();
                                    refreshAdapter();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    exposeDialogs.dismissProgressDialog();
                                    exposeDialogs.showToast(error.getMessage(), 1);
                                }
                            });
                        }
                    }
                } else {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast("No User found", 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });


//        collageReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                String collageCode = null;
//                if (snapshot.exists()) {
//
//                    for (DataSnapshot s : snapshot.getChildren()) {
//                        collageCode = s.getKey();
//                        DataCenter.collageCode = collageCode;
//                    }
//
//                    if (collageCode != null) {
//                        collageReference.child(collageCode).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                if (snapshot.exists()) {
//                                    CollageModel collageModel = snapshot.getValue(CollageModel.class);
//
//                                    if (collageModel != null) {
//                                        list.addAll(collageModel.getClasses());
//                                        list.addAll(collageModel.getYears());
//                                    }
//                                }
//                                exposeDialogs.dismissProgressDialog();
//                                refreshAdapter();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                exposeDialogs.dismissProgressDialog();
//                                exposeDialogs.showToast(error.getMessage(), 1);
//                            }
//                        });
//                    }
//
//                } else {
//                    exposeDialogs.dismissProgressDialog();
//                    exposeDialogs.showToast("No collage found", 1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                exposeDialogs.dismissProgressDialog();
//                exposeDialogs.showToast(error.getMessage(), 1);
//            }
//        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            binding.emptyTv.setVisibility(View.VISIBLE);
        } else binding.emptyTv.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.admin_create:
                CreationDialog creationDialog = new CreationDialog();
                creationDialog.show(getSupportFragmentManager(), "Create");
                break;
            case R.id.log_out:
                logout();
                break;
            case R.id.about_app:
                startActivity(new Intent(AdminActivity.this, AboutActivity.class));
                break;
        }
//        if (item.getItemId() == R.id.admin_create) {// create something
//            CreationDialog creationDialog = new CreationDialog();
//            creationDialog.show(getSupportFragmentManager(), "Create");
//        } else {
//            logout();
//
//        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();

        exposeDialogs.showProgressDialog("Logging out", "Please wait");

        new Handler().postDelayed(() -> {
            exposeDialogs.dismissProgressDialog();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        }, 1500);
    }

    @Override
    public void yearClassDialog() {
        YearClassDialog yearClassDialog = new YearClassDialog();
        yearClassDialog.show(getSupportFragmentManager(), "Year class dialog");
    }
}