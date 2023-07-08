package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView

class VipResetDialog(val context: Context, val activity: ExchangeStarsActivity) {
    private lateinit var dialogBuilder: Dialog
    private lateinit var positiveButton: ImageView
    private lateinit var negativeButton: ImageView
    private lateinit var positiveButtonText: TextView
    private lateinit var dialogTitle: TextView
    private lateinit var dialogMessage: TextView

    fun showVipResetDialog(title: String, message: String, amount: Float, starsCost: Long) {
        dialogBuilder = Dialog(context)
        dialogBuilder.setContentView(R.layout.attention_dialog_layout)
        dialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilder.setCancelable(true)

        positiveButton = dialogBuilder.findViewById(R.id.attention_dialog_positive_button)
        positiveButtonText = dialogBuilder.findViewById(R.id.attention_dialog_positive_btn_text)
        negativeButton = dialogBuilder.findViewById(R.id.attention_dialog_negative_button)
        dialogTitle = dialogBuilder.findViewById(R.id.attention_dialog_title)
        dialogMessage = dialogBuilder.findViewById(R.id.attention_dialog_message)

        dialogTitle.text  = title
        dialogMessage.text  = message
        positiveButtonText.text = context.getString(R.string.continue_string)
        positiveButton.setOnClickListener {
            dialogBuilder.dismiss()
            activity.doWithdrawalRequest(amount, starsCost)
        }
        negativeButton.setOnClickListener {
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    fun dismiss() {
        dialogBuilder.dismiss()
    }
}