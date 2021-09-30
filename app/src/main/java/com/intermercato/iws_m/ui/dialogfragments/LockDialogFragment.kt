package se.oioi.blestandardapp.ui.dialogfragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.TextWatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.intermercato.iws_m.R

class LockDialogFragment : DialogFragment() {

    private var content: String? = null
    private var lockcode:Int? = 0
    private var codeIsActive: Boolean = false
    private var message : TextView? = null
    private var inputTextForLock : EditText? = null

    private val TAG: String = "LockDialog"
    interface LockListener {
        fun lockClicked(code: Int?)
    }

    public var listener: LockListener? = null

    fun setLockDialogFragmentListener(l: LockListener) {
        listener = l
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString(CONST)
        lockcode = arguments?.getInt(CODE)
        Log.d(TAG, "content $content")
        // Pick a style based on the num.
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    fun isCodeActive(str: String?) {
        if (str.equals("ACTIVE")) {
            //inputTextForLock.transformationMethod = PasswordTransformationMethod.getInstance()
            //Editable.Factory.getInstance().newEditable("unlock with your code")
            message?.text = "enter code to unlock"
            codeIsActive = true
        } else {
            message?.text = "enter code to activate lock"
            codeIsActive = false
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.dialog_activate_lock_layout, container, false)

        inputTextForLock = view.findViewById(R.id.inputTextForLock)
        message = view.findViewById(R.id.message)

        view.findViewById<Button>(R.id.btnDialogOk).setOnClickListener {
            var s: String? = inputTextForLock?.text.toString()

            if (s == null || TextUtils.isEmpty(s)) {


            } else {

                if(codeIsActive){

                }else{

                }
                Log.d(TAG, "code value $s")
                listener?.lockClicked(Integer.valueOf(s))
            }

            dismiss()

        }

        view.findViewById<Button>(R.id.btnDialogCancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<EditText>(R.id.inputTextForLock).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterText " + p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "beforeTextChanged ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "onTextChange " + p0.toString())
            }
        })



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCodeActive(content)
    }

    companion object {

        val CONST: String = "CONTENT"
        val CODE : String = "CODE"
        fun newInstance(content: String, code: Int): LockDialogFragment {
            val f = LockDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString(CONST, content)
            args.putInt(CODE, code)
            f.arguments = args

            return f
        }
    }
}