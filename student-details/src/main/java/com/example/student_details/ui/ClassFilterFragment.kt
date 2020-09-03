package com.example.student_details.ui

import android.app.ProgressDialog
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assets.uielements.SamagraAlertDialog
import com.example.student_details.R
import com.example.student_details.databinding.ClassFilterLayoutBinding
import com.example.student_details.getViewModelProvider
import kotlinx.android.synthetic.main.filters_layout.*

class ClassFilterFragment : Fragment() {
    private val filterViewModel: ClassFilterViewModel by lazy {
        getViewModelProvider(this).get(
                ClassFilterViewModel::class.java
        )
    }
    private lateinit var mProgress: ProgressDialog

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val dataBindingUtil = ClassFilterLayoutBinding.inflate(inflater, container, false)
        dataBindingUtil.classFilterViewModel = filterViewModel
        return dataBindingUtil.root
    }

    private fun addFragment(containerViewId: Int, manager: FragmentManager, fragment: Fragment, fragmentTag: String) {
        try {
            val transaction = manager.beginTransaction()
            transaction.add(containerViewId, fragment, fragmentTag)
            transaction.addToBackStack(fragmentTag)
            Handler(Looper.getMainLooper()).post {
                try {
                    transaction.commit()
                } catch (ex: java.lang.IllegalStateException) {
                    // reportException(new IllegalStateException("Non App crash custom Exception in addFragment in " + fragmentname,ex));
                }
            }
        } catch (ex: java.lang.IllegalStateException) {
            //  reportException(new IllegalStateException("Non App crash custom Exception addFragment",ex));
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterAttributes = FilterAttributes()
        val grades = ArrayList<Int>()
        grades.addAll(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12))
        val sections = ArrayList<String>()
        sections.addAll(arrayOf("A", "B", "C", "D"))
        val streams = ArrayList<String>()
        streams.addAll(arrayOf("HUMANITIES", "SCIENCE", "COMMERCE", "VOCATIONAL"))
        filterAttributes.allGrades = grades
        filterAttributes.allSections = sections
        filterAttributes.allStreams = streams
        filterViewModel.dataFilterAttributes.value = filterAttributes
        filterViewModel.selectedGrades.value = ArrayList()
        setupAdapter()
        view.findViewById<ImageView>(R.id.filter_close_cross).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
//
        view.findViewById<TextView>(R.id.apply_button).setOnClickListener {
            if (filterViewModel.selectedGrades.value != null &&
                    filterViewModel.selectedGrades.value!!.size > 0) {
                mProgress.show()


            } else {
                SamagraAlertDialog.Builder(requireContext()).setTitle(getText(R.string.no_grade_selected)).setMessage(getText(R.string.please_select_atleast_one_grade)).setAction2(getText(R.string.ok),
                        object : SamagraAlertDialog.CaastleAlertDialogActionListener {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                                alertDialog.dismiss()
                            }
                        }).show()
            }
//            filterViewModel.applyFilter()
//            val markAttendanceView = MarkAttendanceView()
//            addFragment(R.id.bleh_fragment_container, parentFragmentManager!!, markAttendanceView, "MarkAttendanceView")

        }

        view.findViewById<TextView>(R.id.clear_button).setOnClickListener {
            filterViewModel.clearFilter()
            class_recycler_view.adapter!!.notifyDataSetChanged()
            section_recycler_view.adapter!!.notifyDataSetChanged()
            stream_recycler_view.adapter!!.notifyDataSetChanged()
        }

        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.please_wait))
        mProgress.setMessage(getString(R.string.fetching_student_data))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true

//        filterViewModel.isClearButtonEnabled.observe(viewLifecycleOwner, Observer {
//            view.findViewById<Button>(R.id.clear_button).isEnabled = it
//        })
//
//        filterViewModel.isApplyButtonEnabled.observe(viewLifecycleOwner, Observer {
//            view.findViewById<Button>(R.id.apply_button).isEnabled = it
//        })
//
//        filterViewModel.navigateBack.observe(this, Observer {
//            if (!it.hasBeenHandled) {
////                val queryParameters = it.getContentIfNotHandled()
////                when (queryParameters) {
////                    null -> findNavController().popBackStack()//findNavController().navigateUp()
////                    else -> {
////                        productCollectionViewModel.applyFilter(queryParameters)
////                        findNavController().navigateUp()
////                    }
////                }
//            }
//        })
    }

    private fun setupAdapter() {
        class_recycler_view.setHasFixedSize(true)
        class_recycler_view.adapter =
                SizeFilterAdapter(filterViewModel)
        val size = Point()
        (context as FragmentActivity).windowManager.defaultDisplay.getSize(size)
        val itemWidth = context!!.resources.getDimensionPixelSize(R.dimen.filter_size_item_diameter_size)
        val parentWidth = size.x
        val spanCount = Math.min(parentWidth / itemWidth, 6)
        class_recycler_view.layoutManager = GridLayoutManager(context, spanCount)
        filterViewModel.dataFilterAttributes.observe(viewLifecycleOwner, Observer {
            class_recycler_view.adapter?.notifyDataSetChanged()
        })


        section_recycler_view.setHasFixedSize(true)
        section_recycler_view.adapter =
                SectionSizeAdapter(filterViewModel)
        val itemWidth2 = convertDpToPixel(48.0f, context!!).toInt()
        val parentWidth2 = size.x
        var spanCount2 = Math.min(parentWidth2 / itemWidth2, 6)
        spanCount2 = Math.min(spanCount2, filterViewModel.dataFilterAttributes.value!!.allSections!!.size)
        section_recycler_view.layoutManager = GridLayoutManager(context, spanCount2)
        filterViewModel.dataFilterAttributes.observe(viewLifecycleOwner, Observer {
            section_recycler_view.adapter?.notifyDataSetChanged()
        })

        stream_recycler_view.setHasFixedSize(true)
        stream_recycler_view.adapter =
                StreamSizeAdapter(filterViewModel)
        val itemWidth1 = convertDpToPixel(110.0f, context!!).toInt()
        val parentWidth1 = size.x
        val spanCount1 = Math.min(parentWidth1 / itemWidth1, 3)
        stream_recycler_view.layoutManager = GridLayoutManager(context, spanCount1)
        filterViewModel.dataFilterAttributes.observe(viewLifecycleOwner, Observer {
            stream_recycler_view.adapter?.notifyDataSetChanged()
        })

    }


}