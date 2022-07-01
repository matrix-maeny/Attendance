package com.matrix_maeny.attendance.admin.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.collage.activities.TakeAttendanceActivity;
import com.matrix_maeny.attendance.collage.models.AttendanceModel;
import com.matrix_maeny.attendance.databinding.AttendancePeriodDialogBinding;

public class AttendancePeriodDialog extends AppCompatDialogFragment {

    private AttendancePeriodDialogBinding binding;

    public static AttendanceModel attendanceModel;

    private CheckBox[] periods;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.attendance_period_dialog, null);
        binding = AttendancePeriodDialogBinding.bind(root);
        builder.setView(binding.getRoot());
        setCancelable(false);
        periods = new CheckBox[]{binding.period1, binding.period2, binding.period3, binding.period4, binding.period5,
                binding.period6, binding.period7, binding.period8, binding.period9, binding.period10};


        for (int i = 0; i < DataCenter.attendanceSheetModel.getNoOfPeriods(); i++) {
            periods[i].setVisibility(View.VISIBLE);

        }

        periods[0].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 0);
        });
        periods[1].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 1);
        });
        periods[2].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 2);
        });
        periods[3].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 3);
        });
        periods[4].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 4);
        });
        periods[5].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 5);
        });
        periods[6].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 6);
        });
        periods[7].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 7);
        });
        periods[8].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 8);
        });
        periods[9].setOnCheckedChangeListener((buttonView, isChecked) -> {

            present(isChecked, 9);
        });


        binding.setBtn.setOnClickListener(v -> dismiss());


        return builder.create();
    }

    private void present(boolean isChecked, int period) {

        if (isChecked) {
            attendanceModel.addPeriod("Period " + (period + 1));
        } else {
            attendanceModel.getPeriodsPresent().remove(period);
        }
    }
}
