package com.matrix_maeny.attendance.collage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.admin.dialogs.AttendancePeriodDialog;
import com.matrix_maeny.attendance.collage.adapters.AttendanceAdapter;
import com.matrix_maeny.attendance.collage.models.AttendanceModel;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.ActivityTakeAttendanceBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TakeAttendanceActivity extends AppCompatActivity implements AttendanceAdapter.AttendanceAdapterListener {

    private ActivityTakeAttendanceBinding binding;
    public static List<PersonModel> personsList;

    private FirebaseDatabase firebaseDatabase;
    //    private String currentUid;
    private ExposeDialogs exposeDialogs;

    public static final List<AttendanceModel> attendanceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakeAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Today: " + LocalDate.now());

        initialize();

        binding.submitAttendanceBtn.setOnClickListener(v -> new AlertDialog.Builder(TakeAttendanceActivity.this)
                .setTitle("Are you sure to submit?")
                .setMessage("Once you submit, you can't modify..!")
                .setPositiveButton("Submit", (dialog, which) -> submitAttendance())
                .setNegativeButton("Wait", (dialog, which) -> dialog.dismiss())
                .create().show());

    }


    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
//        currentUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(TakeAttendanceActivity.this);

        AttendanceAdapter adapter = new AttendanceAdapter(TakeAttendanceActivity.this, personsList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(TakeAttendanceActivity.this));
        binding.recyclerView.setAdapter(adapter);
    }


    private void submitAttendance() {
        exposeDialogs.showProgressDialog("Submitting...", "please wait...");

        final DatabaseReference collageRef = firebaseDatabase.getReference().child("Collages").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(DataCenter.collageCode);

        collageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CollageModel collageModel = snapshot.getValue(CollageModel.class);

                    if (collageModel != null) {
                        // year
                        if (DataCenter.selectedYearClassModel.getType().equals("year")) {

                            if (collageModel.getYears() != null) {

                                abc:
                                for (YearClassModel ycm : collageModel.getYears()) {

                                    if (ycm.getCode().equals(DataCenter.selectedYearClassModel.getCode())) {

                                        List<SectionDepartmentModel> sectionDepartmentModels;

                                        if (DepartmentsActivity.isDepartment) {
                                            ycm.setSubmittedCountDepartment(ycm.getSubmittedCountDepartment() + 1);
                                            sectionDepartmentModels = ycm.getDepartmentsList();
                                        } else {
                                            sectionDepartmentModels = ycm.getBranchList();
                                            ycm.setSubmittedCountStudent(ycm.getSubmittedCountStudent() + 1);
                                        }


                                        if (sectionDepartmentModels != null) {

                                            for (SectionDepartmentModel sdm : sectionDepartmentModels) {

                                                if (DepartmentsActivity.isDepartment) {
                                                    if (sdm.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                        sdm.setAttendances(attendanceList);
                                                        break abc;
                                                    }
                                                } else {

                                                    if (sdm.getCode().equals(DataCenter.branchCode)) {

                                                        if (sdm.getSectionsList() != null) {
                                                            for (SectionDepartmentModel sections : sdm.getSectionsList()) {

                                                                if (sections.getCode().equals(DataCenter.selectedDepartSectionModel.getCode())) {
                                                                    sections.setAttendances(attendanceList);
                                                                    break abc;
                                                                }
                                                            }
                                                        }
                                                        break abc;
                                                    }
                                                }

                                            }
                                        }
//



                                        break;
                                    }
                                }
                            }

                        }


                        collageRef.setValue(collageModel).addOnCompleteListener(task -> {

                            exposeDialogs.dismissProgressDialog();

                            if (task.isSuccessful()) {
//                                exposeDialogs.showToast("Submitted successfully", 1);
                                finish();
                            } else {
                                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);

                            }

                        }).addOnFailureListener(e -> {
                            exposeDialogs.dismissProgressDialog();
                            exposeDialogs.showToast(e.getMessage(), 1);
                        });
                    }
                } else {
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

    @Override
    public void showAttendancePeriodDialog() {
        AttendancePeriodDialog dialog = new AttendancePeriodDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "Attendance Period Dialog");
    }
}