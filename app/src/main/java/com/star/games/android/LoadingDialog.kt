package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ProgressBar

class LoadingDialog(var context: Context) {
    private lateinit var dialog : Dialog
    var pgb: ProgressBar? = null
    fun show() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_loading)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pgb = dialog.findViewById(R.id.progressBar)
        pgb!!.isIndeterminate = true

        dialog.show()
    }

    fun hide() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}