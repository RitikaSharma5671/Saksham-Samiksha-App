package com.samagra.ancillaryscreens.screens.passReset

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.samagra.ancillaryscreens.R
import com.samagra.ancillaryscreens.models.OnUserFound
import com.samagra.ancillaryscreens.screens.change_password.FindUserByPhoneTask
import com.samagra.commons.CommonUtilities.isNetworkAvailable
import com.samagra.commons.SamagraAlertDialog
import com.samagra.commons.SamagraAlertDialog.CaastleAlertDialogActionListener
import com.samagra.grove.logging.Grove
import kotlinx.android.synthetic.main.aa.*
import org.odk.collect.android.listeners.ActionListener
import org.odk.collect.android.utilities.SnackbarUtils
import java.util.regex.Pattern

class EnterMobileNumberFragment : Fragment(), View.OnClickListener, OnUserFound, ChangePasswordActionListener {

    private lateinit var phoneNumber: String
    private lateinit var mProgress: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.aa, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etResult.setText("");
        btnCollect.text = requireContext().getText(R.string.send_otp)
        listOf(t9_key_clear, btnCollect,
                t9_key_7, t9_key_8,
                t9_key_9, t9_key_4,
                t9_key_5, t9_key_6,
                t9_key_1, t9_key_2,
                t9_key_3, t9_key_0,
                t9_key_backspace
        ).forEach { it.setOnClickListener(this) }

