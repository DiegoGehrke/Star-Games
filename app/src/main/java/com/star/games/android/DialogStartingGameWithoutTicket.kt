package com.star.games.android

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView

class DialogStartingGameWithoutTicket(val context: Context) {
    private lateinit var attentionDialogBuilder: Dialog
    private lateinit var positiveButton: ImageView
    private lateinit var negativeButton: ImageView
    private lateinit var intent: Intent

    fun showAttentionDialog(gameName: String) {
        attentionDialogBuilder = Dialog(context)
        attentionDialogBuilder.setContentView(R.layout.attention_dialog_layout)
        attentionDialogBuilder.setCancelable(true)
        attentionDialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val wantToAccessMemoryGame: Boolean = gameName == "Memory Game"
        val wantToAccessMathGame: Boolean = gameName == "Math Game"

        positiveButton = attentionDialogBuilder.findViewById(R.id.attention_dialog_positive_button)
        negativeButton = attentionDialogBuilder.findViewById(R.id.attention_dialog_negative_button)

        positiveButton.setOnClickListener {
            intent = if (wantToAccessMemoryGame) {
                Intent(context, MemoryGameActivity::class.java)
            } else if (wantToAccessMathGame) {
                Intent(context, MathGameActivity::class.java)
            } else {
                Intent(context, QuizGameActivity::class.java)
            }
            intent.putExtra("noHaveTicketsOrChances", true)
            context.startActivity(intent)
            attentionDialogBuilder.dismiss()
            if (context is Activity) {
                context.finish()
            }
        }

        negativeButton.setOnClickListener {
            attentionDialogBuilder.dismiss()
        }

        attentionDialogBuilder.show()
    }
}