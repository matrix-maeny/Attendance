package com.matrix_maeny.attendance.collage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.collage.adapters.DepartmentAdapter;
import com.matrix_maeny.attendance.collage.models.AttendanceSheetModel;
import com.matrix_maeny.attendance.collage.models.CollageModel;
import com.matrix_maeny.attendance.databinding.ActivityAttendanceCreationBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.ArrayList;
import java.util.Objects;

public class AttendanceCreationActivity extends AppCompatActivity {

    private ActivityAttendanceCreationBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private String currentUid;
    private ExposeDialogs exposeDialogs;

    private final String[] noOfPeriods = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};
//    private final String[] forWhom = {"department","students"};
    public static String whom = DataCenter.selectedDepartSectionModel.getType();

    ArrayAdapter arrayAdapter;
//    ArrayAdapter whomAdapter;

//    public static String name;
    private boolean perDay = true;
    private int periodsNo = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        initialize();
        binding.perDayRadioBtn.setChecked(true);

        binding.perDayRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                perDay = true;
                binding.perPeriodRadioBtn.setChecked(false);
                binding.periodsSpinner.setVisibility(View.GONE);
            }
        });
        binding.perPeriodRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                perDay = false;
                binding.perDayRadioBtn.setChecked(false);
                binding.periodsSpinner.setVisibility(View.VISIBLE);
            }
        });

        binding.periodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                periodsNo = Integer.parseInt(noOfPeriods[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        binding.whomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                whom = forWhom[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        binding.sheetCreateBtn.setOnClickListener(v -> {
            createSheet();
        });
    }


    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(AttendanceCreationActivity.this);

        arrayAdapter = new ArrayAdapter(AttendanceCreationActivity.this, R.layout.spinner_model, noOfPeriods);
//        whomAdapter = new ArrayAdapter(AttendanceCreationActivity.this, R.layout.spinner_model_2, forWhom);

        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
//        whomAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        binding.periodsSpinner.setAdapter(arrayAdapter);
//        binding.whomSpinner.setAdapter(whomAdapter);

    }



    private void createSheet() {
            AttendanceSheetModel sheetModel = new AttendanceSheetModel((perDay) ? "d" : "p", periodsNo,whom);
            exposeDialogs.showProgressDialog("Creating sheet..", "wait few seconds...");

            final DatabaseReference collageReference = firebaseDatabase.getReference().child("Collages").child(currentUid)
                    .child(DataCenter.collageCode);

            collageReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        CollageModel model = snapshot.getValue(CollageModel.class);

                        if (model != null) {
//                            if (model.getAttendanceSheets() != null) {
//                                for (AttendanceSheetModel sheets : model.getAttendanceSheets()) {
//                                    if (sheets.getName().equalsIgnoreCase(name)) {
//                                        exposeDialogs.dismissProgressDialog();
//                                        exposeDialogs.showToast("Name already taken", 1);
//
//                                        return;
//                                    }
//                                }
//                            }

                            model.addSheet(sheetModel);

                            collageReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    exposeDialogs.dismissProgressDialog();
                                    if (task.isSuccessful()) {
                                        exposeDialogs.showToast("Created successfully", 1);
                                        finish();
                                    } else {
                                        exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
                                    }
                                }
                            }).addOnFailureListener(e -> {
                                exposeDialogs.dismissProgressDialog();
                                exposeDialogs.showToast(e.getMessage(), 1);
                            });
                        }
                    } else {
                        exposeDialogs.dismissProgressDialog();
                        exposeDialogs.showToast("No collage found",1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast(error.getMessage(), 1);
                }
            });
    }


}