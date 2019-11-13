package com.samagra.odktest.ui.Submissions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.samagra.odktest.R;
import com.samagra.odktest.data.models.Submission;

import org.odk.collect.android.activities.WebViewActivity;
import org.odk.collect.android.utilities.CustomTabHelper;

import java.util.ArrayList;
import java.util.List;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.SubmissionViewHolder> {

    private ArrayList<Submission> submissions;
    private SubmissionsActivity context;

    public static class SubmissionViewHolder extends RecyclerView.ViewHolder {
        public TextView formName;
        public TextView submissionDate;
        public LinearLayout pdfURL;

        public SubmissionViewHolder(View itemView) {
            super(itemView);
            formName = (TextView) itemView.findViewById(R.id.form_name_tv);
            submissionDate = (TextView) itemView.findViewById(R.id.submission_date_tv);
            pdfURL = itemView.findViewById(R.id.pdf_ll);
        }
    }

    public SubmissionsAdapter(ArrayList<Submission> submissions, SubmissionsActivity context) {
        this.submissions = submissions;
        this.context = context;
    }


    @NonNull
    @Override
    public SubmissionsAdapter.SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submission_row, parent, false);
        SubmissionViewHolder vh = new SubmissionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionsAdapter.SubmissionViewHolder holder, int position) {
        Submission submission = submissions.get(position);
        holder.formName.setText(submission.getFormName());
        holder.submissionDate.setText(submission.getSubmissionDate());
        if (submission.getURL() == null || submission.getURL().equals("")) {
            Drawable pdfIcon =  context.getResources().getDrawable(R.drawable.pdf_icon);
            ImageView iv = (ImageView) holder.pdfURL.findViewById(R.id.pdf_icon);
            iv.setColorFilter(Color.argb(150,200,200,200));
            iv.setImageDrawable(pdfIcon);
        } else {
            holder.pdfURL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra(CustomTabHelper.OPEN_URL, "https://docs.google.com/gview?embedded=true&url=" + submission.getURL());
                    context.startActivity(intent);
                    Snackbar.make(context.getParentLayout(), "This is main activity", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }
}
