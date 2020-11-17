package com.samagra.parent.ui.submissions;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.samagra.ancillaryscreens.di.FormManagementCommunicator;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.R;
import com.samagra.parent.UtilityFunctions;
import com.samagra.parent.base.BaseActivity;
import com.samagra.parent.data.models.PDFItem;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.adapters.SortDialogAdapter;
import org.odk.collect.android.utilities.SnackbarUtils;
import org.odk.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;

/**
 * View part of the MyVisits Screen. This class only handles the UI operations, all the business
 * logic is simply abstracted from this Activity. It <b>must</b> implement the {@link SubmissionsMvpView}
 * and extend the {@link BaseActivity}.
 *
 * @author Chakshu Gautam
 */
public class SubmissionsActivity extends BaseActivity implements SubmissionsMvpView {

    public RecyclerView recyclerView;

    public RelativeLayout parentLayout;

    public TextView clickToDownload;

    public ImageView noSubmissionView;

    public LottieAnimationView lottie_loader;

    public Button viewSubmissions;

    public Button retryFetching;

    public LinearLayout internetErrorView;

    public SubmissionsAdapter submissionsAdapter;
    public RecyclerView.LayoutManager layoutManager;

    private BottomSheetDialog bottomSheetDialog;
    private boolean isBottomDialogShown = false;
    protected int[] sortingOptions;
    protected Integer selectedSortingOrder = Integer.MAX_VALUE;

    private String filterText = "";
    private String savedFilterText;
    private boolean isSearchBoxShown;

    private BottomSheetDialog bottomSheetDialogFormFilters;
    private boolean isBottomDialogForFormsShown = false;
    protected ArrayList<String> formOptions = new ArrayList<>();
    protected Integer selectedForm = 0; //All forms

    private SearchView searchView;
    private ArrayList<PDFItem> submissions = new ArrayList<PDFItem>();

