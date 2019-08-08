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

import butterknife.BindView;
import butterknife.ButterKnife;

class ProfileElementViewHolders {

    interface ProfileElementHolder {
        void toggleHolderEnable(boolean enable);
    }

    static class SimpleTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        SimpleTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(userProfileElement.getContent());
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
    }

    static class NumberTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        NumberTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(userProfileElement.getContent());
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
    }

    static class DateTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_date)
        AppCompatTextView textViewDate;

        private UserProfileElement userProfileElement;

        DateTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textViewDate.setText(userProfileElement.getContent());
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
    }

    static class SpinnerTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.spinner)
        AppCompatSpinner spinner;

        private UserProfileElement userProfileElement;

        SpinnerTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            itemIcon.setImageResource(R.drawable.ic_people_black_24dp);
            setupSpinner(view);
            toggleHolderEnable(false);
        }

        private void setupSpinner(View view) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(view.getContext(),
                    R.layout.profile_spinner_item, userProfileElement.getSpinner_extras());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
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
    }
}
