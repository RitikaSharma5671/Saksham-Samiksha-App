package com.example.student_details.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.student_details.R
import kotlinx.android.synthetic.main.dialog_layout_1.view.*


/**
 * Created by Umang Bhola on 26/5/20.
 * Samagra- Transforming Governance
 */
class SamagraAlertDialog1 {
    private var alertDialog: AlertDialog? = null

    fun dismiss() {
        alertDialog?.dismiss()
    }


    class Builder(val context: Context) {
        private val alertDialogLayout: View = LayoutInflater.from(context).inflate(R.layout.dialog_layout_1, null)
        private var enableTouchOutside = false
        private lateinit var samagraAlertDialog1: SamagraAlertDialog1


        fun setTitle(title: CharSequence): Builder {
            alertDialogLayout.tv_title.text = title
            alertDialogLayout.tv_title.visibility = View.VISIBLE
            return this
        }

        fun setImageView(imageView: Drawable): Builder {
            alertDialogLayout.alert_dialog_image.setImageDrawable(imageView)
            alertDialogLayout.alert_dialog_image.visibility = View.VISIBLE
            return this
        }

        fun setMessage(message: CharSequence): Builder {
            alertDialogLayout.tv_message.text = message
            alertDialogLayout.tv_message.visibility = View.VISIBLE
            return this
        }

        fun setTitle(title: Int): Builder {
            return setTitle(context.getString(title))
        }

        fun setImageTitle(imageId: Drawable):  Builder {
            return setImageView(imageId)
        }

        fun setMessage(message: Int): Builder {
            return setMessage(context.getString(message))
        }

        fun setStringMessage(message: String): Builder {
            return setMessage(message)
        }

        fun setAction1(buttonTextColor: Int, buttonBackgroundColor: Int, action: CharSequence, listener: CaastleAlertDialogActionListener1?): Builder {
            alertDialogLayout.btn_action1.text = action
            alertDialogLayout.btn_action1.visibility = View.VISIBLE
            alertDialogLayout.btn_action1.setBackgroundColor(buttonBackgroundColor)
            alertDialogLayout.btn_action1.setTextColor(buttonTextColor)
            alertDialogLayout.btn_action1.setOnClickListener {
                listener?.onActionButtonClicked(1, samagraAlertDialog1)
            }
            return this
        }

        fun setAction2(action: CharSequence, listener: CaastleAlertDialogActionListener1?): Builder {
            alertDialogLayout.btn_action2.text = action
            alertDialogLayout.btn_action2.visibility = View.VISIBLE
            alertDialogLayout.btn_action2.setOnClickListener {
                listener?.onActionButtonClicked(1, samagraAlertDialog1)
            }
            return this
        }

        fun setAction2(action: CharSequence, listener: CaastleAlertDialogActionListener1?, drawable: Drawable, colorId :Int): Builder {
            alertDialogLayout.btn_action2.text = action
            alertDialogLayout.btn_action2.visibility = View.VISIBLE
            alertDialogLayout.btn_action2.background = drawable
            alertDialogLayout.btn_action2.setTextColor(colorId)
            alertDialogLayout.btn_action2.setOnClickListener {
                listener?.onActionButtonClicked(1, samagraAlertDialog1)
            }
            return this
        }

        fun setAction3(action: CharSequence, listener:  CaastleAlertDialogActionListener1?): Builder {
            alertDialogLayout.btn_action3.text = action
            alertDialogLayout.btn_action3.text = action
            alertDialogLayout.btn_action3.visibility = View.VISIBLE
            alertDialogLayout.btn_action3.setOnClickListener {
                listener?.onActionButtonClicked(1, samagraAlertDialog1)
            }
            return this
        }

        fun setAction1(action: Int, listener: CaastleAlertDialogActionListener1?): Builder {
            return setAction1(R.color.white, R.color.color_primary, context.getString(action), listener)
        }

        fun setAction2(action: Int, listener: CaastleAlertDialogActionListener1?): Builder {
            return setAction2(context.getString(action), listener)
        }

        fun setAction3(action: Int, listener: CaastleAlertDialogActionListener1?): Builder {
            return setAction3(context.getString(action), listener)
        }

        fun dismissOnTouchOutside(): Builder {
            enableTouchOutside = true
            return this
        }

        fun show(): SamagraAlertDialog1 {
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context).setView(alertDialogLayout)
                    .setCancelable(enableTouchOutside).show()
            alertDialog.setCanceledOnTouchOutside(false)
            samagraAlertDialog1 = SamagraAlertDialog1()
            samagraAlertDialog1.alertDialog = alertDialog
            return samagraAlertDialog1
        }

    }

    interface CaastleAlertDialogActionListener1 {
        fun onActionButtonClicked(actionIndex: Int, alertDialog: SamagraAlertDialog1)
    }
}