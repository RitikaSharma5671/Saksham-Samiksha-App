package com.samagra.user_profile.screens.contracts;


import android.content.Context;

import androidx.annotation.NonNull;


import com.samagra.user_profile.ProfileSectionDriver;
import com.samagra.user_profile.models.UserProfileElement;

import java.util.ArrayList;

public class ProfileSectionInteractor implements IProfileContract {

    private ProfileUpdateListener profileUpdateListener;

    @Override
   public void launchProfileActivity(@NonNull Context context, ArrayList<UserProfileElement> userProfileElements,
                                    String fusionAuthApiKey){
       ProfileSectionDriver.launchProfileActivity(context, userProfileElements, fusionAuthApiKey);
//       APIConfig.setAPIKey(fusionAuthApiKey);
//       Intent intent = new Intent(context, ProfileActivity.class);
////       intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//       intent.putParcelableArrayListExtra("config", userProfileElements);
//       context.startActivity(intent);
    }


}