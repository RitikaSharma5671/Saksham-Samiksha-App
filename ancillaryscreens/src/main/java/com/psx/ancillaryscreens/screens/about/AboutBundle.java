package com.psx.ancillaryscreens.screens.about;

import android.os.Bundle;

/**
 * Config Bundle for the AboutActivity.
 * This bundle contains all the variables necessary for personalizing the AboutActivity
 */
public class AboutBundle {
    private String screenTitle;
    private String websiteUrl;
    private String forumUrl;
    private int websiteIconResId;
    private int websiteLinkTextResId;
    private int websiteSummaryDescriptionResId;
    public Bundle aboutExchangeBundle;

    public AboutBundle(String screenTitle, String websiteUrl, String forumUrl, int websiteIconResId, int websiteLinkTextResId, int websiteSummaryDescriptionResId) {
        this.screenTitle = screenTitle;
        this.websiteUrl = websiteUrl;
        this.forumUrl = forumUrl;
        this.websiteIconResId = websiteIconResId;
        this.websiteLinkTextResId = websiteLinkTextResId;
        this.websiteSummaryDescriptionResId = websiteSummaryDescriptionResId;
        this.aboutExchangeBundle = generateExchangeBundle();
    }

    private AboutBundle() {
    }

    private Bundle generateExchangeBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("title", screenTitle);
        bundle.putString("websiteUrl", websiteUrl);
        bundle.putString("forumUrl", forumUrl);
        bundle.putInt("websiteIconRes", websiteIconResId);
        bundle.putInt("websiteLinkText", websiteLinkTextResId);
        bundle.putInt("websiteSummaryDesc", websiteSummaryDescriptionResId);
        return bundle;
    }

    static AboutBundle getAboutBundleFromBundle(Bundle bundle) {
        AboutBundle aboutBundle = new AboutBundle();
        aboutBundle.screenTitle = bundle.getString("title");
        aboutBundle.websiteUrl = bundle.getString("websiteUrl");
        aboutBundle.forumUrl = bundle.getString("forumUrl");
        aboutBundle.websiteIconResId = bundle.getInt("websiteIconRes");
        aboutBundle.websiteLinkTextResId = bundle.getInt("websiteLinkText");
        aboutBundle.websiteSummaryDescriptionResId = bundle.getInt("websiteSummaryDesc");
        return aboutBundle;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getForumUrl() {
        return forumUrl;
    }

    public int getWebsiteIconResId() {
        return websiteIconResId;
    }

    public int getWebsiteLinkTextResId() {
        return websiteLinkTextResId;
    }

    public int getWebsiteSummaryDescriptionResId() {
        return websiteSummaryDescriptionResId;
    }
}
