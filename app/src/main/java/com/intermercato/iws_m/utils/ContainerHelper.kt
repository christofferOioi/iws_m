package com.intermercato.iws_m.utils

import com.intermercato.iws_m.R
import com.intermercato.iws_m.mApplication
import se.oioi.intelweighblelib.helpers.Help

object ContainerHelper {

    fun isContainerActive(): String {
        var modeText: String?= null

        modeText = if (Help.isContainerEnabled(mApplication.applicationContext())) {
            mApplication.applicationContext().getString(R.string.txt_containermode)
        } else {
            mApplication.applicationContext().getString(R.string.txt_weightModeNormal)
        }
        return modeText
    }
}