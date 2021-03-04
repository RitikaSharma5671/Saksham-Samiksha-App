package com.example.student_details.ui.shikshamitr

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.example.assets.uielements.SamagraAlertDialog
import com.example.student_details.R
import com.example.student_details.R.string.incorrect_phone_warning
import com.example.student_details.getViewModelProvider
import com.example.student_details.ui.SamagraAlertDialog1
import kotlinx.android.synthetic.main.samiksha_register_shiksha_mitr_screen.*
import java.util.regex.Pattern


class AddOrUpdateShikshaMitrDetails : Fragment() {

    private var finalDBRelation: String = ""
    private var isEditing: Boolean = false
    private lateinit var studentName: String
    private lateinit var studentSRN: String
    private lateinit var shikshaMitrAddress: String
    private lateinit var shikshaMitrName: String
    private lateinit var previousShikshaMitrName: String
    private lateinit var shikshaMitrRelation: String
    private lateinit var otherText: String
    private lateinit var shikshaMitrContactNumber: String
    private lateinit var previousShikshaMitrContactNumber: String

    private val viedwModel: AddOrUpdateShikshaMitrDetailsViewModel by lazy {
        getViewModelProvider(this
        ).get(
                AddOrUpdateShikshaMitrDetailsViewModel::class.java
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.samiksha_register_shiksha_mitr_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentName = requireArguments().getString("shiksha_mitr_student_name")!!
        studentSRN = requireArguments().getString("shiksha_mitr_student_srn")!!
        shikshaMitrContactNumber = requireArguments().getString("shiksha_mitr_contact", "")
        shikshaMitrName = requireArguments().getString("shiksha_mitr_name", "")
        previousShikshaMitrContactNumber = requireArguments().getString("shiksha_mitr_contact", "")
        previousShikshaMitrName = requireArguments().getString("shiksha_mitr_name", "")
        shikshaMitrRelation = requireArguments().getString("shiksha_mitr_student_relation", "")
        shikshaMitrAddress = requireArguments().getString("shiksha_mitr_student_address", "")
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item_sm_relation, requireContext().resources.getStringArray(R.array.relation_list))
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_sm_relation)
        shiksha_mitr_relation.setAdapter(spinnerAdapter)
        shiksha_mitr_student_name.setText(studentName)
        shiksha_mitr_student_srn.setText(studentSRN)
        if (shikshaMitrName.isNotEmpty() && shikshaMitrName != "-") {
            shiksha_mitr_name.setText(shikshaMitrName)
            isEditing = true
        } else {
            shiksha_mitr_name.setText("")
        }
        if (shikshaMitrContactNumber.isNotEmpty() && shikshaMitrContactNumber != "-") {
            shiksha_mitr_contact.setText(shikshaMitrContactNumber)
        } else {
            shiksha_mitr_contact.setText("")
        }
        if (shikshaMitrAddress.isNotEmpty() && shikshaMitrAddress != "-") {
            shiksha_mitr_address.setText(shikshaMitrAddress)
        } else {
            shiksha_mitr_address.setText("")
        }
        val validCategoryValues = requireContext().resources.getStringArray(R.array.relation_list)
        if(shikshaMitrRelation.contains("Others::")){
            finalDBRelation = shikshaMitrRelation
            shikshaMitrRelation = "Others"
        }
        if (shikshaMitrRelation.isNotEmpty()) {
            for (j in validCategoryValues.indices) {
                if (validCategoryValues[j] == shikshaMitrRelation) shiksha_mitr_relation.setSelection(j)
            }
        }

        if(shikshaMitrRelation =="Others"){
            otherText = finalDBRelation.split("::")[1]
            shiksha_mitr_other.setText(otherText)
            shiksha_mitr_other_layout.visibility = View.VISIBLE
        }else{
            otherText = ""
            shiksha_mitr_other_layout.visibility = View.GONE
        }
        shiksha_mitr_close_cross_icon.setOnClickListener {
            val inputMethodManager: InputMethodManager? = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            parentFragmentManager.popBackStack()
        }

