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

import com.samagra.commons.CustomTabHelper;
import com.samagra.parent.MyApplication;
import com.samagra.parent.R;
import com.samagra.parent.base.BaseActivity;

import static com.samagra.commons.CustomTabHelper.OPEN_URL;

public class ComingSoon extends BaseActivity {
    public Button documentation_link1;
    public TextView documentation_link;
    private CustomTabHelper websiteTabHelper;
    private Uri websiteUri;
    private static final String SAMAGRA_DOC_WEBSITE = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        setupToolbar();
        documentation_link1 = findViewById(R.id.helpline_title);
        documentation_link = findViewById(R.id.documentation_link);
        websiteTabHelper = new CustomTabHelper();
        String urlFromConfig = MyApplication.mFirebaseRemoteConfig.getString("help_url");
        if(urlFromConfig.isEmpty())
            urlFromConfig = "http://bit.ly/samiksha-helpline";
        websiteUri = Uri.parse(urlFromConfig);

        SpannableString content1 = new SpannableString(urlFromConfig);
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        documentation_link.setText(content1);

        documentation_link1.setOnClickListener(v -> {

//            if(!websiteTabHelper.openUri(v.getContext(), websiteUri)){
                try {
                    //open in external browser
                    getActivityContext().startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
                } catch (ActivityNotFoundException | SecurityException e) {
                    //open in webview
                    Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
                    intent.putExtra(OPEN_URL, websiteUri.toString());
                    getActivityContext().startActivity(intent);
                }
//            }
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
