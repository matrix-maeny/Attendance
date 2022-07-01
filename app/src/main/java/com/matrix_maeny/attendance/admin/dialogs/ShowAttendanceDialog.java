package com.matrix_maeny.attendance.admin.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.databinding.ShowAttendanceDialogBinding;

public class ShowAttendanceDialog extends AppCompatDialogFragment {

    private ShowAttendanceDialogBinding binding;
    public static double percentage = 0;
    public static boolean isPeriods =false;
    public static long presentDays = 0;
    public static long totalDays = 0;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.show_attendance_dialog, null);
        binding = ShowAttendanceDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        if (isPeriods) {
            binding.presentClassesTv.setText("Attended Periods : "+presentDays);
            binding.totalClassesTv.setText("Total Periods : "+totalDays);
        }else{
            binding.presentClassesTv.setText("Attended Days : "+presentDays);
            binding.totalClassesTv.setText("Total Days: "+totalDays);
        }
        binding.percentageTv.setText(percentage+"%");
        return builder.create();
    }
}
