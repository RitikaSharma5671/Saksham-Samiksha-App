package com.samagra.parent.ui.HomeScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.samagra.parent.R;

import java.util.ArrayList;

public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.HomeItemViewHolder> {
    private IHomeItemClickListener listener;
    private ArrayList<String> homeItemList;
    private Context context;

    public HomeItemsAdapter(IHomeItemClickListener homeItemClickListener, ArrayList<String> homeItemList, Context context) {
        this.listener = homeItemClickListener;
        this.homeItemList = homeItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_screen_card, parent, false);
        return new HomeItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemViewHolder holder, int position) {
        if (homeItemList.size() > 0) {
            holder.bindItems(homeItemList.get(position), context, position, listener);
        }
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    public static class HomeItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private RelativeLayout relativeLayout;

        public HomeItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_item_image);
            textView = itemView.findViewById(R.id.home_item_title);
            relativeLayout = itemView.findViewById(R.id.home_item_layout);
        }

        public void bindItems(String identifier, Context context, int position, IHomeItemClickListener listener) {
            switch (identifier) {
                case "Fill Forms":
                    textView.setText("FILL\nFORMS");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fill_form));
                    relativeLayout.setOnClickListener(v -> listener.onFillFormsClicked());
                    break;
                case "Mark Student Attendance":
                    textView.setText("MARK\nSTUDENT\nATTENDANCE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attendance_icon));
                    relativeLayout.setOnClickListener(v -> listener.onMarkStudentAttendanceClicked());
                    break;
                case "View Student Attendance":
                    textView.setText("VIEW\nSTUDENT\nATTENDANCE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_data));
                    relativeLayout.setOnClickListener(v -> listener.onViewStudentAttendanceClicked());
                    break;

                case "Shiksha Mitr":
                    textView.setText("SHIKSHA\nMITR\nREGISTRATION");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_logo_design));
                    relativeLayout.setOnClickListener(v -> listener.onShikshaMitrRegnClicked());
                    break;
                case "View Forms":
                    textView.setText("VIEW\nSUBMISSIONS");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_submissions));
                    relativeLayout.setOnClickListener(v -> listener.onViewODKSubmissionsClicked());
                    break;
                case "Submit Forms":
                    textView.setText("SUBMIT\nFORMS");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_send_data));
                    relativeLayout.setOnClickListener(v -> listener.onSubmitOfflineFormsClicked());
                    break;
                case "Helpline":
                    textView.setText("NEED\nHELP?");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_live_help_black_24dp));
                    relativeLayout.setOnClickListener(v -> listener.onViewHelplineClicked());
                    break;
                case "Mark Teacher Attendance":
                    textView.setText("MARK\nTEACHER\nATTENDANCE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attendance_icon));
                    relativeLayout.setOnClickListener(v -> listener.onMarkTeacherAttendanceClicked());
                    break;
                case "View Teacher Attendance":
                    textView.setText("VIEW\nTEACHER\nATTENDANCE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_data));
                    relativeLayout.setOnClickListener(v -> listener.onViewTeacherAttendanceClicked());
                    break;
                case "View School Attendance":
                    textView.setText("VIEW\nSCHOOL\nATTENDANCE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_data));
                    relativeLayout.setOnClickListener(v -> listener.onViewSchoolAttendanceClicked());
                    break;
                case "Edit Student Data":
                    textView.setText("EDIT\nSTUDENT\nSECTION");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_student_details));
                    relativeLayout.setOnClickListener(v -> listener.onEditStudentDataClicked());
                    break;
                case "Report COVID Case":
                    textView.setText("REPORT\nCOVID-19\nCASE");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_reprt_covid_case));
                    relativeLayout.setOnClickListener(v -> listener.onReportCOVIDCaseClicked());
                    break;
            }

        }
    }

//    public class HomIte extends RecyclerView.ViewHolder {
//        public TextView titleText;
//        public TextView markAllMessagesRead;
//
//        public MyTitleViewHolder (View view) {
//            super (view);
//            titleText = view.findViewById (R.id.GroupHeaderTextView);
//            markAllMessagesRead = view.findViewById (R.id.mark_all_messages_read_text);
//        }
//    }
}