    SubmissionsPresenter submissionsPresenter =  new SubmissionsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submissions);
        recyclerView = findViewById(R.id.submissions_rv);

        parentLayout  = findViewById(R.id.parent_of_submissions);

        clickToDownload   = findViewById(R.id.click_to_download);

        noSubmissionView = findViewById(R.id.no_internet_iv) ;

        lottie_loader = findViewById(R.id.lottie_loader) ;

        viewSubmissions= findViewById(R.id.view_submissions);

        retryFetching  = findViewById(R.id.retry_fetching);

        internetErrorView = findViewById(R.id.internet_error_view);
        setupToolbar();
        showProgressBar();
        setupRecyclerView();
        submissionsPresenter.getCachedData();
        sortingOptions = new int[]{
                org.odk.collect.android.R.string.sort_by_name_asc, org.odk.collect.android.R.string.sort_by_name_desc,
                org.odk.collect.android.R.string.sort_by_date_asc, org.odk.collect.android.R.string.sort_by_date_desc,
        };

        // TODO: Fix this.
        formOptions = submissionsPresenter.getFormOptions();
        setupBottomSheet();
        setupBottomSheetForForms();
        submissionsPresenter.updateFilters(filterText, selectedSortingOrder, selectedSortingOrder);
        retryFetching.setOnClickListener( v -> onRefreshButtonPressed());
        viewSubmissions.setOnClickListener( v -> onViewSubmissionsClicked());
    }

    private void onViewSubmissionsClicked() {
        if(FormManagementCommunicator.getContract() != null)
            FormManagementCommunicator.getContract().launchViewSubmittedFormsView(getActivityContext(),  UtilityFunctions.generateToolbarModificationObject(true,
                    R.drawable.ic_arrow_back_white_24dp,
                    getActivityContext().getResources().getString(R.string.view_sent_forms), true));
    }

    public View getParentLayout() {
        return parentLayout;
    }

    void setupRecyclerView() {
        submissions = submissionsPresenter.getSubmissionsFromCache();
        submissionsAdapter = new SubmissionsAdapter(submissions, this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(submissionsAdapter);
    }

    private void refreshData(ArrayList<PDFItem> newData) {
        // https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data?rq=1
        submissions.clear();
        submissions.addAll(newData);
        submissionsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    private void customizeToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sort) {
            bottomSheetDialog.show();
            isBottomDialogShown = true;
            bottomSheetDialogFormFilters.hide();
            isBottomDialogForFormsShown = false;
            return true;
        }

        if (item.getItemId() == R.id.menu_form_selection) {
            bottomSheetDialog.hide();
            isBottomDialogShown = false;
            bottomSheetDialogFormFilters.show();
            isBottomDialogForFormsShown = true;
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(org.odk.collect.android.R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.my_visits));
        setSupportActionBar(toolbar);
    }
    
    @Override
    public void onRefreshButtonPressed() {
        showLoadingView();
        submissionsPresenter.getCachedData();
    }

    @Override
    public void showViewLayout() {
        clickToDownload.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        noSubmissionView.setVisibility(View.GONE);
        internetErrorView.setVisibility(View.GONE);
        startLoader(false);
    }

    private void startLoader(boolean b) {
        if(b){
            lottie_loader.setVisibility(View.VISIBLE);
            lottie_loader.setAnimation("loader.json");
            lottie_loader.setRepeatCount(ValueAnimator.INFINITE);
            lottie_loader.playAnimation();
        }else{
            lottie_loader.cancelAnimation();
            lottie_loader.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void showProgressBar() {
        clickToDownload.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noSubmissionView.setVisibility(View.GONE);
    }

    @Override
    public void animateSmallProgressBar() {

    }

    @Override
    public void render(ArrayList<PDFItem> submissions) {
        refreshData(submissions);
        if(submissions.size() > 0){
        showViewLayout();
        startLoader(false);}
        else{
            renderNoData();
            startLoader(false);
        }
        Grove.d("Rendered submissions");
    }

    @Override
    public void renderNoData() {
        if(submissionsPresenter.isNetworkConnected()){
        clickToDownload.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        startLoader(false);
        internetErrorView.setVisibility(View.GONE);
        noSubmissionView.setVisibility(View.VISIBLE);}
        else{
        onInternetNotConnected();
    }
    }

    @Override
    public void onInternetNotConnected() {
        clickToDownload.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noSubmissionView.setVisibility(View.GONE);
        internetErrorView.setVisibility(View.VISIBLE);
        startLoader(false);
        showMessage(this.getResources().getString(R.string.no_internet));
    }

    @Override
    public void showMessage(String message) {
        SnackbarUtils.showLongSnackbar(parentLayout, message);

    }

    @Override
    public void showLoadingView() {
        clickToDownload.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noSubmissionView.setVisibility(View.GONE);
        internetErrorView.setVisibility(View.GONE);
        startLoader(true);
    }

    @Override
    public String getUserName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivityContext());
        String username = sharedPreferences.getString("user.username", "");
        return username;
    }

    private void setupBottomSheet() {
        ThemeUtils themeUtils =  new ThemeUtils(this);
        bottomSheetDialog = new BottomSheetDialog(this, themeUtils.getBottomDialogTheme());
        final View sheetView = getLayoutInflater().inflate(org.odk.collect.android.R.layout.bottom_sheet, null);
        final RecyclerView recyclerView = sheetView.findViewById(org.odk.collect.android.R.id.recyclerView);

        final SortDialogAdapter adapter = new SortDialogAdapter(this, recyclerView, sortingOptions, selectedSortingOrder, new org.odk.collect.android.listeners.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(SortDialogAdapter.ViewHolder holder, int position) {
                performSelectedSearch(position);
                holder.updateItemColor(selectedSortingOrder);
                bottomSheetDialog.dismiss();
                isBottomDialogShown = false;
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bottomSheetDialog.setContentView(sheetView);

        if (isBottomDialogShown) {
            bottomSheetDialog.show();
        }
    }

    private void setupBottomSheetForForms() {
        ThemeUtils themeUtils =  new ThemeUtils(this);
        bottomSheetDialogFormFilters = new BottomSheetDialog(this, themeUtils.getBottomDialogTheme());
        final View sheetView = getLayoutInflater().inflate(org.odk.collect.android.R.layout.bottom_sheet, null);
        final TextView labelTV = sheetView.findViewById(org.odk.collect.android.R.id.label);
        final RecyclerView recyclerView = sheetView.findViewById(org.odk.collect.android.R.id.recyclerView);

        labelTV.setText(R.string.select_form);

        final FilterDialogAdapter adapter = new FilterDialogAdapter(this, recyclerView, formOptions, selectedForm, new RecyclerViewClickListener() {
            @Override
            public void onItemClicked(FilterDialogAdapter.ViewHolder holder, int position) {
                holder.updateItemColor(selectedForm);
                performSelectedFilter(position);
                bottomSheetDialogFormFilters.dismiss();
                isBottomDialogForFormsShown = false;
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bottomSheetDialogFormFilters.setContentView(sheetView);

        if (isBottomDialogForFormsShown) {
            bottomSheetDialogFormFilters.show();
        }
    }

    private void performSelectedFilter(int position) {
        selectedForm = position;
        updateAdapter();
    }


    private void performSelectedSearch(int position) {
        selectedSortingOrder = position;
        updateAdapter();
    }

    private void updateAdapter() {
        submissionsPresenter.updateFilters(filterText, selectedSortingOrder, selectedForm);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu_submissions, menu);
        final MenuItem sortItem = menu.findItem(R.id.menu_sort);
        final MenuItem searchItem = menu.findItem(R.id.menu_filter);
        final MenuItem filterItem = menu.findItem(R.id.menu_form_selection);
        final MenuItem filterItem1 = menu.findItem(R.id.menu_form_selection1);
        filterItem.setVisible(false);
        filterItem.setEnabled(false);
        filterItem1.setVisible(true);
        filterItem1.setEnabled(true);
        filterItem1.setOnMenuItemClickListener(item -> {
            onViewSubmissionsClicked();
            return true;
        });
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search) + "</font>"));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterText = query;
                updateAdapter();
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterText = newText;
                updateAdapter();
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(item.getItemId() != R.id.menu_form_selection1){
                sortItem.setVisible(false);
                filterItem.setVisible(false);
                return true;}
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() != R.id.menu_form_selection1) {
                    sortItem.setVisible(true);

                    filterItem.setVisible(false);
                    return true;
                }
                return false;
            }
        });

        if (isSearchBoxShown) {
            searchItem.expandActionView();
            searchView.setQuery(savedFilterText, false);
        }
        return super.onCreateOptionsMenu(menu);
    }


}
