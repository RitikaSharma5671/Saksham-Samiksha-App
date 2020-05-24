package com.samagra.parent.ui.HomeScreen;

import com.samagra.parent.base.MvpView;

import org.odk.collect.android.contracts.IFormManagementContract;

/**
 * The view interface 'contract' for the Home Screen. This defines all the functionality required by the
 * Presenter for the view as well as for enforcing certain structure in the Views.
 * The {@link HomeActivity} <b>must</b> implement this interface. This way, the business logic behind the screen
 * can remain unaffected.
 *
 * @author Pranav Sharma
 */
public interface HomeMvpView extends MvpView {
    void customizeToolbar();
    void showLoading(String message);
    void renderLayoutVisible();
    void hideLoading();
    void launchSearchModule();
    void setDownloadProgress(int progress);
    void updateLocale(String language);
    void displayHomeWelcomeText(String userName);
    void showNoInternetMessage();
    void showDownloadFailureMessage();
    void showFailureDownloadMessage();
    void renderLayoutInvisible();
}
