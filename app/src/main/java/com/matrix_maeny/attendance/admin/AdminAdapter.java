package com.matrix_maeny.attendance.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.admin.dialogs.ShowCodeDialog;
import com.matrix_maeny.attendance.admin.dialogs.YearClassDialog;
import com.matrix_maeny.attendance.collage.models.YearClassModel;
import com.matrix_maeny.attendance.databinding.AdminModelBinding;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.viewHolder> {

    private final Context context;
    private final List<YearClassModel> list;
    private final AdminAdapterListener listener;


    public AdminAdapter(Context context, List<YearClassModel> list) {
        this.context = context;
        this.list = list;

        listener = (AdminAdapterListener) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_model, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        YearClassModel model = list.get(position);

        holder.binding.nameTv.setText(model.getName());

        holder.binding.cardView.setOnClickListener(v -> {
            ShowCodeDialog.code = model.getCode();
            DataCenter.selectedYearClassModel = model;

            YearClassDialog.isYear = model.getType().equals("year");
            listener.yearClassDialog();

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface AdminAdapterListener {
        void yearClassDialog();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        AdminModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AdminModelBinding.bind(itemView);
        }
    }
}