        t9_key_backspace.setOnLongClickListener {
            if (it.id == R.id.t9_key_backspace) {
                resetAmount()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }
        t9_key_clear.setOnLongClickListener {
            if (it.id == R.id.t9_key_clear) {
                resetAmount()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }
        etResult.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable?) {
                phoneNumber = etResult.text.toString()
            }
        })
        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.sending_the_request))
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true
        ttb_close_button.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.t9_key_backspace -> {
                if (etResult.text.isNotEmpty()) {
                    etResult.deleteBoldText()
                    etResult.setSelection(etResult.text.length)
                }
            }
            R.id.t9_key_clear -> {
                resetAmount()
            }
            R.id.btnCollect -> {
                if (isNetworkAvailable(requireContext())) {
                    val amount = etResult.text.toString()
                    if (amount.isNotEmpty() && !isValidPhoneNumber(amount)) {
                        mProgress.show()
                        btnCollect.isClickable = false
                        checkPhoneValidity(amount)
                        phoneNumber = amount
                    } else {
                        SamagraAlertDialog.Builder(requireContext()).setTitle(getText(R.string.invalid_phone_number)).setMessage(getText(R.string.enter_valid_number)).setAction2(getText(R.string.ok), object : CaastleAlertDialogActionListener {
                            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                                alertDialog.dismiss()
                            }
                        }).show()
                    }
                } else {
                    if (otp_parent != null) SnackbarUtils.showLongSnackbar(otp_parent, getString(R.string.internet_not_connected))

                }
            }

            R.id.t9_key_9 -> checkSpaceAndAdd(9)

            R.id.t9_key_8 -> checkSpaceAndAdd(8)

            R.id.t9_key_7 -> checkSpaceAndAdd(7)

            R.id.t9_key_6 -> checkSpaceAndAdd(6)

            R.id.t9_key_5 -> checkSpaceAndAdd(5)

            R.id.t9_key_4 -> checkSpaceAndAdd(4)

            R.id.t9_key_3 -> checkSpaceAndAdd(3)

            R.id.t9_key_2 -> checkSpaceAndAdd(2)

            R.id.t9_key_1 -> checkSpaceAndAdd(1)

            R.id.t9_key_0 -> checkSpaceAndAdd(0)

        }
    }

    private fun checkPhoneValidity(amount: String) {
        FindUserByPhoneTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, amount, requireContext().getText(R.string.fusionauth_api_key).toString())

    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val p = Pattern.compile("[6-9][0-9]{9}")
        val m = p.matcher(phoneNumber)
        return !m.find() || m.group() != phoneNumber
    }


    @SuppressLint("SetTextI18n")
    private fun checkSpaceAndAdd(value: Int) {
        if (etResult.text.isNotEmpty()) {
            var number = etResult.text.toString()
            if (number.length == 9) {
                btnCollect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.your_enabled_drawable));
            }
            if (number.length == 10) {
                return;
            } else {
                number += value;
                etResult.setText(number)
            }
        } else {
            etResult.setText(value.toString())
        }
    }


    private fun resetAmount() {
        etResult.setText("")
    }

    override fun onSuccessUserFound(numberOfUsers: String) {
        if (numberOfUsers.toInt() > 0 && phoneNumber.isNotEmpty()) {
            SendOTPTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber)
        } else {
            btnCollect.isClickable = true
            btnCollect.isEnabled = true
            mProgress.dismiss()
            SamagraAlertDialog.Builder(requireContext()).setImageTitle(ContextCompat.getDrawable(requireContext(), R.drawable.ic_browser_error)!!).setTitle(getText(R.string.no_users_found)).
            setMessage(getText(R.string.contact_admin_for_this)).setAction2(getText(R.string.ok), object : CaastleAlertDialogActionListener {
                override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                    alertDialog.dismiss()
                }
            }).show()
        }
    }

    override fun onFailureUserFound(e: Exception) {
        mProgress.dismiss()
        btnCollect.isClickable = true
        btnCollect.isEnabled = true
        SamagraAlertDialog.Builder(requireContext()).setImageView(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_warning_error)!!).
        setTitle(getText(R.string.unable_to_send_OTP)).
        setMessage(e.localizedMessage!!).setAction2(getText(R.string.ok), object : CaastleAlertDialogActionListener {
            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                alertDialog.dismiss()
            }
        }).show()
    }

    override fun onSuccess() {
        if (otp_parent != null) {
            SnackbarUtils.showShortSnackbar(otp_parent, getString(R.string.otp_sent_successfully, phoneNumber))
        }
        btnCollect.isClickable = true
        btnCollect.isEnabled = true
        mProgress.dismiss()
        val otpFragment = OTPFragment()
        val arguments = Bundle()
        arguments.putString("phoneNumber", phoneNumber)
        otpFragment.arguments = arguments
        removeFragment(this, fragmentManager)
        addFragment(R.id.login_fragment_container, fragmentManager!!, otpFragment, "OTPFragment")
    }


    private fun addFragment(containerViewId: Int, manager: FragmentManager, fragment: Fragment, fragmentTag: String) {
        try {
            val fragmentName = fragment.javaClass.name
            Grove.d("addFragment() :: Adding new fragment $fragmentName")
            // Create new fragment and transaction
            val transaction = manager.beginTransaction()
            transaction.add(containerViewId, fragment, fragmentTag)
            transaction.addToBackStack(fragmentTag)
            Handler().post(Runnable {
                try {
                    transaction.commit()
                } catch (ex: java.lang.IllegalStateException) {
                    // reportException(new IllegalStateException("Non App crash custom Exception in addFragment in " + fragmentname,ex));
                }
            })
        } catch (ex: java.lang.IllegalStateException) {
            //  reportException(new IllegalStateException("Non App crash custom Exception addFragment",ex));
        }
    }

    private fun removeFragment(fragment: Fragment?, manager: FragmentManager?) {
        if (fragment == null || manager == null) return
        try {
            val fragmentName = fragment.javaClass.name
            Grove.d("removeFragment() :: Removing current fragment $fragmentName")
            val transaction = manager.beginTransaction()
            transaction.remove(fragment)
            Handler().post {
                try {
                    transaction.commit()
                    manager.popBackStack()
                } catch (ex: IllegalStateException) {
                    //  reportException(new IllegalStateException("Non App crash custom Exception in removeFragment in " + fragmentname,ex));
                }
            }
        } catch (ex: IllegalStateException) {
//            reportException(new IllegalStateException("Non App crash custom Exception in removeFragment",ex));
        }
    }

    override fun onFailure(exception: java.lang.Exception?) {
        mProgress.dismiss()
        btnCollect.isEnabled = true
        btnCollect.isClickable = true
        SamagraAlertDialog.Builder(requireContext()).setImageView(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_warning_error)!!).
        setTitle(getText(R.string.unable_to_send_OTP)).
        setMessage(exception!!.localizedMessage!!).setAction2(getText(R.string.ok), object : CaastleAlertDialogActionListener {
            override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                alertDialog.dismiss()
            }
        }).show()
    }


}
