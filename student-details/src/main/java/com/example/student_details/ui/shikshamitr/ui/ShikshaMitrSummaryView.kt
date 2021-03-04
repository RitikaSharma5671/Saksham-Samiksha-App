package com.example.student_details.ui.shikshamitr.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.student_details.R
import com.example.student_details.databinding.SamikshaShikshaMitrDetailsView1Binding
import com.example.student_details.getViewModelProvider
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.ui.shikshamitr.AddOrUpdateShikshaMitrDetails
import kotlinx.android.synthetic.main.fragment_student_details.*
import kotlinx.android.synthetic.main.samiksha_shiksha_mitr_details_view.hundered_layout
import kotlinx.android.synthetic.main.samiksha_shiksha_mitr_details_view1.*
import java.util.*
import kotlin.collections.ArrayList


class ShikshaMitrSummaryView : AppCompatActivity() ,ISMEditIconClickListener{
    private var start: Boolean = false
    private var selectedGradeInt: Int = 0
    private var selectedStream: String = ""
    private lateinit var shikshaMitrAdapter: ShikshaMitrAdapter
    private val shikshaMitrDetailsViewModel: ShikshaMitrDetailsViewModel by lazy {
        getViewModelProvider(this).get(
                ShikshaMitrDetailsViewModel::class.java
        )
    }
    private lateinit var samikshaShikshaMitrDetailsViewBinding: SamikshaShikshaMitrDetailsView1Binding

    private lateinit var selectedGrade: String
    private lateinit var selectedSection: String
    private var selectedSectionPosition: Int = 0
    private var selectedGradePosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        samikshaShikshaMitrDetailsViewBinding =
                DataBindingUtil.setContentView(this, R.layout.samiksha_shiksha_mitr_details_view1)
        samikshaShikshaMitrDetailsViewBinding.progressBarSmScreen.visibility = View.GONE
        samikshaShikshaMitrDetailsViewBinding.noStudentSm.visibility = View.GONE
        samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.GONE
        samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.GONE
        samikshaShikshaMitrDetailsViewBinding.hunderedLayout.visibility = View.GONE
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("ssss", false).apply()
        start = true
        initFilters()
        close_cross_sm_summary_screen1.setOnClickListener {
            dc()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(false)
//        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.cross_icon)
//        supportActionBar!!.setDisplayShowHomeEnabled(true);
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
     }

    private fun dc() {
        finish()

    }

