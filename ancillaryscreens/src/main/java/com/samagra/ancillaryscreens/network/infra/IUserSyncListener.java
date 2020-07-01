package com.samagra.ancillaryscreens.network.infra;

public interface IUserSyncListener {

    /**
     * The notifier which is called when the user data is synced with the back-end systems.
     * This passes back the Object[] parameters which are set in the
     * {@link #fetchUserData(IUserSyncListener, Object...)} (IUserSyncListener, Object...)}.
     *
     * @param options - {Object...} varargs of object.
     */
    void onUserDataSynced(Object... options);
}
