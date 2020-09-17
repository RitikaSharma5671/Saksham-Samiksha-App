@file:Suppress("DEPRECATION", "ImplicitThis", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.samagra.ancillaryscreens.screens.passReset

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.androidnetworking.error.ANError
import com.example.assets.uielements.SamagraAlertDialog
import com.mukesh.OnOtpCompletionListener
import com.samagra.ancillaryscreens.AncillaryScreensDriver
import com.samagra.ancillaryscreens.AncillaryScreensDriver.VERIFY_PHONE_OTP_URL
import com.samagra.ancillaryscreens.R
import com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl
import com.samagra.ancillaryscreens.data.network.UpdateUserTask
import com.samagra.ancillaryscreens.data.network.UserUpdatedListener
import com.samagra.ancillaryscreens.utils.SnackbarUtils.showSnackbar
import com.samagra.grove.logging.Grove
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.enter_mobile_number_otp.ttb_close_button
import kotlinx.android.synthetic.main.otp_view_pin.*
import org.json.JSONObject
import org.odk.collect.android.utilities.SnackbarUtils
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class OTPViewFragment : Fragment(), View.OnClickListener, OnOtpCompletionListener {

    private lateinit var phoneNumber: String
    private lateinit var mProgress: ProgressDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var resendListener: View.OnClickListener
    private lateinit var preference: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.otp_view_pin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        mProgress = ProgressDialog(requireContext())
        mProgress.setTitle(getString(R.string.sending_the_request))
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true
        preference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        ttb_close_button.setOnClickListener {
            requireActivity().finish()
        }
        if (arguments != null) {
            phoneNumber = requireArguments().getString("phoneNumber")
            //lastPage = arguments.getString("last")
        }
        validate_button.setOnClickListener(this)
        resendListener = View.OnClickListener {
            if (isNetworkConnected()) {
                SnackbarUtils.showShortSnackbar(parent_pin, requireContext().resources.getString(R.string.internet_not_connected))
            } else {
                showProgressBar()
                SendOTPTask(object : ChangePasswordActionListener {
                    override fun onSuccess() {
                        hideProgressBar()
                        startTimer()
                        SnackbarUtils.showLongSnackbar(parent_pin, getString(R.string.otp_sent_change_pwd))
                        validate_button.text = activity!!.resources.getString(R.string.validate)
                        validate_button.setOnClickListener(this@OTPViewFragment)
                    }

                    override fun onFailure(exception: Exception) {
                        SnackbarUtils.showLongSnackbar(parent_pin, requireContext().resources.getString(R.string.error_sending_otp))
                    }
                }, VERIFY_PHONE_OTP_URL).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber)
            }
        }
        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (countdown_timer != null)
                    countdown_timer.text = getString(R.string.seconds_remaining) + millisUntilFinished / 1000
            }

            override fun onFinish() {
                validate_button.setText(R.string.resend_otp_password)
                validate_button.setOnClickListener(resendListener)
            }
        }
        countDownTimer.start()
    }


    private fun showProgressBar() {
        parent_pin.isClickable = false
        mProgress.show()
    }

    private fun hideProgressBar() {
        parent_pin.isClickable = true
        mProgress.dismiss()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.validate_button) {
            if (otp_view_text.text!!.length < 4) {
                Toast.makeText(requireContext(), "Please enter OTP before sending for Verification.", Toast.LENGTH_LONG).show()
            } else {
                val imm: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = requireActivity().currentFocus
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                mProgress.show()
                CompositeDisposable().add(BackendCallHelperImpl.getInstance()
                        .performVerifyPhoneNumberCall(phoneNumber, otp_view_text.text!!.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ loginResponse: JSONObject ->
                            if (loginResponse != null) {
                                mProgress.setTitle("Updating Contact Number")
                                Grove.d("Received successful login response for the user $loginResponse")
                                updatePhoneNumber()
                            }
                        }, { throwable: Throwable ->
                            run {
                                mProgress.dismiss()
                                if (throwable is ANError) {
                                    if (throwable.errorCode == 400 && throwable.errorBody.contains("This OTP is incorrect")) {
                                        Toast.makeText(requireContext(), "Please enter a valid OTP.", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Unable to verify the phone number. Please try again later.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Unable to verify the phone number. Please try again later.", Toast.LENGTH_LONG).show()
                                }
                            }
                            Grove.e("Error while verifying OTP to update Contact Number: $throwable")
                        }))
            }
        }
    }

    private fun updatePhoneNumber() {
        //        Single<JSONObject> usersForEmail = getApiHelper().performSearchUserByEmailCall(updatedEmail, apiKey);
        val updatedData: Single<JSONObject> = BackendCallHelperImpl.getInstance()
                .performGetUserDetailsApiCall(preference.getString("user.id", ""), AncillaryScreensDriver.API_KEY )
                .flatMap(Function<JSONObject, SingleSource<out JSONObject>> { oldData: JSONObject ->
                    // Update the data with new fields
                    Grove.d("Sending request to update the profile data")
                    val user = oldData.getJSONObject("user")
                    val internalData: JSONObject
                    internalData = if (user.has("data") && user.getJSONObject("data") != null) {
                        user.getJSONObject("data")
                    } else {
                        JSONObject()
                    }
                        user.put("mobilePhone", phoneNumber)
                    internalData.put("phone", phoneNumber)
                    user.put("data", internalData)
                    oldData.put("user", user)
                    BackendCallHelperImpl.getInstance().performPutUserDetailsApiCall(preference.
                    getString("user.id", ""), AncillaryScreensDriver.API_KEY , oldData)
                })
        updateUserProfile(updatedData);
    }

    private fun updateUserProfile( updatedData: Single<JSONObject>) {
        CompositeDisposable().add(updatedData.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ newData: JSONObject? ->
                    mProgress.dismiss()
                    Grove.d("Success API. Response %s", newData)
                    showSnackbar(parent_pin, requireContext().resources.getString(R.string.successful_update), 5000)
                    preference.edit().putString("user.mobilePhone", phoneNumber).apply()
                    SamagraAlertDialog.Builder(requireContext()).setTitle("SUCCESS").setMessage("You contact number has been successfully updated. Please press OK to go back to Home Screen.")
                            .setAction2("Ok", object : SamagraAlertDialog.CaastleAlertDialogActionListener {
                                override fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog) {
                                    alertDialog.dismiss()
                                    requireActivity().finish()
                                }
                            }).show()
                }) { t: Throwable? ->
                    Grove.e(t)
                    mProgress.dismiss()
                    showSnackbar(parent_pin,"Failed to update user profile.", 3000)
                })
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo == null || !networkInfo.isConnected
    }

    private fun setListeners() {
        validate_button.setOnClickListener(this)
        otp_view_text.setOtpCompletionListener(this)
    }

    override fun onOtpCompleted(otp: String?) {
        Grove.d("OnOtpCompletionListener called");
        val imm: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onStop() {
        countDownTimer.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }
}
