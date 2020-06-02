package com.samagra.parent.ui.submissions;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.samagra.parent.R;
import com.samagra.parent.data.models.PDFItem;

import java.util.ArrayList;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.SubmissionViewHolder> {

    private ArrayList<PDFItem> submissions;
    private SubmissionsActivity context;

    public static class SubmissionViewHolder extends RecyclerView.ViewHolder {
        public TextView formName;
        public TextView submissionDate;
        public LinearLayout pdfURL;

        public SubmissionViewHolder(View itemView) {
            super(itemView);
            formName = itemView.findViewById(R.id.form_name_tv);
            submissionDate = itemView.findViewById(R.id.submission_date_tv);
            pdfURL = itemView.findViewById(R.id.pdf_ll);
        }
    }

    public SubmissionsAdapter(ArrayList<PDFItem> submissions, SubmissionsActivity context) {
        this.submissions = submissions;
        this.context = context;
    }


    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submission_row, parent, false);
        SubmissionViewHolder vh = new SubmissionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        PDFItem submission = submissions.get(position);
        holder.formName.setText(submission.getPDFItemTags().getFORMNAME());
        holder.formName.setTypeface(Typeface.DEFAULT_BOLD);
        holder.submissionDate.setText(submission.getOutputData().get(0).getTags().getFORMSUBMISSIONDATE());
        if (submission.getCurrentStatus().equals("Queue") || submission.getOutputData().get(0).getDocName() == null || submission.getOutputData().get(0).getDocName() .equals("")) {
            Drawable pdfIcon = context.getResources().getDrawable(R.drawable.pdf_icon);
            ImageView iv = holder.pdfURL.findViewById(R.id.pdf_icon);
            iv.setImageAlpha(30);
            iv.setImageDrawable(pdfIcon);
            holder.pdfURL.setOnClickListener(v -> {
                        Toast.makeText(context, R.string.pdf_not_generated_error, Toast.LENGTH_LONG).show();
                    }
            );
        } else {
            Drawable pdfIcon = context.getResources().getDrawable(R.drawable.pdf_icon);
            ImageView iv = holder.pdfURL.findViewById(R.id.pdf_icon);
            iv.setImageDrawable(pdfIcon);
            iv.setImageAlpha(255);
            holder.pdfURL.setOnClickListener(v -> {
               Uri websiteUri = Uri.parse(submission.getOutputData().get(0).getDocName());
                context.startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
                Snackbar.make(context.getParentLayout(), R.string.file_download_start, Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", view -> {

                        })
                        .setActionTextColor(context.getResources().getColor(R.color.colorPrimary))
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }
}
