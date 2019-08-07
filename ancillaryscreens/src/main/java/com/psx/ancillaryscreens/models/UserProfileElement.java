package com.psx.ancillaryscreens.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.psx.ancillaryscreens.InvalidConfigurationException;

import java.util.ArrayList;

public class UserProfileElement implements Parcelable {

    private String base64Icon;
    private String title;
    private String content;
    private boolean isEditable;
    private int section;
    private ProfileElementContentType profileElementContentType;
    private ArrayList<String> spinner_extras;

    public UserProfileElement(@NonNull String base64Icon, @NonNull String title, @NonNull String content, boolean isEditable, int section,
                              @NonNull ProfileElementContentType profileElementContentType,
                              @Nullable ArrayList<String> spinner_extras) {
        if (profileElementContentType == ProfileElementContentType.SPINNER && (spinner_extras == null || spinner_extras.size() == 0))
            throw new InvalidConfigurationException(UserProfileElement.class);
        this.base64Icon = base64Icon;
        this.title = title;
        this.content = content;
        this.isEditable = isEditable;
        this.section = section;
        this.profileElementContentType = profileElementContentType;
        this.spinner_extras = spinner_extras;
    }

    private UserProfileElement(Parcel parcel) {
        this.base64Icon = parcel.readString();
        this.title = parcel.readString();
        this.content = parcel.readString();
        this.isEditable = parcel.readInt() == 1;
        this.section = parcel.readInt();
        this.profileElementContentType = ProfileElementContentType.valueOf(parcel.readString());
        if (this.spinner_extras != null)
            parcel.readStringList(this.spinner_extras);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public int getSection() {
        return section;
    }

    public ArrayList<String> getSpinner_extras() {
        return spinner_extras;
    }

    public Bitmap decodeBase64ToGetBitmap() {
        byte[] decodedString = Base64.decode(base64Icon, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public ProfileElementContentType getProfileElementContentType() {
        return profileElementContentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.base64Icon);
        parcel.writeString(this.title);
        parcel.writeString(this.content);
        parcel.writeInt(this.isEditable ? 1 : 0);
        parcel.writeInt(this.section);
        parcel.writeString(this.profileElementContentType.name());
        if (this.spinner_extras != null)
            parcel.writeStringList(this.spinner_extras);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<UserProfileElement>() {

        @Override
        public UserProfileElement createFromParcel(Parcel parcel) {
            return new UserProfileElement(parcel);
        }

        @Override
        public UserProfileElement[] newArray(int size) {
            return new UserProfileElement[size];
        }
    };

    public enum ProfileElementContentType {
        TEXT,
        DATE,
        NUMBER,
        SPINNER;
    }
}
