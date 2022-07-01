package com.matrix_maeny.attendance.collage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.admin.dialogs.DepartmentCreationDialog;
import com.matrix_maeny.attendance.admin.dialogs.DepartmentDialog;
import com.matrix_maeny.attendance.collage.adapters.DepartmentAdapter;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.ActivityDepartmentsBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DepartmentsActivity extends AppCompatActivity implements DepartmentCreationDialog.DepartmentCreationDialogListener
        , DepartmentAdapter.DepartmentAdapterListener, DepartmentDialog.DepartmentDialogListener {

    private ActivityDepartmentsBinding binding;


    private FirebaseDatabase firebaseDatabase;
//    private String currentUid;
    private ExposeDialogs exposeDialogs;

    private List<SectionDepartmentModel> list;
    private DepartmentAdapter adapter;

    public static boolean isDepartment = true;
    public static boolean isSections = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDepartmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

//        if (!isDepartment) {
//            Objects.requireNonNull(getSupportActionBar()).setTitle("Branches");
//        }

        initialize();
    }

    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(DepartmentsActivity.this);

        list = new ArrayList<>();
        adapter = new DepartmentAdapter(DepartmentsActivity.this, list);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(DepartmentsActivity.this, 2));
        binding.recyclerView.setAdapter(adapter);

        fetchData();
    }

    @Override
    public void fetchData() {
        isSections = false;
        exposeDialogs.setAllCancellable(false);
        exposeDialogs.showProgressDialog("Fetching data..", "wait...");

        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                CollageModel model = snapshot.getValue(CollageModel.class);

                if (model != null) {
                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {
                        for (YearClassModel classModel : model.getYears()) {

                            if (classModel.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {
                                if (isDepartment) {
                                    Objects.requireNonNull(getSupportActionBar()).setTitle("D : "+classModel.getName());
                                    if (classModel.getDepartmentsList() != null) {
                                        list.addAll(classModel.getDepartmentsList());
                                    }
                                } else {
                                    Objects.requireNonNull(getSupportActionBar()).setTitle("B : "+classModel.getName());
                                    if (classModel.getBranchList() != null) {
                                        list.addAll(classModel.getBranchList());
                                    }
                                }
                                break;
                            }
                        }
                    }// else class


                }


                exposeDialogs.dismissProgressDialog();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Departments");
                refreshAdapter();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });
    }

    @Override
    public void fetchSections() {
        isSections = true;
        exposeDialogs.setAllCancellable(false);
        exposeDialogs.showProgressDialog("Fetching data..", "wait...");

        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                CollageModel model = snapshot.getValue(CollageModel.class);

                if (model != null) {
                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {
                        for (YearClassModel classModel : model.getYears()) {

                            if (classModel.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                if (classModel.getBranchList() != null) {

                                    for (SectionDepartmentModel sdm : classModel.getBranchList()) {
                                        if (sdm.getCode().equals(DataCenter.branchCode)) {
                                            Objects.requireNonNull(getSupportActionBar()).setTitle("S : "+sdm.getName());
                                            if (sdm.getSectionsList() != null) {
                                                list.addAll(sdm.getSectionsList());
                                            }
                                        }
                                    }
//                                        list.addAll(classModel.getBranchList());
                                }
                                break;
                            }
                        }
                    }// else class


                }


                exposeDialogs.dismissProgressDialog();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Sections");
                refreshAdapter();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshAdapter() {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // create departments
        DepartmentCreationDialog dialog = new DepartmentCreationDialog();
        dialog.show(getSupportFragmentManager(), "Department dialog");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDepartmentDialog() {
        DepartmentDialog dialog = new DepartmentDialog();
        dialog.show(getSupportFragmentManager(), "Department dialog");
    }

    @Override
    public void onBackPressed() {
        if (isSections) {
            isSections = false;
            fetchData();
        } else
            super.onBackPressed();
    }
}