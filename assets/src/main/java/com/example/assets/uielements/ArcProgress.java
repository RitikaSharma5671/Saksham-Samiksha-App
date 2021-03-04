package com.example.assets.uielements;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

 import com.example.assets.R;

/**
 * GBClosetApp Arc Progress
 * This class will create the custom view of the mProgress bar in reward fragment
 *
 * @author Vidur Sachdeva
 * @version 1.1
 */

public class ArcProgress extends View implements View.OnTouchListener {
    private Bitmap bitmap;//Bitmap
    //creating Paint Object to make arc and text
    private Paint mPaint;//Paint object
    private Paint mTextPaint;//Paint object
    private RectF mRectF = new RectF();//create the object of RectF that has all 4 coordinates
    //Defining the strings for the view
    private String mRewardEarned;//Reward Earned String
    private String mPointsTo;//points to reward String
    private String mRewardPrice;//reward price string
    //Defining the color attribute
    private int mPointsToColor;//points to string color
    private int mRewardPriceColor;//reward string color
    private int textColor;//Text Color for the texts
    private int mRewardTextColor;//reward string color
    private int mFinishedStrokeColor;//color of the unfinished arc
    private int mUnfinishedStrokeColor;
    private int mProgress = 0;//initial progress
    private int mMax;//maximum
    //Defining Text Sizes and Angles
    private float mStrokeWidth;//This is the width of the arc progress
    private float mRewardPriceSize;//Text Size of the reward price string
    private float mPointsToSize;//Text Size of the points to reward string
    // private float mStaticNextGoalSize;//Text Size of Static Next Goal
    private float textSize;//Text Size of fonts default
    private float mTvRewardTextSize;//Text Size of the reward string
    private float mArcAngle;//Arc angle will change for animation
    private float mNextGoalSize;//Next Goal Text Size
    private final float mDefaultUnfinishedArcStartingAngle = 135f;//angle to start the unfinished arc
    private final float getmDefaultUnfinishedArcSweepingAngle = 270f;// angle to finish the unfinished arc
    //Default values to be used if not give in xml as attributes
    private final int mDefaultFinishColor = Color.WHITE; //Default Finish Color is White
    private final int mDefaultUnfinishedColor = Color.RED;//Default Unfinished Color is Red
    private final int mDefaultTextColor = Color.BLACK;//Default text color is black
    private final float mDefaultStrokeWidth;//Default Stroke
    private final int mDefaultMax = 100; //Default Maximum
    private final float mDefaultArcAngle = 360 * 0.4f; // Default arc Angle
    private float mDefaultTextSize;//Default Text Size if not given
    private final int mMinSize;//Default Minimum               \
    //Definig the Typeface for texts
    //Defining final Variables
    private final float ONE = 1.0f;
    private final float TWO = 2.0f;
    //Scale X for Bigger Texts
    private final float mScaleX = 1.6f;
    //Listener
//    private CustomViewClickListener customViewClickListener;
    //Defining the width and height of image
    private float mWidth;
    //defining the Default height and width
    private float mDefaultHeight = 120;
    private float mDefaultWidth = 190;
    private boolean crashLogAlreadyThrown;

    // private RectF mSmilyRectancle = new RectF();
    public interface CustomViewClickListener {
        void customTextClick();
    }

    /**
     * Constructor.
     *
     * @param context (required) Context of the Application
     */
    public ArcProgress(Context context) {
        this(context, null);
    }

    /**
     * Constructor.
     *
     * @param context (required) Context of the Application
     * @param attrs   (required)
     */
    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor.
     *
     * @param context      (required) Context of the Application
     * @param attrs        (required)
     * @param defStyleAttr (required)
     */
    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.setOnTouchListener(this);
//        customViewClickListener = (CustomViewClickListener) context;

        mDefaultTextSize = sp2px(getResources(), 18);
        mMinSize = (int) dp2px(getResources(), 100);
        mDefaultTextSize = sp2px(getResources(), 40);
        mDefaultStrokeWidth = dp2px(getResources(), 4);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();
        initPainters();
    }
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    /**
     * This function will initialize the attributes defined in attr.xml
     *
     * @param attributes
     */
    private void initByAttributes(TypedArray attributes) {
        //initializing the Color of the arc and text views
        mFinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, mDefaultFinishColor);
        mUnfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, mDefaultUnfinishedColor);
        mRewardTextColor = attributes.getColor(R.styleable.ArcProgress_tv_reward_text_color, mDefaultTextColor);
        textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, mDefaultTextColor);
        mPointsToColor = attributes.getColor(R.styleable.ArcProgress_tv_points_to_color, mDefaultTextColor);
        mRewardPriceColor = attributes.getColor(R.styleable.ArcProgress_tv_points_to_color, mDefaultTextColor);
        //initializing the text size attributes
        textSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, mDefaultTextSize);
        mTvRewardTextSize = attributes.getDimension(R.styleable.ArcProgress_tv_reward_text_size, mDefaultTextSize);
        mPointsToSize = attributes.getDimension(R.styleable.ArcProgress_tv_points_to_text_size, mDefaultTextSize);
        mNextGoalSize = attributes.getDimension(R.styleable.ArcProgress_tv_next_goal_text_size, mDefaultTextSize);
        mRewardPriceSize = attributes.getDimension(R.styleable.ArcProgress_tv_points_to_text_size, mDefaultTextSize);
        //initializing the height and width of image
        mWidth = attributes.getDimension(R.styleable.ArcProgress_image_width, mDefaultWidth);
        //initializing the Maximum and progress
        setmMax(attributes.getInt(R.styleable.ArcProgress_arc_max, mDefaultMax));
        setmProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        //Initializing the Arc Angle and Stroke width Attributes
        mArcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, mDefaultArcAngle);
        mStrokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, mDefaultStrokeWidth);
        //Initializing the the text attributes
        mRewardEarned = attributes.getString(R.styleable.ArcProgress_tv_reward_text);
        mPointsTo = attributes.getString(R.styleable.ArcProgress_tv_points_to);
        mRewardPrice = attributes.getString(R.styleable.ArcProgress_tv_reward);


    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    /**
     * Method to set smile image in Progress bar according to rewards earned by user
     *
     * @param rewardsEarned
     */
    private void generateBitMapForSmile(int rewardsEarned) {
//        rewardsEarned = GBAppConfig.getUserRewardsPoints();
        rewardsEarned = 100;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.starrr);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 35f, true);

