package com.example.student_details.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.assets.uielements.ArcAngleAnimation
import com.example.assets.uielements.ArcAngleAnimation.addCommasToNumber
import com.example.student_details.R
import kotlinx.android.synthetic.main.eelayout.*
import kotlinx.android.synthetic.main.shiksha_mitr_add.*

class SSS:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eelayout)
        bar1.setCurrentValues(80f);

        //Initialization
        arcProgressInit()
    }
    private var emptyString = "" //Empty string when getting null from the response
    private var mBalance = 0
    private val mAnimationDuration:Long = 1000 //Animation Duration in Mili-second


    override fun onResume() {
        super.onResume()

        //checking if arcprogress is not null and setting the items in that
        //and starting the animation
        if (arc_progress != null) {
            arc_progress.setmRewardEarned(addCommasToNumber(mBalance))
            // arc_progress.setmNextGoal(Utils.addCommasToNumber(mNearestRewardPoints));
            arc_progress.setmPointsTo(message1)
            arc_progress.setmRewardPrice(message2)
            arc_progress.setmProgress(mBalance * mMax / mNearestRewardPoints)
            val animation = ArcAngleAnimation(arcProgress, arc_progress.getmProgress(), mBalance)
            animation.duration = mAnimationDuration
            arc_progress.startAnimation(animation)
        }
    }

    fun arcProgressInit() {
        arc_progress.setmRewardEarned(emptyString)
        arc_progress.setmPointsTo(emptyString)
        arc_progress.setmRewardPrice(emptyString)
        arc_progress.setmProgress(0)
        val animation = ArcAngleAnimation(arc_progress, arc_progress.getmProgress(), mBalance)
        animation.setDuration(mAnimationDuration)
        arc_progress.startAnimation(animation)
    }

}