        shiksha_mitr_relation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (shiksha_mitr_relation.selectedItem == "Others") {
                    shiksha_mitr_other_layout.visibility = View.VISIBLE
                } else {
                    shiksha_mitr_other_layout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        viedwModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if (it == "true") {
                sm_update_screen_parent_layout.isEnabled = true
                sm_update_screen_parent_layout.isClickable = true
                sm_update_screen_loader.visibility = View.GONE
                showAlertDialogForIncompleteInputs(resources.getString(R.string.failed_to_add_update_sm_details))
            } else if (it == "false") {
                dd()
                SamagraAlertDialog1.Builder(requireContext()).setTitle("DATA SUBMITTED SUCCESSFULLY").setMessage("The Shiksha Mitr data has been successfully updated/added.\n Click on the button to go back to Shiksha Mitr Details Screen.")
                        .setAction2("Yes, Please", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean("ssss", true).apply()
                                alertDialog.dismiss()
                                val inputMethodManager: InputMethodManager? = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                                inputMethodManager?.hideSoftInputFromWindow(view.applicationWindowToken, 0)
                                requireActivity().onBackPressed()
                            }

                        }).show()
            }else{
                    sm_update_screen_parent_layout.isEnabled = true
                    sm_update_screen_parent_layout.isClickable = true
                    sm_update_screen_loader.visibility = View.GONE
            }
        })

        viedwModel.showCompleteDialog.observe(viewLifecycleOwner, Observer {
            if (it) {
                val message = if (isEditing) "Please ensure that you have updated all fields of the Shiksha Mitr"
                else "Please ensure that you have entered correct data for the Shiksha Mitr."
                SamagraAlertDialog1.Builder(requireContext())
                        .setTitle("UPDATE DATA")
                        .setMessage(message)
                        .setAction2("Proceed Ahead", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                alertDialog.dismiss()
                                sm_update_screen_parent_layout.isEnabled = false
                                sm_update_screen_parent_layout.isClickable = false
                                sm_update_screen_loader.visibility = View.VISIBLE
                                viedwModel.eeded(studentSRN, shikshaMitrName, shikshaMitrContactNumber, finalDBRelation, shikshaMitrAddress,
                                        PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("token","")!!,
                                        PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("user.username","")!!,
                                        PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("user.schoolCode","")!!,
                                        PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("user.designation","")!!,
                                        previousShikshaMitrName, previousShikshaMitrContactNumber)
                            }

                        }).setAction3("Cancel, Want to Recheck", object : SamagraAlertDialog1.CaastleAlertDialogActionListener1 {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1) {
                                alertDialog.dismiss()
                            }

                        }).show()
            }
        })
        shiksha_mitr_save_details.setOnClickListener {
            if (isValidInputForEditing()) {
                val inputMethodManager: InputMethodManager? = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                inputMethodManager?.hideSoftInputFromWindow(view.applicationWindowToken, 0)
                shikshaMitrName = shiksha_mitr_name.text.toString()
                shikshaMitrContactNumber = shiksha_mitr_contact.text.toString()
                shikshaMitrRelation = shiksha_mitr_relation.selectedItem.toString()

                finalDBRelation = if (shikshaMitrRelation == "Others")
                    shikshaMitrRelation + "::" + shiksha_mitr_other.text.toString()
                else
                    shikshaMitrRelation
                shikshaMitrAddress = if (shiksha_mitr_address.text == null || shiksha_mitr_address.text.toString() == "") "-" else shiksha_mitr_address.text.toString()
                viedwModel.onClicked()
            }
        }
    }

    private fun dd() {
        sm_update_screen_parent_layout.isEnabled = true
        sm_update_screen_parent_layout.isClickable = true
        sm_update_screen_loader.visibility = View.GONE
    }

    private fun showAlertDialogForIncompleteInputs(charSequence: CharSequence) {
        SamagraAlertDialog.Builder(requireContext()).setTitle(resources.getString(R.string.could_add_student))
                .setMessage(charSequence)
                .setAction2(getString(R.string.got_it), object : SamagraAlertDialog.CaastleAlertDialogActionListener {
                    override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                        alertDialog.dismiss()
                    }
                }).show()
    }

    private fun isValidInputForEditing(): Boolean {

        if (shiksha_mitr_name.text == null || shiksha_mitr_name.text.toString() == "" || shiksha_mitr_name.text.toString() == "-") {
            showAlertDialogForIncompleteInputs(resources.getString(R.string.no_name_warning))
            return false
        }

        if (validatePhoneNumber(shiksha_mitr_contact.text.toString())) {
            showAlertDialogForIncompleteInputs(resources.getString(incorrect_phone_warning))
            return false
        }

        if (shiksha_mitr_relation.selectedItemPosition == 0) {
            showAlertDialogForIncompleteInputs(resources.getString(R.string.select_sm_relation))
            return false
        }

        if (shiksha_mitr_relation.selectedItem == "Others" && (shiksha_mitr_other.text == null || shiksha_mitr_other.text!!.isEmpty())) {
            return false
        }

        return true

    }


    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        val p = Pattern.compile("[6-9][0-9]{9}")
        val m = p.matcher(phoneNumber)
        return !m.find() || m.group() != phoneNumber
    }

}