package com.samagra.ancillaryscreens.screens.change_password

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.samagra.ancillaryscreens.R
import com.samagra.ancillaryscreens.screens.passReset.ChangePasswordActionListener
import com.samagra.ancillaryscreens.screens.passReset.OTPFragment
import com.samagra.ancillaryscreens.screens.passReset.SendOTPTask
import com.samagra.ancillaryscreens.utils.SnackbarUtils
import com.samagra.ancillaryscreens.utils.inflate
import kotlinx.android.synthetic.main.change_password_view.*
import java.util.regex.Pattern

class ChangePasswordFragment : Fragment(), ChangePasswordActionListener {

    internal var phone: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = container?.inflate(R.layout.change_password_view)
        val title = getActivity()!!.getResources().getString(R.string.forgot_pass)
        val toolbar = rootView!!.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_navigate_back)
        toolbar.setNavigationOnClickListener {
            fragmentManager!!.popBackStack()
            activity!!.finish()
        }
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phone_submit.setOnClickListener{
            if (validate(user_phone!!.text.toString()))
                SendOTPTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user_phone!!.text.toString())
            else {
                SnackbarUtils.showLongSnackbar(parent, activity!!.resources.getString(R.string.invalid_phone_number))
            }
        }
    }
    private fun validate(phoneNumber: String): Boolean {
        val p = Pattern.compile("[6-9][0-9]{9}")
        val m = p.matcher(phoneNumber)
        return m.find() && m.group() == phoneNumber
    }

    override fun onSuccess() {
        val otpFragment = OTPFragment()
        val arguments = Bundle()
        arguments.putString("phoneNumber", user_phone!!.text.toString())
        otpFragment.arguments = arguments
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragment_container, otpFragment, "NewFragmentTag")
        ft.commit()
        ft.addToBackStack(null)
        if (parent != null) {
            SnackbarUtils.showLongSnackbar(parent!!, context!!.resources.getString(R.string.otp_successfully_sent) + user_phone!!.text.toString())
        }
    }

    override fun onFailure(exception: Exception) {
        if (parent != null) SnackbarUtils.showLongSnackbar(parent!!, exception.message!!)
    }


}



