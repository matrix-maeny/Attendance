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

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.databinding.ShowCodeDialogBinding;
import com.matrix_maeny.attendance.javaclasses.ExposeDialogs;

import java.util.Objects;

public class ShowCodeDialog extends AppCompatDialogFragment {


    ShowCodeDialogBinding binding;
    public static String code = null;

    public static boolean isPerson = false;


    private String password = null;
    private ExposeDialogs exposeDialogs;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.show_code_dialog, null);
        binding = ShowCodeDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        exposeDialogs = new ExposeDialogs(requireContext());


        if (!isPerson) {
            binding.showCodeTv.setVisibility(View.GONE);
            binding.passwordLayout.setVisibility(View.VISIBLE);
            binding.verifyBtn.setVisibility(View.VISIBLE);

            binding.verifyBtn.setOnClickListener(v -> {
                if (checkPassword()) {
                    if (DataCenter.model.getPassword().equals(password)) {
                        showCode();
                    }else{
                        exposeDialogs.showToast("Wrong password",1);
                    }
                }
            });
        }else{
            showCode();
            isPerson = false;
        }


        return builder.create();
    }

    @SuppressLint("SetTextI18n")
    private void showCode() {
        binding.passwordLayout.setVisibility(View.GONE);
        binding.verifyBtn.setVisibility(View.GONE);

        binding.verifyHTv.setText("Access Code");
        binding.showCodeTv.setText(code);
        binding.showCodeTv.setVisibility(View.VISIBLE);
    }

    private boolean checkPassword() {
        password = null;

        try {
            password = Objects.requireNonNull(binding.passwordEt.getText()).toString().trim();
            if (!password.equals(""))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter password", 1);

        return false;
    }


}