    override fun onResume() {
        super.onResume()

        initializeAdapter(samikshaShikshaMitrDetailsViewBinding)
        shikshaMitrDetailsViewModel.progressBarVisible.observe(this, Observer {
            if (selectedGradeInt != 0) {
                if (it == "true") {
                    samikshaShikshaMitrDetailsViewBinding.studentSmLayout.isClickable = false
                    samikshaShikshaMitrDetailsViewBinding.progressBarSmScreen.visibility = View.VISIBLE
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.GONE
                    samikshaShikshaMitrDetailsViewBinding.hunderedLayout.visibility = View.GONE
                } else {
                    samikshaShikshaMitrDetailsViewBinding.studentSmLayout.isClickable = true
                    samikshaShikshaMitrDetailsViewBinding.progressBarSmScreen.visibility = View.GONE

                }
            }
        })
        shikshaMitrDetailsViewModel.studentsList.observe(this, Observer {
            val productList = shikshaMitrDetailsViewModel.studentsList.value
            if (selectedGradeInt != 0) {
                when {
                    productList != null && productList.isNotEmpty() -> {
                        samikshaShikshaMitrDetailsViewBinding.noStudentSm.visibility = View.GONE
                        samikshaShikshaMitrDetailsViewBinding.studentSmList.visibility = View.VISIBLE
                        shikshaMitrAdapter.submitList(productList)
                        shikshaMitrAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        samikshaShikshaMitrDetailsViewBinding.noStudentSm.visibility = View.VISIBLE
                        samikshaShikshaMitrDetailsViewBinding.studentSmList.visibility = View.GONE
                        samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.GONE
                        samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.GONE
                        samikshaShikshaMitrDetailsViewBinding.hunderedLayout.visibility = View.GONE
                        initializeAdapter(samikshaShikshaMitrDetailsViewBinding)
                    }
                }
            }
        })

        shikshaMitrAdapter.submitList(studentList)
        initialiseArcProgress()
        if (selectedGradePosition > 0) {
            shikshaMitrDetailsViewModel.onApplyFiltersClicked(samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1).toInt(),
                    samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1], selectedStream)
        }
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

    private fun initialiseArcProgress() {
        shikshaMitrDetailsViewModel.findInitialRegisterations()
        shikshaMitrDetailsViewModel.shikshaMitraRegistered.observe(this, Observer {
            if (it != null) {
                if (shikshaMitrDetailsViewModel.totalStudentCount.value!! != 0) {
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.VISIBLE
                    samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.VISIBLE
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.unfinishedStrokeColor = ContextCompat.getColor(this, R.color.green)
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.finishedStrokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.textColor = ContextCompat.getColor(this, R.color.colorPrimary)
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.bottomText = ""
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.bottomTextSize = 36f
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.textSize = 95f

                    if (shikshaMitrDetailsViewModel.shikshaMitraRegistered.value!! == 0) {
                        samikshaShikshaMitrDetailsViewBinding.arcProgress.progress = 0
                        hundered_layout.visibility = View.GONE
                        samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.VISIBLE
                        samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.VISIBLE
                    } else {
                        val d: Int = (shikshaMitrDetailsViewModel.shikshaMitraRegistered.value!! * 100) / shikshaMitrDetailsViewModel.totalStudentCount.value!!
                        samikshaShikshaMitrDetailsViewBinding.arcProgress.progress = d
                        if (d >= 100) {
                            hundered_layout.visibility = View.VISIBLE
                            samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.GONE
                            samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.GONE
                        } else {
                            hundered_layout.visibility = View.GONE

                            samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.VISIBLE
                            samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.VISIBLE

                        }
                    }

                } else {
                    samikshaShikshaMitrDetailsViewBinding.arcProgress.visibility = View.GONE
                    samikshaShikshaMitrDetailsViewBinding.smTitle.visibility = View.GONE
                    samikshaShikshaMitrDetailsViewBinding.hunderedLayout.visibility = View.GONE
                }
            }
        })
    }

    private val studentList = ArrayList<StudentInfo>()

    private fun initializeAdapter(binding: SamikshaShikshaMitrDetailsView1Binding) {
        shikshaMitrAdapter = ShikshaMitrAdapter(this,
                shikshaMitrDetailsViewModel
        )
        binding.studentSmList.adapter = shikshaMitrAdapter
        binding.studentSmList.itemAnimator = DefaultItemAnimator()
        binding.studentSmList.setHasFixedSize(true)
        (binding.studentSmList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        binding.studentSmList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var dy = 0
            var dx = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (dy < 0) {
                        showHeaderFooter(true)
                    } else if (dy > 0) {
                        showHeaderFooter(false)
                    }
                } else {
                    dy = 0
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                this.dx = dx
                this.dy = dy
                super.onScrolled(recyclerView, dx, dy)
            }


        })

    }
    var isTitleStripVisible = true

    private fun showHeaderFooter(toShow: Boolean) {

        if (isTitleStripVisible == toShow) {
            return
        }

        if (toShow) {
//            mCollectionSummaryViewPager.animate().translationY(mTitleStrip.getMeasuredHeight());
            samikshaShikshaMitrDetailsViewBinding.arclayout.visibility = View.VISIBLE
        }

        isTitleStripVisible = toShow
        samikshaShikshaMitrDetailsViewBinding.arclayout.animate()
                .translationY(if (toShow) 0f else -samikshaShikshaMitrDetailsViewBinding.arclayout.measuredHeight.toFloat())
                .alpha(if (toShow) 1f else 0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        isTitleStripVisible = toShow
                        samikshaShikshaMitrDetailsViewBinding.arclayout.visibility = if (toShow) View.VISIBLE else View.GONE
                    }
                })
    }

    private fun initFilters() {
        val grades = ArrayList<String>()
        grades.add("Select Grade")
        grades.add("Class 1")
        grades.add("Class 2")
        grades.add("Class 3")
        grades.add("Class 4")
        grades.add("Class 5")
        grades.add("Class 6")
        grades.add("Class 7")
        grades.add("Class 8")
        grades.add("Class 9")
        grades.add("Class 10")
        grades.add("Class 11")
        grades.add("Class 12")
        val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, grades
        )

        val mPreconditionSpinner = samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner
        mPreconditionSpinner.adapter = adapter
        mPreconditionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val previous = selectedGradePosition
                selectedGrade = samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                selectedGradeInt = if (samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItemPosition == 0) 0 else samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1].toInt()
                shikshaMitrDetailsViewModel.selectedGrade.postValue(selectedGradeInt)
                selectedGradePosition = samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItemPosition
                if (samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItemPosition > 0) {
                    samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.isEnabled = true
                    if (selectedGrade == "11" || selectedGrade == "12") {
                        samikshaShikshaMitrDetailsViewBinding.smDetailsStreamCategoryRg.visibility = View.VISIBLE
                    } else {
                        samikshaShikshaMitrDetailsViewBinding.smDetailsStreamCategoryRg.visibility = View.GONE
                    }
                    shikshaMitrDetailsViewModel.onApplyFiltersClicked(samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1).toInt(),
                            samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1], selectedStream)

                } else {
                    samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.isEnabled = false
                    if (samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItemPosition > 0 ||
                            samikshaShikshaMitrDetailsViewBinding.smDetailsStreamCategoryRg.checkedRadioButtonId != -1) {
                        Toast.makeText(this@ShikshaMitrSummaryView, "Please Select Grade before selecting Section Filter", Toast.LENGTH_LONG).show()
                        samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.setSelection(previous)
                    }else{

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
            }
        }

        val sections = ArrayList<String>()
        sections.add("Select Section")
        sections.add("Section A")
        sections.add("Section B")
        sections.add("Section C")
        sections.add("Section D")
        sections.add("Section E")
        sections.add("Section F")
        sections.add("Section G")
        sections.add("Section H")
        sections.add("Section I")
        sections.add("Section J")
        sections.add("Section K")
        sections.add("Section L")
        sections.add("Section M")
        sections.add("Section N")
        val adapter1 = ArrayAdapter(
                this, R.layout.spinner_item, sections
        )

        val mPreconditionSpinner1 = samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner
        mPreconditionSpinner1.adapter = adapter1
        mPreconditionSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                shikshaMitrDetailsViewModel.selectedSection.postValue(samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1])
                selectedSection = samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1)
                selectedSectionPosition = samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItemPosition
                if (samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItemPosition > 0) {
                    shikshaMitrDetailsViewModel.onApplyFiltersClicked(samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1].toInt(), samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1],
                            selectedStream)
                } else {
                    if (!start) {
                        Toast.makeText(this@ShikshaMitrSummaryView, "Please Select Grade before selecting Section", Toast.LENGTH_LONG).show()
                    } else {
                        start = false
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
            }
        }
        samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.setSelection(selectedGradePosition)
        samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.setSelection(selectedSectionPosition)
        samikshaShikshaMitrDetailsViewBinding.smDetailsStreamCategoryRg.setOnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked: Boolean = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                selectedStream = checkedRadioButton.text.toString()
                shikshaMitrDetailsViewModel.selectedStream.postValue(checkedRadioButton.text.toString())
                if (samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItemPosition > 0) {
                    shikshaMitrDetailsViewModel.onApplyFiltersClicked(samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1).toInt(), samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1),
                            selectedStream)
                } else {
                    Toast.makeText(this@ShikshaMitrSummaryView, "Please Select Grade before selecting Stream Filter", Toast.LENGTH_LONG).show()
                }
            } else {
                shikshaMitrDetailsViewModel.selectedStream.postValue("")
                selectedStream = ""
            }
        }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.getBackStackEntryAt(0)
            if (fm.getBackStackEntryAt(0).name != null && fm.getBackStackEntryAt(0).name == "AddOrUpdateShikshaMitrDetails") {
                fm.popBackStackImmediate()
                shikshaMitrDetailsViewModel.onApplyFiltersClicked(samikshaShikshaMitrDetailsViewBinding.smSummaryGradeSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray().get(1).toInt(),
                        samikshaShikshaMitrDetailsViewBinding.smSummarySectionSpinner.selectedItem.toString().split(" ".toRegex()).toTypedArray()[1], selectedStream)

            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onEditSMClicked(studentInfo: StudentInfo) {
        val f = AddOrUpdateShikshaMitrDetails()
        val bundle = Bundle()
        bundle.putString("shiksha_mitr_student_name", studentInfo.name)
        bundle.putString("shiksha_mitr_student_srn", studentInfo.srn)
        bundle.putString("shiksha_mitr_name", studentInfo.shikshaMitrName)
        bundle.putString("shiksha_mitr_contact", studentInfo.shikshaMitrContact)
        bundle.putString("shiksha_mitr_student_relation", studentInfo.shikshaMitrRelation)
        bundle.putString("shiksha_mitr_student_address", studentInfo.shikshaMitrAddress)
        f.arguments = bundle
        addFragment(R.id.sm_fragment_container, supportFragmentManager, f, "AddOrUpdateShikshaMitrDetails")
    }
}