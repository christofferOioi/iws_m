package com.intermercato.iws_m.ui.dialogfragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.intermercato.iws_m.R
import com.intermercato.iws_m.mApplication

import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.helpers.Help

/**
 * Created by fredrik on 2017-12-12.
 */
class WeighingButtonsFragment : DialogFragment(), View.OnClickListener {

    val TAG:String = "weighing"

    companion object {
        var workMode: Int? = null
        var subtraction: Boolean = false
        fun newInstance(workMode: Int?, subtraction: Boolean = false) : WeighingButtonsFragment {
            this.workMode = workMode
            this.subtraction = subtraction
            println("WEIGHINGBUTTONS ${workMode}")
            return WeighingButtonsFragment()
        }
    }
    private lateinit var listener : OnClickWeighingActions
    fun setListener(l : OnClickWeighingActions){
        listener = l
    }
    interface OnClickWeighingActions {
        fun  WeighingButtonsClicked(v : View)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView setLayout")
        val v = getLayout(container)
        if (v != null) {
            if (!Help.isContainerEnabled(activity)) {
                if (!Companion.subtraction) v.findViewById<ImageButton>(R.id.btnSubtract).setImageDrawable(resources.getDrawable(R.drawable.ic_remove_black_24dp))
                else v.findViewById<ImageButton>(R.id.btnSubtract).setImageDrawable(resources.getDrawable(R.drawable.ic_add_black_24dp))
            }
        }
        return v
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

         v.findViewById<ImageButton>(R.id.btnUndo).setOnClickListener(this)
         v.findViewById<ImageButton>(R.id.btnAction).setOnClickListener(this)
         v.findViewById<ImageButton>(R.id.btnBack).setOnClickListener(this)
         v.findViewById<ImageButton>(R.id.btnTare).setOnClickListener(this)
         v.findViewById<ImageButton>(R.id.btnSubtract).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnAction -> listener.WeighingButtonsClicked(v).also { dialog?.dismiss() }
            R.id.btnUndo -> listener.WeighingButtonsClicked(v).also { dialog?.dismiss() }
            R.id.btnTare -> listener.WeighingButtonsClicked(v).also { dialog?.dismiss() }
            R.id.btnSubtract -> listener.WeighingButtonsClicked(v).also { dialog?.dismiss() }
            R.id.btnBack -> { dialog?.dismiss()}

        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        //dialog.window?.setLayout(resources.getDimension(R.dimen.utils_button_dialog_fragment_width).toInt(),resources.getDimension(R.dimen.utils_button_dialog_fragment_height).toInt())
        //dialog.window?.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)


        /*val params = dialog.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = params as android.view.WindowManager.LayoutParams*/
    }

    private fun getLayout(container: ViewGroup?) : View? {

        Log.d(TAG, "isContainer active " + Help.isContainerEnabled(mApplication.applicationContext()))

        when (Companion.workMode) {
                Constants.WorkStates.STATIC -> return requireDialog().layoutInflater.inflate(R.layout.fragment_scale_result_semiautomatic,container,false)
                Constants.WorkStates.SEMI -> return  requireDialog().layoutInflater.inflate(R.layout.fragment_scale_result_semiautomatic,container,false)
                Constants.WorkStates.FULL -> return requireDialog().layoutInflater.inflate(R.layout.fragment_scale_result_automatic,container,false)
                //Constants.WorkStates.NORMAL -> return normalOrContainer()

     /*       Constants.WorkStates.SEMI -> {
                if (Help.isContainerEnabled(mApplication.applicationContext())) {

                    // return activity.layoutInflater.inflate(R.layout.fragment_scale_result_container,rootView,false)
                } else {
                    // return activity.layoutInflater.inflate(R.layout.fragment_scale_result_semiautomatic,rootView,false)
                }
            }*/

            else -> return null
        }

    }

}
