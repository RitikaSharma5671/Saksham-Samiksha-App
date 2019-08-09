package com.psx.odktest.ui.HomeScreen;

import com.psx.odktest.base.MvpView;

/**
 * The view interface 'contract' for the Home Screen. This defines all the functionality required by the
 * Presenter for the view as well as for enforcing certain structure in the Views.
 * The {@link HomeActivity} <b>must</b> implement this interface. This way, the business logic behind the screen
 * can remain unaffected.
 *
 * @author Pranav Sharma
 */
public interface HomeMvpView extends MvpView {

    void updateWelcomeText(String text);

    void showLoading(String message);

    void hideLoading();

    /**
     * This function subsribe to the {@link com.psx.commons.RxBus} to listen for the Logout related events
     * and update the UI accordingly. The events being subscribed to are {@link com.psx.commons.CustomEvents#LOGOUT_COMPLETED}
     * and {@link com.psx.commons.CustomEvents#LOGOUT_INITIATED}
     *
     * @see com.psx.commons.CustomEvents
     */
    void initializeLogoutListener();
}
