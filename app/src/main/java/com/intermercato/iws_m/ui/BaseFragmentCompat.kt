package com.intermercato.iws_m.ui

import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat

import com.intermercato.iws_m.R
import com.intermercato.iws_m.repositories.PrefsRepo
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import se.oioi.intelweighblelib.events.other.DialogBusEvent
import se.oioi.intelweighblelib.events.other.ErrorEvent

public abstract class BaseFragmentCompat : PreferenceFragmentCompat() {

    private var dialog: ProgressDialog? = null
    private var isWeightInKg: Boolean = false
    private val TAG = "prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate")

    }

    override
    fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        isWeightInKg = PrefsRepo.isWeightInKg()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (dialog != null) {
            //EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.HIDE, ""))
            dialog?.dismiss()
            dialog = null
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DialogBusEvent) {

        Log.d("DialogBusEvent", "event " + event.eventType)
        val eventType = event.eventType

        if (eventType == DialogBusEvent.SHOW) {

            val eventMessage = event.eventMessage
            /*
                Check if dialog requested is the same as current
                else kill dialog and create buttonAdd new one
                 */
            if (dialog != null) {
                if (dialog!!.isIndeterminate() && event.eventStyle == ProgressDialog.STYLE_SPINNER) {

                } else if (!dialog?.isIndeterminate()!! && event.eventStyle == ProgressDialog.STYLE_HORIZONTAL) {

                } else {
                    dialog?.dismiss()

                }
            }

            if (dialog == null) {
                dialog = ProgressDialog( requireContext() )
            }
            dialog?.hide()
            dialog?.setProgressStyle(event.eventStyle)
            if (event.max > 0)
                dialog?.setMax(event.max)
            if (event.progress != 0)
                dialog?.setProgress(event.progress)

            dialog?.setIndeterminate(event.isIndeterminate)
            dialog?.setOnCancelListener { dialog -> dialog.dismiss() }

            dialog?.setMessage(event.eventMessage)

            dialog?.show()
        }
        if (eventType == DialogBusEvent.HIDE) {
            if (dialog != null) {
                dialog?.hide()
            }
        }
    }


    @Subscribe
    fun onEvent(event: ErrorEvent) {
        displayErrorDialog(event.title, event.message, event.icon)
    }

    private fun displayErrorDialog(t: String, m: String, icon: Drawable) {
     runOnUiThread {
            val dialogBuilder: AlertDialog.Builder
            dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setNeutralButton(getString(R.string.txt_positive_button_title_ok)) { dialog, which -> dialog.dismiss() }
            dialogBuilder.setTitle(t)
            dialogBuilder.setMessage(m)
            dialogBuilder.setIcon(icon)
            dialogBuilder.show()
        }
    }


    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}