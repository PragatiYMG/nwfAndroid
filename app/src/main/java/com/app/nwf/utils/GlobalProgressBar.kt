package com.app.nwf.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.nwf.R

object GlobalProgressBarUtil {
    private var progressDialog: Dialog? = null

    fun show(context: Context) {
        if (progressDialog == null) {
            progressDialog = Dialog(context)
            progressDialog?.setContentView(R.layout.global_progress_bar)
            progressDialog?.setCancelable(false)
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog?.show()
        }
    }

    fun hide() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}
