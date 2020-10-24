package com.samagra.parent.ui.HomeScreen;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.commons.CustomTabHelper;
import com.samagra.parent.MyApplication;
import com.samagra.parent.R;
import com.samagra.parent.base.BaseActivity;

import org.odk.collect.android.activities.WebViewActivity;

import static com.samagra.commons.CustomTabHelper.OPEN_URL;

@SuppressWarnings("ALL")
public class ComingSoon extends BaseActivity {
    public TextView teacher_documentation_link1;
    public TextView mentor_documentation_link1;
    public TextView documentation_link;
    private CustomTabHelper websiteTabHelper;
    private Uri websiteUri;
    private static final String SAMAGRA_DOC_WEBSITE = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        setupToolbar();
        teacher_documentation_link1 = findViewById(R.id.faq_teacher_doc);
        mentor_documentation_link1 = findViewById(R.id.faq_mentor_doc);
        documentation_link = findViewById(R.id.documentation_link);
        websiteTabHelper = new CustomTabHelper();
        String urlFromConfig = MyApplication.mFirebaseRemoteConfig.getString("help_url");
        if(urlFromConfig.isEmpty())
            urlFromConfig = "https://forms.gle/ReS5tMBVwpmCMhEe7";
        websiteUri = Uri.parse(urlFromConfig);

       documentation_link.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try {
                   //open in external browser
                   getActivityContext().startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
               } catch (ActivityNotFoundException | SecurityException e) {
                   //open in webview
                   Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
                   intent.putExtra(OPEN_URL, websiteUri.toString());
                   getActivityContext().startActivity(intent);
               }

           }
       });
        SpannableString content1 = new SpannableString("HELPLINE FORM");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        documentation_link.setText(content1);

        String urlFromConfig_MentorDoc = "http://bit.ly/Guidelines_document";
        if(MyApplication.mFirebaseRemoteConfig.getString("faq_mentor_url") != null && !MyApplication.mFirebaseRemoteConfig.getString("faq_mentor_url").isEmpty())
            urlFromConfig_MentorDoc = MyApplication.mFirebaseRemoteConfig.getString("faq_mentor_url");
        String title1 = "Guidelines for Mentors and Monitors";
        SpannableString content11 = new SpannableString(title1);
        content11.setSpan(new UnderlineSpan(), 0, content11.length(), 0);
        mentor_documentation_link1.setText(content11);


        String urlFromConfig_TeacherDoc = "http://bit.ly/samiksha-FAQ";
        if(MyApplication.mFirebaseRemoteConfig.getString("faq_teacher_url") != null && !MyApplication.mFirebaseRemoteConfig.getString("faq_teacher_url").isEmpty())
            urlFromConfig_TeacherDoc = MyApplication.mFirebaseRemoteConfig.getString("faq_teacher_url");
        String title11 = "Guidelines for Teachers and School Heads";
        SpannableString content111 = new SpannableString(title11);
        content111.setSpan(new UnderlineSpan(), 0, content111.length(), 0);
        teacher_documentation_link1.setText(content111);

        String finalUrlFromConfig_TeacherDoc = urlFromConfig_TeacherDoc;
        teacher_documentation_link1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
            intent.putExtra(org.odk.collect.android.utilities.CustomTabHelper.OPEN_URL, finalUrlFromConfig_TeacherDoc);
            getActivityContext().startActivity(intent);
        });
        String finalUrlFromConfig_MentorDoc = urlFromConfig_MentorDoc;
        mentor_documentation_link1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
            intent.putExtra(org.odk.collect.android.utilities.CustomTabHelper.OPEN_URL, finalUrlFromConfig_MentorDoc);
            getActivityContext().startActivity(intent);
        });
    }



    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getActivityContext().getResources().getString(R.string.need_help));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> finish());

    }


    @Override
    public void onStart() {
        super.onStart();
        websiteTabHelper.bindCustomTabsService(this, websiteUri); }

    @Override
    public void onDestroy() {
        unbindService(websiteTabHelper.getServiceConnection());
        super.onDestroy();
    }
}
