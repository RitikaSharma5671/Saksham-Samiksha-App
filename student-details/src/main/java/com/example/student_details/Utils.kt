package com.example.student_details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider


fun getViewModelProvider(
        fragment: Fragment,
        factory: ViewModelProvider.Factory
): ViewModelProvider {
    return ViewModelProvider(fragment, factory)
}

fun getViewModelProvider(
        activity: FragmentActivity,
        factory: ViewModelProvider.Factory
): ViewModelProvider {
    return ViewModelProvider(activity, factory)
}

//fun getViewModelProvider(activity: FragmentActivity): ViewModelProvider {
//    return ViewModelProvider(activity)
//}

fun getViewModelProvider(fragment: Fragment): ViewModelProvider {
    return ViewModelProvider(fragment)
}

