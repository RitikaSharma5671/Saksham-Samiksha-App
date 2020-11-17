/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.formentry.questions;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;
import org.odk.collect.android.R;
import org.odk.collect.android.R2;
import org.odk.collect.android.audio.AudioButton;
import org.odk.collect.android.audio.AudioHelper;
import org.odk.collect.android.audioclips.Clip;
import org.odk.collect.android.listeners.SelectItemClickListener;
import org.odk.collect.android.utilities.ContentUriProvider;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.FormEntryPromptUtils;
import org.odk.collect.android.utilities.ScreenContext;
import org.odk.collect.android.utilities.ScreenUtils;
import org.odk.collect.android.utilities.StringUtils;
import org.odk.collect.android.utilities.ThemeUtils;
import org.odk.collect.android.utilities.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Represents a label for a prompt/question or a select choice. The label can have media
 * attached to it as well as text (such as audio, video or an image).
 */
public class AudioVideoImageTextLabel extends RelativeLayout implements View.OnClickListener {

    @BindView(R2.id.audioButton1)
    AudioButton audioButton1212;
    @BindView(R2.id.videoButton1212)
    MaterialButton videoButton1212;

    @BindView(R2.id.imageView)
    ImageView imageView;

    @BindView(R2.id.missingImage)
    TextView missingImage;

    @BindView(R2.id.text_container)
    FrameLayout textContainer;

    @BindView(R2.id.text_label)
    TextView labelTextView;

    @BindView(R2.id.media_buttons)
    LinearLayout mediaButtonsContainer;

    private int originalTextColor;
    private int playTextColor = Color.BLUE;
    private CharSequence questionText;
    private SelectItemClickListener listener;
    private File videoFile;
    private File bigImageFile;

    public AudioVideoImageTextLabel(Context context) {
        super(context);
        View.inflate(context, R.layout.audio_video_image_text_label, this);
        labelTextView = findViewById(R.id.text_label);
        ButterKnife.bind(this);
//        audioButton1212 = findViewById(R.id.audioButton1);
//        videoButton1212 = findViewById(R.id.videoButton1212);
//        imageView = findViewById(R.id.imageView);
//        missingImage = findViewById(R.id.missingImage);
//        textContainer = findViewById(R.id.text_container);
//        labelTextView = findViewById(R.id.text_label);
//        mediaButtonsContainer = findViewById(R.id.media_buttons);
    }

    public AudioVideoImageTextLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.audio_video_image_text_label, this);
        ButterKnife.bind(this);
//        audioButton1212 = findViewById(R.id.audioButton1);
//
//        videoButton1212 = findViewById(R.id.videoButton1212);
//
//        imageView = findViewById(R.id.imageView);
//        missingImage = findViewById(R.id.missingImage);
//
//        textContainer = findViewById(R.id.text_container);
//
//        labelTextView = findViewById(R.id.text_label);
//
//       mediaButtonsContainer = findViewById(R.id.media_buttons);
    }

    public void setTextView(TextView questionText) {
        this.questionText = questionText.getText();

        this.labelTextView = questionText;
        this.labelTextView.setId(R.id.text_label);
        this.labelTextView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClicked();
            }
        });