//        if (rewardsEarned == 0) {
//            bitmap =getBitmap(R.drawable.ic_star_svgrepo_com);
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 35f, true);
//        } else if (rewardsEarned > 0 && rewardsEarned < 10) {
//            bitmap = getBitmap(R.drawable.ic_star_svgrepo_com);
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 40f, true);
//        } else if (rewardsEarned >= 10 && rewardsEarned < 2000) {
//            bitmap = getBitmap( R.drawable.ic_star_svgrepo_com);
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 50f, true);
//        } else if (rewardsEarned >= 2000 && rewardsEarned < 6000) {
//            bitmap = getBitmap( R.drawable.ic_star_svgrepo_com);
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 70f, true);
//        } else if (rewardsEarned >= 6000) {
//            bitmap = getBitmap( R.drawable.ic_star_svgrepo_com);
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) mWidth, (int) 83f, true);
//        }
    }

    /**
     * This function will initialize paint and set the initial params for paint
     */
    private void initPainters() {
        mTextPaint = new TextPaint();//initialize the mTextPaint
        mTextPaint.setColor(textColor);//color of the paint
        mTextPaint.setTextSize(textSize);//initial text size
        mTextPaint.setAntiAlias(true);//set Alias
        mPaint = new Paint();
        mPaint.setColor(mDefaultUnfinishedColor);//set initial unfinished color
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);//stroke width
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//cap of the arc should be rounded
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    /**
     * This function is used to get the progress of the arc
     *
     * @return progress of the arc
     */
    public int getmProgress() {
        return mProgress;
    }

    /**
     * Sets the Progress of the arc
     *
     * @param mProgress
     */
    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
        if (this.mProgress > getmMax()) {
            this.mProgress %= getmMax();
        }
        invalidate();
    }

    /**
     * This function is used to get the Maximum
     *
     * @return max
     */
    public int getmMax() {
        return mMax;
    }

    /**
     * Set the Maximum
     *
     * @param mMax
     */
    public void setmMax(int mMax) {
        if (mMax > 0) {
            this.mMax = mMax;
            invalidate();
        }
    }

    /**
     * This function will get the points remaining to earn the reward string
     *
     * @return string for ex "230 points to"
     */
    public String getmPointsTo() {
        return mPointsTo;
    }

    /**
     * Sets the the points remaining to earn the reward string
     *
     * @param mPointsTo
     */
    public void setmPointsTo(String mPointsTo) {
        this.mPointsTo = mPointsTo;
        this.invalidate();
    }

    /**
     * This function will get the reward price string
     *
     * @return String ex "$15 reward"
     */
    public String getmRewardPrice() {
        return mRewardPrice;
    }

    /**
     * Sets the reward price string
     *
     * @param mRewardPrice
     */
    public void setmRewardPrice(String mRewardPrice) {
        this.mRewardPrice = mRewardPrice;
        this.invalidate();
    }

    /**
     * This function will get the String of Reward Earned by User
     *
     * @return String ex "1,800"
     */
    public String getmRewardEarned() {
        return mRewardEarned;
    }

    /**
     * Sets the String of Reward Earned by User
     *
     * @param mRewardEarned
     */
    public void setmRewardEarned(String mRewardEarned) {
        this.mRewardEarned = mRewardEarned;
        this.invalidate();
    }

    /**
     * This function will return the instance of Paint class
     *
     * @return mPaint
     */
    public Paint getmPaint() {
        return mPaint;
    }

    /**
     * Sets the instance of Paint class
     *
     * @param mPaint
     */
    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    /**
     * This function will be used to get Arc Angle which can be used for animation
     *
     * @return mArcAngle
     */
    public float getmArcAngle() {
        return mArcAngle;
    }

    /**
     * Sets Arc Angle
     */
    public void setmArcAngle(float mArcAngle) {
        this.mArcAngle = mArcAngle;
        this.invalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getSuggestedMinimumHeight() {
        return mMinSize;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    protected int getSuggestedMinimumWidth() {
        return mMinSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mRectF.set(mStrokeWidth / TWO, mStrokeWidth / TWO, width - mStrokeWidth / TWO, MeasureSpec.getSize(heightMeasureSpec) - mStrokeWidth / TWO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //setting the color for the unfinished Arc
        mPaint.setColor(mUnfinishedStrokeColor);
        //Draw the arc with angle 135 degrees to 270 degrees
        canvas.drawArc(mRectF, mDefaultUnfinishedArcStartingAngle, getmDefaultUnfinishedArcSweepingAngle, false, mPaint);//no animation
        //setting the finished Arc color
        mPaint.setColor(mFinishedStrokeColor);
        //Draw the finished Arc and animate it using the mArcAngle by changing it in ArcAngleAnimation Class
        canvas.drawArc(mRectF, mDefaultUnfinishedArcStartingAngle, mArcAngle, false, mPaint);// animate

        //setting Points earned text properties
        if (!TextUtils.isEmpty(mRewardEarned)) {
            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);//setting Typeface
            mTextPaint.setTextScaleX(ONE);//setting the thickness of the letter
            mTextPaint.setColor(mRewardTextColor);//setting the color
            mTextPaint.setTextSize(mTvRewardTextSize);//setting the text size
            float textHeight = mTextPaint.descent() + mTextPaint.ascent() + 30;//setting the height
            float textBaseline = (getHeight() - textHeight) / 3.0f;//setting the width
            canvas.drawText(mRewardEarned, (getWidth() - mTextPaint.measureText(mRewardEarned)) / TWO, textBaseline, mTextPaint);
            // passing reward earned by user to set image in progress Arc
            try {
                generateBitMapForSmile(Integer.parseInt(mRewardEarned));
            } catch (NumberFormatException e) {
                //Will Throw exception!
                //throw new RuntimeException("ArcProgress >> generateBitMapForSmile()");
            }
        }


        //Setting pts below earned points in progress bar
        mTextPaint.setTypeface(Typeface.DEFAULT);//setting Typeface
        mTextPaint.setTextScaleX(ONE);//setting the thickness of the letter
        mTextPaint.setColor(mRewardTextColor);//setting the color
        mTextPaint.setTextSize(50);//setting the text size
        float textHeight = mTextPaint.descent() + mTextPaint.ascent();//setting the height
        float textBaseline = (getHeight() - textHeight) / 2.4f;//setting the width
//        canvas.drawText("pts", (getWidth() - mTextPaint.measureText("pts")) / TWO, textBaseline, mTextPaint);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, ((getWidth() - bitmap.getWidth()) / TWO), getHeight() / 1.9f, mPaint);
        } else {
//            if (!crashLogAlreadyThrown)
//                Crashlytics.logException(new Throwable("Arc Progress Custom Crash"));
//            crashLogAlreadyThrown = true;
        }

        //setting Next Goal Points text properties
        if (!TextUtils.isEmpty(mPointsTo)) {
            mTextPaint.setTypeface(Typeface.DEFAULT);//setting Typeface
            mTextPaint.setTextScaleX(ONE);//setting the thickness of the letter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextPaint.setLetterSpacing(0.10f);
            }
            mTextPaint.setColor(mPointsToColor);//setting the color
            mTextPaint.setTextSize(mPointsToSize);//setting the size
            float bottomTextBaseline = (getHeight() - (mTextPaint.descent() + mTextPaint.ascent())) / 1.25f;//1.2setting the height
            canvas.drawText(mPointsTo, (getWidth() - mTextPaint.measureText(mPointsTo)) / TWO, bottomTextBaseline, mTextPaint);
        }

        //setting Reward price text properties
        if (!TextUtils.isEmpty(mRewardPrice)) {
            mTextPaint.setTypeface(Typeface.DEFAULT);//setting Typeface
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextPaint.setLetterSpacing(0.10f);
            }
            mTextPaint.setTextScaleX(ONE);//setting the thickness of the letter
            mTextPaint.setColor(mPointsToColor);//setting the color
            float bottomTextBaseline = (getHeight() - (mTextPaint.descent() + mTextPaint.ascent())) / 1.15f;//1.1f  setting the height
            canvas.drawText(mRewardPrice, (getWidth() - mTextPaint.measureText(mRewardPrice)) / TWO, bottomTextBaseline, mTextPaint);
        }
    }

    /**
     * Setting touch event on String which takes user to how to earn screen
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if (event.getY() >= (getHeight() - (mTextPaint.descent() + mTextPaint.ascent())) / 1.3f && event.getY() <= (getHeight() - (mTextPaint.descent() + mTextPaint.ascent())) / 1.1f && event.getX() >= (getWidth() - mTextPaint.measureText(mPointsTo)) / TWO && event.getX() <= (getWidth() + mTextPaint.measureText(mPointsTo)) / TWO) {
//            if (customViewClickListener != null) {
//                customViewClickListener.customTextClick();
//            }
//        }
        return false;
    }
}
