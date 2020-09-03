package com.example.student_details.ui

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources: Resources = context.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }



