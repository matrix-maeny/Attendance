package com.matrix_maeny.attendance.collage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.admin.dialogs.ShowCodeDialog;
import com.matrix_maeny.attendance.collage.activities.DepartmentsActivity;
import com.matrix_maeny.attendance.collage.models.SectionDepartmentModel;
import com.matrix_maeny.attendance.databinding.AdminModelBinding;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.viewHolder> {

    private final Context context;
    private final List<SectionDepartmentModel> list;

    private final DepartmentAdapterListener listener;


    public DepartmentAdapter(Context context, List<SectionDepartmentModel> list) {
        this.context = context;
        this.list = list;

        listener = (DepartmentAdapterListener) context;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_model, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SectionDepartmentModel model = list.get(position);

        holder.binding.nameTv.setText(model.getName());

        holder.binding.cardView.setOnClickListener(v -> {
            ShowCodeDialog.code = model.getCode();

//                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
            if (!DepartmentsActivity.isSections) {
                DataCenter.branchCode = model.getCode();
            }
            DataCenter.selectedDepartSectionModel = model;
            listener.showDepartmentDialog();
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface DepartmentAdapterListener {
        void showDepartmentDialog();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        AdminModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AdminModelBinding.bind(itemView);
        }
    }

}
