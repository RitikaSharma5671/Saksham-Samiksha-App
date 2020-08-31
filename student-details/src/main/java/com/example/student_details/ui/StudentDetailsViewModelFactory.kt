package com.example.student_details.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StudentDetailsViewModelFactory (     private val application: Application,
            private val ss: String
    ) :
            ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudentDetailsViewModel(application, ss) as T
        }
    }