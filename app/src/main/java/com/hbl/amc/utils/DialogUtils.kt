package com.hbl.amc.utils

import android.R.attr
import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import com.hbl.amc.R
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import android.R.attr.label

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Typeface
import android.view.View.INVISIBLE
import android.widget.*
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import java.util.*
import kotlin.concurrent.schedule


class DialogUtils {

    companion object {

        fun showDialog(activity: Context, onClick: () -> Unit) {
            //val dialog = AppCompatDialog(activity, R.style.Theme_Dialog)
            val dialog = AppCompatDialog(activity, R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            //dialog.window!!.setBackgroundDrawableResource( android.R.color.background_light);
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.otp_dialog)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent_colour)
            val timerView = dialog.findViewById<TextView>(R.id.timer_tv)
            val verifyBtn = dialog.findViewById<AppCompatButton>(R.id.otp_verify_btn)

            object : CountDownTimer(120000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val remainedSecs = millisUntilFinished / 1000
                    timerView!!.text = "" + (remainedSecs / 60) + ":" + (remainedSecs % 60)

                }

                override fun onFinish() {
                    // timerView!!.setText("try again")
                }
            }.start()

            verifyBtn?.setOnClickListener {
                dialog.dismiss()
                onClick.invoke()
            }

            val closeButton = dialog.findViewById<ImageView>(R.id.close_btn) as ImageView

            closeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun showAlertDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            title: String,
            message: String,
            icon: Int,
            okBtnTitle: String,
            onOkPressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.dialog_alert, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val alertTitle = v.findViewById<TextView>(R.id.title_tv)
            val alertMessage = v.findViewById<TextView>(R.id.detail_msg_tv)
            val alertIcon = v.findViewById<ImageView>(R.id.icon)
            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            val verifyBtn = v.findViewById<Button>(R.id.verify_btn)
            alertTitle.text = title
            alertMessage.text = message
            alertIcon.setImageResource(icon)
            verifyBtn.text = okBtnTitle

            val dg = dialog.show()

            verifyBtn.setOnClickListener {
                dg.dismiss()
                onOkPressed?.invoke()
            }

