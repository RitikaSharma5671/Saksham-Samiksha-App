package com.example.student_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.student_details.R

class LKLK : Fragment(){

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //        val dataBindingUtil = ClassFilterLayoutBinding.inflate(inflater, container, false)
//        dataBindingUtil.classFilterViewModel = filterViewModel
        return inflater.inflate(R.layout.jkjk, container, false)
    }
}
