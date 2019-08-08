package com.psx.ancillaryscreens.screens.profile;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.psx.ancillaryscreens.R;
import com.psx.ancillaryscreens.R2;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.psx.ancillaryscreens.models.UserProfileElement.ProfileElementContentType;

/**
 * This class contains the 'View Holders' for different types of Profile elements. These 'View Holders'
 * dynamically populate the {@link ProfileActivity} by providing a View for various kinds of properties
 * a User profile may have. For instance, User name, phone number, email, etc can be profile properties.
 * These inner classes are placed in a separate class for separation of concerns and to avoid cluttering
 * the actual activity ({@link ProfileActivity}.
 * All of the {@link ProfileElementViewHolders}'s inner classes must implement {@link ProfileElementHolder}
 * interface.
 *
 * @author Pranav Sharma
 * @see ProfileElementContentType
 * @see UserProfileElement
 */
class ProfileElementViewHolders {

    /**
     * This interface <b>must</b> be implemented by all the inner classes of {@link ProfileElementViewHolders}
     */
    interface ProfileElementHolder {
        /**
         * This method enable/disables the editing of the profile element.
         * The profile element should be of type {@link ProfileElementContentType}
         *
         * @param enable - boolean to indicate if the editing of {@link ProfileElementHolder}
         *               is allowed.
         */
        void toggleHolderEnable(boolean enable);

        /**
         * This function enforces that all the {@link ProfileElementHolder}s are capable of returning
         * a {@link UserProfileElement} object, which is also a requisite for creation for all
         * {@link ProfileElementHolder}
         *
         * @return an object of type {@link UserProfileElement}
         * @see UserProfileElement
         */
        UserProfileElement getUserProfileElement();

        /**
         * This function returns the updated value of the profile element the {@link ProfileElementHolder}
         * is representing. For instance, The user name may be displayed using {@link SimpleTextViewHolder}
         * So to get the updated user name (which may result as a part of user editing this screen),
         * {@link SimpleTextViewHolder} must be able to return the new updated value of the name.
         *
         * @return a {@link String} that represents the updated value for the profile element.
         */
        String getUpdatedElementValue();
    }

    static class SimpleTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        SimpleTextViewHolder(View view, UserProfileElement userProfileElement, String contentValue) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(contentValue);
            itemIcon.setImageResource(R.drawable.ic_people_black_24dp);
            toggleHolderEnable(false);
        }

        @Override
        public void toggleHolderEnable(boolean enable) {
            if (!enable) {
                textInputEditText.setClickable(false);
                textInputEditText.setEnabled(false);
            } else if (userProfileElement.isEditable()) {
                textInputEditText.setEnabled(true);
                textInputEditText.setClickable(true);
            }
        }

        @Override
        public UserProfileElement getUserProfileElement() {
            return userProfileElement;
        }

        @Override
        public String getUpdatedElementValue() {
            return Objects.requireNonNull(textInputEditText.getText()).toString();
        }
    }

    static class NumberTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        NumberTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(content);
            itemIcon.setImageResource(R.drawable.ic_call_black_24dp);
            toggleHolderEnable(false);
        }

        @Override
        public void toggleHolderEnable(boolean enable) {
            if (!enable) {
                textInputEditText.setEnabled(false);
                textInputEditText.setClickable(false);
            } else if (userProfileElement.isEditable()) {
                textInputEditText.setEnabled(true);
                textInputEditText.setClickable(true);
            }
        }

        @Override
        public UserProfileElement getUserProfileElement() {
            return userProfileElement;
        }

        @Override
        public String getUpdatedElementValue() {
            return Objects.requireNonNull(textInputEditText.getText()).toString();
        }
    }

    static class DateTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_date)
        AppCompatTextView textViewDate;

        private UserProfileElement userProfileElement;

        DateTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textViewDate.setText(content);
            itemIcon.setImageResource(R.drawable.ic_date_range_black_24dp);
            toggleHolderEnable(false);
        }


        @Override
        public void toggleHolderEnable(boolean enable) {
            if (!enable) {
                textViewDate.setClickable(false);
                textViewDate.setEnabled(false);
            } else if (userProfileElement.isEditable()) {
                textViewDate.setEnabled(true);
                textViewDate.setClickable(true);
            }
        }

        @Override
        public UserProfileElement getUserProfileElement() {
            return userProfileElement;
        }

        @Override
        public String getUpdatedElementValue() {
            return textViewDate.getText().toString();
        }
    }

    static class SpinnerTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.spinner)
        AppCompatSpinner spinner;

        private UserProfileElement userProfileElement;

        SpinnerTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            itemIcon.setImageResource(R.drawable.ic_people_black_24dp);
            setupSpinner(view, content);
            toggleHolderEnable(false);
        }

        private void setupSpinner(View view, String content) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(view.getContext(),
                    R.layout.profile_spinner_item, userProfileElement.getSpinner_extras());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setSelection(findContentPosition(content));
        }

        private int findContentPosition(String content) {
            for (int i = 0; i < userProfileElement.getSpinner_extras().size(); i++) {
                if (userProfileElement.getSpinner_extras().get(i).equalsIgnoreCase(content))
                    return i;
            }
            return 0;
        }

        @Override
        public void toggleHolderEnable(boolean enable) {
            if (!enable) {
                spinner.setEnabled(false);
                spinner.setClickable(false);
            } else if (userProfileElement.isEditable()) {
                spinner.setEnabled(true);
                spinner.setClickable(true);
            }
        }

        @Override
        public UserProfileElement getUserProfileElement() {
            return userProfileElement;
        }

        @Override
        public String getUpdatedElementValue() {
            return userProfileElement.getSpinner_extras().get(spinner.getSelectedItemPosition());
        }
    }
}
