package com.matrix_maeny.attendance.collage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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
import com.matrix_maeny.attendance.admin.dialogs.PersonCreationDialog;
import com.matrix_maeny.attendance.admin.dialogs.PersonDialog;
import com.matrix_maeny.attendance.collage.adapters.PersonsAdapter;
import com.matrix_maeny.attendance.collage.models.AttendanceSheetModel;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.ActivityPersonsListBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonsListActivity extends AppCompatActivity implements PersonCreationDialog.PersonCreationDialogListener, PersonsAdapter.PersonsAdapterListener,
        PersonDialog.PersonDialogListener {

    private ActivityPersonsListBinding binding;

    private FirebaseDatabase firebaseDatabase;
    //    private String currentUid;
    private ExposeDialogs exposeDialogs;

    private List<PersonModel> list;
    private List<PersonModel> tempList;
    private PersonsAdapter adapter;
    private String searchQuery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);

        if (DepartmentsActivity.isDepartment) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Faculty List");
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Student List");
        }

        initialize();

        binding.swipeRefreshLayout.setOnRefreshListener(this::fetchData);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.equals("")) {
                    searchQuery = query.toLowerCase();
                    searchPerson();
                } else {
                    exposeDialogs.showToast("Please enter something to search", 1);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText.trim().toLowerCase();
                searchPerson();
                return true;
            }
        });

        binding.goBtn.setOnClickListener(v -> {
            if (searchQuery != null) {
                if (!searchQuery.equals("")) {
                    searchPerson();
                }
            } else {
                exposeDialogs.showToast("Please enter something to search", 1);
            }
        });


        binding.takeAttendanceBtn.setOnClickListener(v -> takeAttendance());

    }


    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(PersonsListActivity.this);
        exposeDialogs.setAllCancellable(false);

        list = new ArrayList<>();
        tempList = new ArrayList<>();
        adapter = new PersonsAdapter(PersonsListActivity.this, list);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PersonsListActivity.this));
        binding.recyclerView.setAdapter(adapter);

        fetchData();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // create persons

        switch (item.getItemId()) {
            case R.id.person_add:
                PersonCreationDialog dialog = new PersonCreationDialog();
                dialog.show(getSupportFragmentManager(), "Persons Dialog");
                break;
            case R.id.person_search:
                setToolbarVisibilities(true);
                break;
            case R.id.delete_sheet:
                deleteAttendanceSheet();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAttendanceSheet() {
        new AlertDialog.Builder(PersonsListActivity.this)
                .setTitle("Are you sure..?")
                .setMessage("All attendance records till now are deleted permanently..!")
                .setPositiveButton("ok", (dialog, which) -> deleteSheet())
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void deleteSheet() {
        exposeDialogs.showProgressDialog("Deleting attendance sheet..", "wait...");
        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CollageModel collageModel = snapshot.getValue(CollageModel.class);

                if (collageModel != null) {

                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {

                        if (collageModel.getYears() != null) {

                            abc:
                            for (YearClassModel ycm : collageModel.getYears()) {

                                if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                    List<SectionDepartmentModel> sectionDepartmentModels;

                                    if (DepartmentsActivity.isDepartment) {
                                        sectionDepartmentModels = ycm.getDepartmentsList();
                                    } else {
                                        sectionDepartmentModels = ycm.getBranchList();
                                    }

                                    if (sectionDepartmentModels != null) {


                                        for (SectionDepartmentModel sdm : sectionDepartmentModels) {

                                            if (DepartmentsActivity.isDepartment) {
                                                if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {


                                                    if (sdm.getAttendances() != null) {

                                                        sdm.getAttendances().clear();
                                                        break abc;
                                                    }

                                                    break abc;
                                                }
                                            } else {

                                                if (sdm.getCode().equals(DataCenter.branchCode)) {
                                                    if (sdm.getSectionsList() != null) {
                                                        for (SectionDepartmentModel sections : sdm.getSectionsList()) {

                                                            if (sections.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                                if (sections.getAttendances() != null) {
                                                                    sections.getAttendances().clear();
                                                                    break abc;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    break abc;

                                                }
                                            }
                                        }
                                    }

                                    if (DepartmentsActivity.isDepartment) {
//                        collageModel.setSubmittedCountDepartment(0);
//                                        collageModel.setSubmittedDateDepartment("No Date");
                                        ycm.setSubmittedCountDepartment(0);
//                        DataCenter.submittedDateDepartment = collageModel.getSubmittedDateDepartment();
                                    } else {
//                        collageModel.setSubmittedCountStudent(0);
//                                        collageModel.setSubmittedDateStudents("No Date");
//                        DataCenter.submittedDateStudent = collageModel.getSubmittedDateStudents();
                                        ycm.setSubmittedCountStudent(0);
                                    }
                                    break;
                                }
                                break;
                            }
                        }

                    }

                    if (collageModel.getAttendanceSheets() != null) {

                        for (int i = 0; i < collageModel.getAttendanceSheets().size(); i++) {
                            if (collageModel.getAttendanceSheets().get(i).getWhom().equals(DataCenter.selectedDepartSectionModel.getType())) {

                                collageModel.getAttendanceSheets().remove(i);
                                break;
                            }
                        }
                    }


                    reference.setValue(collageModel).addOnCompleteListener(task -> {

                        exposeDialogs.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            exposeDialogs.showToast("Deleted successfully", 1);
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

    private void searchPerson() {
        list.clear();
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getPersonName().toLowerCase().contains(searchQuery) ||
                    searchQuery.contains(tempList.get(i).getPersonName().toLowerCase())
                    || tempList.get(i).getPersonId().toLowerCase().contains(searchQuery) ||
                    searchQuery.contains(tempList.get(i).getPersonId().toLowerCase())) {
                list.add(tempList.get(i));
            }
        }

        refreshAdapter();
    }

    private void setToolbarVisibilities(boolean shouldSearch) {
        if (shouldSearch) {
            binding.toolbar.setVisibility(View.INVISIBLE);
            binding.searchLayout.setVisibility(View.VISIBLE);
            binding.searchView.requestFocus();
        } else {
            binding.toolbar.setVisibility(View.VISIBLE);
            binding.searchLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void fetchData() {

        exposeDialogs.setAllCancellable(false);
        exposeDialogs.showProgressDialog("Fetching data..", "wait...");

        final DatabaseReference reference = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                tempList.clear();
                CollageModel model = snapshot.getValue(CollageModel.class);

                if (model != null) {
//                    DataCenter.submittedDateDepartment = model.getSubmittedDateDepartment();


                    if (model.getAttendanceSheets() != null) {
                        for (int i = 0; i < model.getAttendanceSheets().size(); i++) {
                            if (model.getAttendanceSheets().get(i).getWhom().equals(DataCenter.selectedDepartSectionModel.getType())) {
                                DataCenter.attendanceSheetModel = model.getAttendanceSheets().get(i);
                                break;
                            }
                        }
                    }

                    if (DataCenter.selectedYearClassModel.getType().equals("year")) {
                        abc:
                        for (YearClassModel classModel : model.getYears()) {

                            if (classModel.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                List<SectionDepartmentModel> sectionDepartmentModel;
                                if (DepartmentsActivity.isDepartment) {
                                    sectionDepartmentModel = classModel.getDepartmentsList();
                                } else {
                                    sectionDepartmentModel = classModel.getBranchList();
                                }

                                for (SectionDepartmentModel departmentModel : sectionDepartmentModel) {

                                    if (DepartmentsActivity.isDepartment) {
                                        if (departmentModel.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                            Objects.requireNonNull(getSupportActionBar()).setTitle("Fc : "+departmentModel.getName());
                                            if (departmentModel.getPersonsList() != null) {
                                                list.addAll(departmentModel.getPersonsList());
                                                tempList.addAll(list);
                                            }

                                            break abc;
                                        }
                                    } else {
                                        if (departmentModel.getCode().equals(DataCenter.branchCode)) {

                                            if (departmentModel.getSectionsList() != null) {
                                                for (SectionDepartmentModel sdm : departmentModel.getSectionsList()) {
                                                    if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                        Objects.requireNonNull(getSupportActionBar()).setTitle("St : "+sdm.getName());
                                                        if (sdm.getPersonsList() != null) {
                                                            list.addAll(sdm.getPersonsList());
                                                            tempList.addAll(list);
                                                        }
                                                        break abc;
                                                    }
                                                }
                                            }
                                        }// else exposeDialogs.showToast("No 1", 1);

                                    }
                                }
                            }
                        }

                    }// else class


                }

                exposeDialogs.dismissProgressDialog();
                binding.swipeRefreshLayout.setRefreshing(false);
                refreshAdapter();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });


    }


    private void takeAttendance() {
        exposeDialogs.showProgressDialog("Loading...", "checking attendance sheets");
        final DatabaseReference collageRef = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(DataCenter.collageCode);
//        final DatabaseReference attendanceRef = firebaseDatabase.getReference().child("Collages")
//                .child(DataCenter.collageCode).child("AttendanceSheets");

        collageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()) {
                    CollageModel collageModel = snapshot.getValue(CollageModel.class);

                    if (collageModel != null) {

                        if (collageModel.getAttendanceSheets() != null) {
                            for (AttendanceSheetModel model : collageModel.getAttendanceSheets()) {

                                if (DataCenter.selectedDepartSectionModel.getType().equalsIgnoreCase(model.getWhom())) {
                                    DataCenter.attendanceSheetModel = model;
                                    startActivity(new Intent(PersonsListActivity.this, TakeAttendanceActivity.class));
                                    exposeDialogs.dismissProgressDialog();
                                    return;
                                }
                            }


                        }

                        exposeDialogs.dismissProgressDialog();
                        showDialogForAttendanceCreation();
                    }

                } else {
                    // you didn't create any attendance sheet
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast("No Collage found", 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 1);
            }
        });
    }

    private void showDialogForAttendanceCreation() {
        new AlertDialog.Builder(PersonsListActivity.this)
                .setTitle("No Attendance record found..!")
                .setMessage("There is no Attendance record found for " + DataCenter.selectedDepartSectionModel.getType())
                .setPositiveButton("Create one", (dialog, which) -> {

                    AttendanceCreationActivity.whom = DataCenter.selectedDepartSectionModel.getType();
                    startActivity(new Intent(PersonsListActivity.this, AttendanceCreationActivity.class));
                    dialog.dismiss();

                })
                .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss()).create().show();

    }


    @SuppressLint("NotifyDataSetChanged")
    private void refreshAdapter() {
        adapter.notifyDataSetChanged();

        TakeAttendanceActivity.personsList = list;
        if (list.isEmpty()) {
            binding.emptyTv.setVisibility(View.VISIBLE);
            binding.takeAttendanceBtn.setVisibility(View.GONE);
        } else {
            binding.emptyTv.setVisibility(View.GONE);
            binding.takeAttendanceBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showPersonDialog() {
        PersonDialog dialog = new PersonDialog();
        dialog.show(getSupportFragmentManager(), "Person dialog");
    }


    @Override
    public void onBackPressed() {
        if (binding.toolbar.getVisibility() == View.INVISIBLE) {
            setToolbarVisibilities(false);
            fillAll();
        } else super.onBackPressed();
    }

    private void fillAll() {
        list.clear();
        list.addAll(tempList);
        refreshAdapter();
    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchData();
    }
}