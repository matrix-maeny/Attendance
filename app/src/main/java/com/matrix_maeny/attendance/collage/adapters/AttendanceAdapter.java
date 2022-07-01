package com.matrix_maeny.attendance.collage.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.attendance.DataCenter;
import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.admin.dialogs.AttendancePeriodDialog;
import com.matrix_maeny.attendance.collage.activities.TakeAttendanceActivity;
import com.matrix_maeny.attendance.collage.models.AttendanceModel;
import com.matrix_maeny.attendance.collage.models.PersonModel;
import com.matrix_maeny.attendance.databinding.AttendanceDayModelBinding;

import java.time.LocalDate;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.viewHolder> {


    private final Context context;
    private final List<PersonModel> list;

    private AttendanceAdapterListener listener;

    public AttendanceAdapter(Context context, List<PersonModel> list) {
        this.context = context;
        this.list = list;

        listener = (AttendanceAdapterListener) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_day_model, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        PersonModel model = list.get(position);


        holder.binding.nameTv.setText(model.getPersonId() + "|" + model.getPersonName());


        holder.binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // present
                AttendanceModel attendanceModel = new AttendanceModel(LocalDate.now().toString(), model.getPersonId());
                TakeAttendanceActivity.attendanceList.add(attendanceModel);

                if (DataCenter.attendanceSheetModel.getAttendanceType().equals("d")) {
                    attendanceModel.setDayPresent(true);

                } else {
                    AttendancePeriodDialog.attendanceModel = attendanceModel;
                    listener.showAttendancePeriodDialog();

                }


            } else {
                // absent
                for (int i = 0; i < TakeAttendanceActivity.attendanceList.size(); i++) {
                    if (TakeAttendanceActivity.attendanceList.get(i).getPersonId().equals(model.getPersonId())) {
                        TakeAttendanceActivity.attendanceList.remove(i);
                        break;
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface AttendanceAdapterListener {
        void showAttendancePeriodDialog();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        AttendanceDayModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AttendanceDayModelBinding.bind(itemView);
        }
    }

}
