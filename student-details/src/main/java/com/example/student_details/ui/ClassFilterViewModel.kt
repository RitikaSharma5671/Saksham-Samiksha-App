package com.example.student_details.ui

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ClassFilterViewModel(application: Application) : AndroidViewModel(application) {

    val isClearButtonEnabled = MutableLiveData<Boolean>()
    val dataFilterAttributes = MutableLiveData<FilterAttributes>()
    val selectedGrades = MutableLiveData<ArrayList<Int>>()
    val selectedSections = MutableLiveData<ArrayList<String>>()
    val selectedStreams = MutableLiveData<ArrayList<String>>()
    val isApplyButtonEnabled = ObservableBoolean(false)

    fun onGradeSelected(size: Int, selected: Boolean) {
        if (selected) {
            var list = selectedGrades.value
            if (list != null && !list.contains(size)) {
                list.add(size)
            } else if (list == null) {
                list = ArrayList()
                list.add(size)
            }
            selectedGrades.value = list
        } else {
            val list = selectedGrades.value
            if (list != null && list.contains(size)) {
                list.remove(size)
            }
            selectedGrades.value = list
        }
    }

    fun onStreamSelected(size: String, selected: Boolean) {
        if (selected) {
            var list = selectedStreams.value
            if (list != null && !list.contains(size)) {
                list.add(size)
            } else if (list == null) {
                list = ArrayList()
                list.add(size)
            }
            selectedStreams.value = list
        } else {
            val list = selectedStreams.value
            if (list != null && list.contains(size)) {
                list.remove(size)
            }
            selectedStreams.value = list
        }
    }

    fun onSectionSelected(size: String, selected: Boolean) {
        if (selected) {
            var list = selectedSections.value
            if (list != null && !list.contains(size)) {
                list.add(size)
            } else if (list == null) {
                list = ArrayList()
                list.add(size)
            }
            selectedSections.value = list
        } else {
            val list = selectedSections.value
            if (list != null && list.contains(size)) {
                list.remove(size)
            }
            selectedSections.value = list
        }
    }

    fun clearFilter() {
         selectedGrades.value = ArrayList()
         selectedSections.value  = ArrayList()
        selectedStreams.value = ArrayList()
    }

    fun initialize() {
        isApplyButtonEnabled.set(false)
    }

}