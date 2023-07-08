package com.star.games.android

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import java.util.concurrent.TimeUnit

class CooldownDialog (val context: Context, private val areInExchangeActivity: Boolean) {
    private lateinit var cooldownDialogBuilder: Dialog
    private lateinit var dialogTitle: TextView
    private lateinit var dialogMessage: TextView
    private lateinit var dialogCloseButton: ImageView
    private lateinit var coolDownTimer: CountDownTimer

    fun showCooldownDialog(cooldownRemaining: Long) {
        cooldownDialogBuilder = Dialog(context)
        cooldownDialogBuilder.setContentView(R.layout.cooldown_dialog_layout)
        cooldownDialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cooldownDialogBuilder.setCancelable(false)

        dialogTitle = cooldownDialogBuilder.findViewById(R.id.cooldown_dialog_title)
        dialogMessage = cooldownDialogBuilder.findViewById(R.id.cooldown_dialog_message)
        dialogCloseButton = cooldownDialogBuilder.findViewById(R.id.cooldown_dialog_save_button)

        dialogTitle.text = context.getString(R.string.on_cooldown)
        dialogCloseButton.setOnClickListener {
            if (areInExchangeActivity) {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
                cooldownDialogBuilder.dismiss()
                if (context is Activity) {
                    context.finish()
                }
            } else {
                cooldownDialogBuilder.dismiss()
            }

        }

        coolDownTimer = object : CountDownTimer(cooldownRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                val timeRemainingA = StringBuilder()

                if (days >= 1) {
                    timeRemainingA.append(String.format("%02dD:", days))
                }

                if (hours >= 1) {
                    timeRemainingA.append(String.format("%02dH:", hours))
                }

                if (minutes >= 1) {
                    timeRemainingA.append(String.format("%02dM:", minutes))
                }

                if (seconds >= 1) {
                    timeRemainingA.append(String.format("%02dS", seconds))
                } else {
                    cooldownDialogBuilder.dismiss()
                }
                dialogMessage.text = timeRemainingA
            }

            override fun onFinish() {

            }
        }.start()

        cooldownDialogBuilder.show()
    }
}