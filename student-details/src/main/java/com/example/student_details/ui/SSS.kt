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
//        bar1.setCurrentValues(80f);

        //Initialization
        arcProgressInit()
    }
    private var emptyString = "" //Empty string when getting null from the response
    private var mBalance = 0
    private val mAnimationDuration:Long = 2000 //Animation Duration in Milli-second


    override fun onResume() {
        super.onResume()

        //checking if arc progress is not null and setting the items in that
        //and starting the animation
        if (arc_progress != null) {
            arc_progress.setmRewardEarned("100%")
             arc_progress.setmPointsTo("Shiksha Mitra")
            arc_progress.setmRewardPrice("Registered")

            arc_progress.setmProgress(100)
            val animation = ArcAngleAnimation(arc_progress, arc_progress.getmProgress(), mBalance)
            animation.duration = mAnimationDuration
            arc_progress.startAnimation(animation)
        }
    }

    fun arcProgressInit() {
        arc_progress.setmRewardEarned("100%")
        arc_progress.setmPointsTo("Shiksha Mitra")
        arc_progress.setmRewardPrice("Registered")
        arc_progress.setmProgress(100)
        val animation = ArcAngleAnimation(arc_progress, arc_progress.getmProgress(), mBalance)
        animation.duration = mAnimationDuration
        arc_progress.startAnimation(animation)
    }

}