//        textContainer = findViewById(R.id.text_container);
        textContainer.removeAllViews();
        textContainer.addView(this.labelTextView);
    }

    public void setText(String questionText, boolean isRequiredQuestion, float fontSize) {
        this.questionText = questionText;

        if (questionText != null && !questionText.isEmpty()) {
            labelTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
            labelTextView.setText(StringUtils.textToHtml(FormEntryPromptUtils.markQuestionIfIsRequired(questionText, isRequiredQuestion)));
            labelTextView.setMovementMethod(LinkMovementMethod.getInstance());

            // Wrap to the size of the parent view
            labelTextView.setHorizontallyScrolling(false);
        } else {
            labelTextView.setVisibility(View.GONE);
        }
    }

    public void setAudio(String audioURI, AudioHelper audioHelper) {
        setupAudioButton(audioURI, audioHelper);
    }

    public void setImage(@NonNull File imageFile) {
        setupImage(imageFile);
    }

    public void setBigImage(@NonNull File bigImageFile) {
        this.bigImageFile = bigImageFile;
    }

    public void setVideo(@NonNull File videoFile) {
        this.videoFile = videoFile;
        setupVideoButton();
    }

    public void setPlayTextColor(int textColor) {
        playTextColor = textColor;
        audioButton1212.setColors(getThemeUtils().getColorOnSurface(), playTextColor);
    }

    public void playVideo() {
        if (!videoFile.exists()) {
            // We should have a video clip, but the file doesn't exist.
            String errorMsg = getContext().getString(R.string.file_missing, videoFile);
            Timber.d("File %s is missing", videoFile);
            ToastUtils.showLongToast(errorMsg);
            return;
        }

        Intent intent = new Intent("android.intent.action.VIEW");
        Uri uri =
                ContentUriProvider.getUriForFile(getContext(), "com.samagra.sakshamSamiksha.provider", videoFile);
        FileUtils.grantFileReadPermissions(intent, uri, getContext());
        intent.setDataAndType(uri, "video/*");
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        } else {
            ToastUtils.showShortToast(getContext().getString(R.string.activity_not_found, getContext().getString(R.string.view_video)));
        }
    }

    public TextView getLabelTextView() {
        return labelTextView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getMissingImage() {
        return missingImage;
    }

    public Button getVideoButton1212() {
        return videoButton1212;
    }

    public Button getAudioButton() {
        return audioButton1212;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.videoButton1212) {
            playVideo();
        } else if (id == R.id.imageView) {
            onImageClick();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        labelTextView = findViewById(R.id.text_label);
        if(labelTextView != null)
        labelTextView.setEnabled(enabled);
        imageView = findViewById(R.id.imageView);
        if(imageView != null)
        imageView.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return labelTextView.isEnabled() && imageView.isEnabled();
    }

    private void onImageClick() {
        if (bigImageFile != null) {
            openImage();
        } else {
            selectItem();
        }
    }

    private void openImage() {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            Uri uri =
                    ContentUriProvider.getUriForFile(getContext(), "com.samagra.sakshamSamiksha.provider", bigImageFile);
            FileUtils.grantFileReadPermissions(intent, uri, getContext());
            intent.setDataAndType(uri, "image/*");
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.d(e, "No Activity found to handle due to %s", e.getMessage());
            ToastUtils.showShortToast(getContext().getString(R.string.activity_not_found,
                    getContext().getString(R.string.view_image)));
        }
    }

    private void selectItem() {
        if (labelTextView instanceof RadioButton) {
            ((RadioButton) labelTextView).setChecked(true);
        } else if (labelTextView instanceof CheckBox) {
            CheckBox checkbox = (CheckBox) labelTextView;
            checkbox.setChecked(!checkbox.isChecked());
        }
        if (listener != null) {
            listener.onItemClicked();
        }
    }

    private void setupImage(File imageFile) {
        String errorMsg = null;

        if (imageFile.exists()) {
            Bitmap b = FileUtils.getBitmapScaledToDisplay(imageFile, ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth());
            imageView = findViewById(R.id.imageView);
            if (b != null) {
                imageView.setVisibility(VISIBLE);
                imageView.setImageBitmap(b);
                imageView.setOnClickListener(this);
            } else {
                // Loading the image failed, so it's likely a bad file.
                errorMsg = getContext().getString(R.string.file_invalid, imageFile);
            }
        } else {
            // We should have an image, but the file doesn't exist.
            errorMsg = getContext().getString(R.string.file_missing, imageFile);
        }

        if (errorMsg != null) {
            // errorMsg is only set when an error has occurred
            Timber.e(errorMsg);
            imageView.setVisibility(View.GONE);
            missingImage.setVisibility(VISIBLE);
            missingImage.setText(errorMsg);
        }
    }

    private void setupVideoButton() {
        videoButton1212.setVisibility(VISIBLE);
        mediaButtonsContainer.setVisibility(VISIBLE);
        videoButton1212.setOnClickListener(this);
    }

    private void setupAudioButton(String audioURI, AudioHelper audioHelper) {
        audioButton1212.setVisibility(VISIBLE);
        mediaButtonsContainer.setVisibility(VISIBLE);

        ScreenContext activity = getScreenContext();
        String clipID = getTag() != null ? getTag().toString() : "";
        LiveData<Boolean> isPlayingLiveData = audioHelper.setAudio(audioButton1212, new Clip(clipID, audioURI));

        originalTextColor = labelTextView.getTextColors().getDefaultColor();
        isPlayingLiveData.observe(activity.getViewLifecycle(), isPlaying -> {
            if (isPlaying) {
                labelTextView.setTextColor(playTextColor);
            } else {
                labelTextView.setTextColor(originalTextColor);
                // then set the text to our original (brings back any html formatting)
                labelTextView.setText(questionText);
            }
        });
    }

    @NotNull
    private ThemeUtils getThemeUtils() {
        return new ThemeUtils(getContext());
    }

    private ScreenContext getScreenContext() {
        try {
            return (ScreenContext) getContext();
        } catch (ClassCastException e) {
            throw new RuntimeException(getContext().toString() + " must implement " + ScreenContext.class.getName());
        }
    }

    public void setItemClickListener(SelectItemClickListener listener) {
        this.listener = listener;
    }
}
