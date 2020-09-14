package com.samagra.ancillaryscreens.screens.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.samagra.ancillaryscreens.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;



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
 * @see UserProfileElement.ProfileElementContentType
 * @see UserProfileElement
 */
class ProfileElementViewHolders {

    /**
     * This interface <b>must</b> be implemented by all the inner classes of {@link ProfileElementViewHolders}
     */
    interface ProfileElementHolder {
        /**
         * This method enable/disables the editing of the profile element.
         * The profile element should be of type {@link UserProfileElement.ProfileElementContentType}
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
        ImageView itemIcon;
        AppCompatTextView textViewItemDesc;
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        SimpleTextViewHolder(View view, UserProfileElement userProfileElement, String contentValue) {
            itemIcon = view.findViewById(R.id.icon);
            textViewItemDesc =  view.findViewById(R.id.item_description);
            textInputEditText = view.findViewById(R.id.text_edit_text);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(contentValue);
            if(userProfileElement.decodeBase64ToGetBitmap(userProfileElement.getBase64Icon()) != null) {
                itemIcon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
                itemIcon.setImageBitmap(userProfileElement.decodeBase64ToGetBitmap(userProfileElement.getBase64Icon()));
            }else{
                if(userProfileElement.getContent().equals("user.email"))
                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_email_black_24dp));
                else if(userProfileElement.getContent().equals("user.fullName") || userProfileElement.getContent().equals("user.username"))
                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_people_black_24dp));
                else if(userProfileElement.getContent().equals("user.mobilePhone"))
                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_phone));
                else if(userProfileElement.getContent().equals("user.designation"))
                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_supervised_user_circle_24px));
                else if(userProfileElement.getContent().equals("user.category"))
                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_supervised_user_circle_24px));

                itemIcon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));

            }
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
        ImageView itemIcon;
        AppCompatTextView textViewItemDesc;
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        NumberTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            itemIcon = view.findViewById(R.id.icon);
            textViewItemDesc =  view.findViewById(R.id.item_description);
            textInputEditText = view.findViewById(R.id.text_edit_text);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(content);
//            if (userProfileElement.decodeBase64ToGetBitmap(userProfileElement.getBase64Icon()) != null) {
//                itemIcon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
//                itemIcon.setImageBitmap(userProfileElement.decodeBase64ToGetBitmap(userProfileElement.getBase64Icon()));
//            } else {
//                if (userProfileElement.getContent().equals("user.mobilePhone"))
//                    itemIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_phone));
                itemIcon.setImageResource(R.drawable.ic_call_black_24dp);
//            }
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

    static class DateTextViewHolder implements ProfileElementHolder, DatePickerDialog.OnDateSetListener {


        ImageView itemIcon;
        AppCompatTextView textViewItemDesc;
        AppCompatTextView textViewDate;

        private UserProfileElement userProfileElement;
        private DatePickerDialog datePickerDialog;

        DateTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            itemIcon = view.findViewById(R.id.icon);
            textViewItemDesc =  view.findViewById(R.id.item_description);
            textViewDate = view.findViewById(R.id.text_date);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textViewDate.setText(content);
            itemIcon.setImageResource(R.drawable.ic_date_range_black_24dp);
            setupDatePickerDialog(view.getContext(), this, Calendar.getInstance());
            toggleHolderEnable(false);
        }

        private void setupDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener,
                                           Calendar todayInstance) {
            if (datePickerDialog == null) {
                datePickerDialog = new DatePickerDialog(context, this, todayInstance.get(Calendar.YEAR), todayInstance.get(Calendar.MONTH), todayInstance.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(todayInstance.getTime().getTime());
                textViewDate.setOnClickListener(view -> datePickerDialog.show());
            }
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

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
            textViewDate.setText(simpleDateFormat.format(calendar.getTime()));
        }
    }

    static class SpinnerTextViewHolder implements ProfileElementHolder {

        ImageView itemIcon;
        AppCompatTextView textViewItemDesc;
        AppCompatSpinner spinner;

        private UserProfileElement userProfileElement;

        SpinnerTextViewHolder(View view, UserProfileElement userProfileElement, String content) {
            itemIcon = view.findViewById(R.id.icon);
            textViewItemDesc =  view.findViewById(R.id.item_description);
            spinner = view.findViewById(R.id.spinner);
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