@file:Suppress("DEPRECATION")

package com.star.games.android

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

object SuccessToastUtils {

    fun showCustomSuccessToast(context: Context, message: String, duration: Int, window: Window) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, window.decorView.findViewById(android.R.id.content), false) as ConstraintLayout
        val toastBackground: ImageView = layout.findViewById(R.id.toastBackground)
        val toastText: TextView = layout.findViewById(R.id.toastText)

        toastBackground.setImageResource(R.drawable.toast_success_background)
        val textColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(R.color.green_color_to_success_toast_txt)
        } else {
            ContextCompat.getColor(context, R.color.green_color_to_success_toast_txt)
        }
        toastText.text = message
        toastText.setTextColor(textColor)

        val toast = Toast(context)
        toast.duration = duration
        toast.view = layout
        toast.show()
    }

}