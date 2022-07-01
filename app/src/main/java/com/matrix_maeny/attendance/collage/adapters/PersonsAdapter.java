package com.matrix_maeny.attendance.collage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.admin.dialogs.ShowCodeDialog;
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.databinding.AdminModelBinding;

import java.util.Arrays;
import java.util.List;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.viewHolder> {


    private final Context context;
    private final List<PersonModel> list;

    private final PersonsAdapterListener listener;

    public PersonsAdapter(@NonNull Context context, List<PersonModel> list) {
        this.context = context;
        this.list = list;

        listener = (PersonsAdapterListener) context;


    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_model, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PersonModel model = list.get(position);

        holder.binding.nameTv.setText(model.getPersonName());

        holder.binding.cardView.setOnClickListener(v -> {
            ShowCodeDialog.code = model.getPersonId();
            DataCenter.selectedPersonModel = model;
            listener.showPersonDialog();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface PersonsAdapterListener {
        void showPersonDialog();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        AdminModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AdminModelBinding.bind(itemView);
        }
    }

}
