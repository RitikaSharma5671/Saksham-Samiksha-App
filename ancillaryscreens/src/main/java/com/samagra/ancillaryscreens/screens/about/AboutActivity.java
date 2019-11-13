package com.samagra.ancillaryscreens.screens.about;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samagra.ancillaryscreens.InvalidConfigurationException;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BaseActivity;
import com.samagra.ancillaryscreens.models.AboutBundle;

import org.odk.collect.android.adapters.AboutListAdapter;

import javax.inject.Inject;

/**
 * The View Part for the About Screen, must implement {@link AboutContract.View} and {@link org.odk.collect.android.adapters.AboutListAdapter.AboutItemClickListener}.
 * The activity is adapted from the ODK library and efforts have been made to keep it as similar as possible.
 *
 * @author Pranav Sharma
 */
public class AboutActivity extends BaseActivity implements AboutContract.View, AboutListAdapter.AboutItemClickListener {

    private Uri websiteUri;
    private Uri forumUri;

    private int websiteResIcon, websiteLinkTextResId, websiteSummaryDescResId;
    private String title;

    @Inject
    AboutPresenter<AboutContract.View, AboutContract.Interactor> aboutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActivityComponent().inject(this);
        aboutPresenter.onAttach(this);
        if (getIntent().getBundleExtra("config") == null)
            throw new InvalidConfigurationException(AboutActivity.class);
        else
            configureActivityFromBundle(getIntent().getBundleExtra("config"));
        initToolbar();
        setupRecyclerView();
        aboutPresenter.test(0, 1000, 0.0001f);
    }

    @Override
    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setupRecyclerView() {
        int[][] items = {
                {websiteResIcon, websiteLinkTextResId, websiteSummaryDescResId},
        };
        RecyclerView recyclerView = findViewById(org.odk.collect.android.R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AboutListAdapter(items, this, this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Configures the AboutActivity through config values passed from the app module via {@link Bundle} object
     *
     * @param bundle - The bundle containing the config values.
     * @see AboutBundle
     */
    @Override
    public void configureActivityFromBundle(Bundle bundle) {
        AboutBundle aboutBundle = AboutBundle.getAboutBundleFromBundle(bundle);
        websiteLinkTextResId = aboutBundle.getWebsiteLinkTextResId();
        websiteResIcon = aboutBundle.getWebsiteIconResId();
        websiteSummaryDescResId = aboutBundle.getWebsiteSummaryDescriptionResId();
        title = aboutBundle.getScreenTitle();
    }

    //AboutItemClickListener's onClick Callback
    @Override
    public void onClick(int position) {
        //TODO : Implement here, if you want something to happen onClick for the Website/Forum Link.
    }
}
