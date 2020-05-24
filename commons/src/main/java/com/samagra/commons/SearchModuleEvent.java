package com.samagra.commons;


import android.app.Activity;
import android.content.Context;

import androidx.core.content.ContextCompat;

/**
 * Created by Umang Bhola on 16/5/20.
 * Samagra- Transforming Governance
 */
public class SearchModuleEvent extends REvent{

    public SearchModuleEvent(InstitutionInfo institutionInfo, Context context) {
        this.institutionInfo = institutionInfo;
        this.context = context;
    }
    private Context context;
    private InstitutionInfo institutionInfo;

    public InstitutionInfo getInstitutionInfo(){
        return institutionInfo;
    }
    public Context getContext(){
        return context;
    }
}
