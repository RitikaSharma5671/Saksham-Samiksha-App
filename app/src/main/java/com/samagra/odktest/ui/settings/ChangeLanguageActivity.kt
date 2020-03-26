package com.samagra.odktest.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.widget.Toolbar
import com.samagra.ancillaryscreens.screens.splash.SplashActivity
import com.samagra.odktest.R
import com.samagra.odktest.base.BaseActivity
import com.samagra.odktest.base.NonMvpBaseActivity
import com.samagra.odktest.base.ODKTestActivity
import kotlinx.android.synthetic.main.activity_change_language.*
import org.odk.collect.android.ODKDriver
import org.odk.collect.android.activities.SplashScreenActivity
import org.odk.collect.android.preferences.GeneralKeys
import kotlin.system.exitProcess

class ChangeLanguageActivity : NonMvpBaseActivity(), ODKTestActivity {

    private var title: String? = null
    private var originalLanguage: String? = null
    private var finalLanguage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)
        if (intent != null && intent.hasExtra(GeneralKeys.TITLE))
            title = intent.getStringExtra(GeneralKeys.TITLE)
        setupToolbar()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val currentLanguage = sharedPreferences.getString("currentLanguage", "hi")
        originalLanguage = currentLanguage
        if (currentLanguage == "hi") {
            change_lang_rg.check(change_lang_rg.getChildAt(2).id)
        } else {
            change_lang_rg.check(change_lang_rg.getChildAt(1).id)
        }

        change_lang_rg.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                val checkedLang = checkedRadioButton.text.toString()
                if (checkedLang == baseContext!!.resources.getString(R.string.english)) {
                    sharedPreferences.edit().putString("currentLanguage", "en").apply()
                    finalLanguage = "en"
                } else {
                    sharedPreferences.edit().putString("currentLanguage", "hi").apply()
                    finalLanguage = "hi"
                }
                sharedPreferences.edit().putBoolean("isLanguageChanged", true).apply()
            }
        }
        update_language.setOnClickListener {
            val mStartActivity = Intent(this, SplashActivity::class.java)
            val mPendingIntentId = 123456
            val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT)
            val mgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
            exitProcess(0)
        }
        cancel_update_choice.setOnClickListener { finish() }

    }

    override fun onResume() {
        super.onResume()
        customizeToolbar()
    }

    private fun customizeToolbar() {
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use [ODKDriver]. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    override fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (originalLanguage == finalLanguage) {
            return
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity()
            } else {
                this.finish()
                System.exit(0)
            }
        }
    }

}