            closeBtn.setOnClickListener {
                dg.dismiss()
            }
        }

        fun showConfirmationDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            title: String,
            message: String,
            icon: Int,
            onOkPressed: (() -> Unit)? = null,
            onCancelPressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.dialog_confirmation_alert, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val alertTitle = v.findViewById<TextView>(R.id.title_tv)
            val alertMessage = v.findViewById<TextView>(R.id.detail_msg_tv)
            val alertIcon = v.findViewById<ImageView>(R.id.icon)
            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            val verifyBtn = v.findViewById<Button>(R.id.verify_btn)
            val cancelBtn = v.findViewById<Button>(R.id.cancel_btn)
            alertTitle.text = title
            alertMessage.text = message
            alertIcon.setImageResource(icon)

            val dg = dialog.show()

            verifyBtn.setOnClickListener {
                dg.dismiss()
                onOkPressed?.invoke()
            }

            cancelBtn.setOnClickListener {
                dg.dismiss()
                onCancelPressed?.invoke()
            }

            closeBtn.setOnClickListener {
                dg.dismiss()
            }
        }

        fun showVideoCallDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            onInitiatePressed: (() -> Unit)? = null,
            onSchedulePressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.dialog_video_call, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val initiateBtn = v.findViewById<Button>(R.id.initiate_btn)
            val scheduleBtn = v.findViewById<Button>(R.id.schedule_btn)

            val dg = dialog.show()

            initiateBtn?.setOnClickListener {
                dg.dismiss()
                onInitiatePressed?.invoke()
            }

            scheduleBtn?.setOnClickListener {
                dg.dismiss()
                onSchedulePressed?.invoke()
            }

        }

        fun showBillNumberGeneratorDialog(
            activity: Context,
            title: String,
            msg: String,
            billNumber: Long,
            btnText: String,
            onInvestClicked: (() -> Unit)? = null
        ) {
            val dialog = AppCompatDialog(activity, R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.bill_number_generator_dialog)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent_colour)

            val screenshotBtn = dialog.findViewById<TextView>(R.id.screenshot_btn)
            val titleTv = dialog.findViewById<TextView>(R.id.title_tv)
            val msgTv = dialog.findViewById<TextView>(R.id.detail_msg_tv)
            val billNumberTv = dialog.findViewById<TextView>(R.id.bill_number_tv)
            val billNumberCopyIcon = dialog.findViewById<TextView>(R.id.bill_number_tv)
            val investBtn = dialog.findViewById<TextView>(R.id.invest_btn)

            screenshotBtn?.text = btnText
            titleTv?.text = title
            msgTv?.text = msg
            billNumberTv?.text = billNumber.toString()

            screenshotBtn?.setOnClickListener {
                AppUtils.getScreenShotFromView(dialog.window!!.decorView.rootView)?.let { it1 ->
                    AppUtils.saveToGallery(
                        activity, it1, "hbl"
                    )
                }
//                dialog.dismiss()
            }

            investBtn?.setOnClickListener {
                onInvestClicked?.invoke()
                dialog.dismiss()
            }


            val closeButton = dialog.findViewById<ImageView>(R.id.close_btn) as ImageView

            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            billNumberCopyIcon?.setOnClickListener {
                val clipboard: ClipboardManager? =
                    activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("Bill Number", billNumber.toString())
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(activity, "Bill Number Copied to Clipboard!", Toast.LENGTH_LONG)
                        .show()
                }
            }

            dialog.show()
        }

        fun showDisclaimerDialog(
            activity: Context,
            msg: String,
            onAcceptChecked: (() -> Unit)? = null
        ) {
            val dialog = AppCompatDialog(activity, R.style.Theme_HBL_AMC)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent_colour)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_disclaimer)


            dialog.findViewById<TextView>(R.id.detail_msg_tv)!!.text = msg
            val checkBox = dialog.findViewById<CheckBox>(R.id.disclaimer_dialog_checkbox)

            checkBox?.setOnClickListener {
                if (checkBox.isChecked) {

                    onAcceptChecked?.invoke()
                    Timer("SettingUp", false).schedule(1000) {
                        dialog.dismiss()
                    }
                }

            }
            dialog.show()
        }


        var progressDialog: AppCompatDialog? = null
        var dialogContext: Context? = null

        fun createPogressDialog(context: Context) {
            //val dialog = AppCompatDialog(activity, R.style.Theme_Dialog)
            dialogContext = context
            progressDialog = AppCompatDialog(dialogContext, R.style.Theme_HBL_AMC)
            progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            progressDialog?.window!!.setBackgroundDrawableResource(R.color.transparent_colour)
            //dialog.window!!.setBackgroundDrawableResource( android.R.color.background_light);
            progressDialog?.setCancelable(false)
            progressDialog?.setContentView(R.layout.progress_dialog)
        }

        fun showLoading() {
            if (!(dialogContext as AppCompatActivity).isFinishing) {
                progressDialog?.let {
                    it.show()
                }
            }
        }

        fun hideLoading() {
            if (!(dialogContext as AppCompatActivity).isFinishing) {
                progressDialog?.let {
                    it.cancel()
                }
            }
        }

        fun showInfoDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            onOkPressed: (() -> Unit)? = null,
            onCancelPressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.info_dialog, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            val investLater = v.findViewById<Button>(R.id.invest_later_btn)
            val tryAgain = v.findViewById<Button>(R.id.try_again_btn)

            val dg = dialog.show()

            tryAgain.setOnClickListener {
                dg.dismiss()
                onOkPressed?.invoke()
            }

            investLater.setOnClickListener {
                dg.dismiss()
                onCancelPressed?.invoke()
            }

            closeBtn.setOnClickListener {
                dg.dismiss()
            }
        }

        fun showAccountTypeChangeDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            title: String,
            message: String,
            icon: Int,
            btn1Text: String,
            btn2Text: String,
            onOkPressed: (() -> Unit)? = null,
            onCancelPressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.change_account_type_dialog, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val btn1 = v.findViewById<Button>(R.id.verify_btn)
            val btn2 = v.findViewById<Button>(R.id.cancel_btn)
            val alertTitle = v.findViewById<TextView>(R.id.title_tv)
            val alertMessage = v.findViewById<TextView>(R.id.detail_msg_tv)
            val alertIcon = v.findViewById<ImageView>(R.id.icon)
            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            closeBtn.visibility = INVISIBLE
            alertTitle.text = title
            alertTitle.setTypeface(null, Typeface.BOLD)
            alertMessage.text = message
            alertIcon.setImageResource(icon)

            btn1.text = btn1Text
            btn2.text = btn2Text

            btn1.isAllCaps = false
            btn1.setTypeface(null, Typeface.BOLD)

            btn2.isAllCaps = false
            btn2.setTypeface(null, Typeface.BOLD)

            val dg = dialog.show()

            btn1.setOnClickListener {
                dg.dismiss()
                onOkPressed?.invoke()
            }

            btn2.setOnClickListener {
                dg.dismiss()
                onCancelPressed?.invoke()
            }
        }

        fun showProfileBasedDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            title: String,
            message: String,
            okBtnTitle: String,
            onOkPressed: (() -> Unit)? = null
        ) {
            val dialog: AlertDialog.Builder =
                AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.profile_based_alert, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val alertTitle = v.findViewById<TextView>(R.id.title)
            val alertMessage = v.findViewById<TextView>(R.id.desc)
            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            val verifyBtn = v.findViewById<Button>(R.id.proceed)
            alertTitle.text = title
            alertMessage.text = message
            verifyBtn.text = okBtnTitle

            val dg = dialog.show()

            verifyBtn.setOnClickListener {
                dg.dismiss()
                onOkPressed?.invoke()
            }

            closeBtn.setOnClickListener {
                dg.dismiss()
            }
        }

        fun showInfoPopup(
            context: Context,
            layoutInflater: LayoutInflater,
            accountType : String
        ) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)

            val v = layoutInflater.inflate(R.layout.info_popup, null)
            dialog.setView(v)
            dialog.setCancelable(false)
            dialog.create()

            val closeBtn = v.findViewById<ImageView>(R.id.close_btn)
            val min1 = v.findViewById<TextView>(R.id.min_1)
            val min2 = v.findViewById<TextView>(R.id.min_2)
            val min3 = v.findViewById<TextView>(R.id.min_3)

            if (accountType == "01-KS") {
                min1.text = "Identification Document"
                min2.text = "Proof of Income"
//                min3.text = "Other Documents for FATCA/CRS"
                min3.visibility = View.GONE
            } else if (accountType == "01-BB") {
                min1.text = "Identification Document"
                min2.visibility = View.GONE
                min3.visibility = View.GONE
            }

            val dg = dialog.show()

            closeBtn.setOnClickListener {
                dg.dismiss()
            }
        }
    }
}