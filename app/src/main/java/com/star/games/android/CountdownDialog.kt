package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.widget.TextView
import java.util.concurrent.atomic.AtomicInteger

class CountdownDialog(val context: Context) {

    private lateinit var countdownDialog: Dialog
    private lateinit var countDownTxt: TextView

    private var mathGameActivity: MathGameActivity? = null
    private var memoryGameActivity: MemoryGameActivity? = null
    private var quizGameActivity: QuizGameActivity? = null

    fun countdownShow() {
        countdownDialog = Dialog(context)
        countdownDialog.setContentView(R.layout.countdown_dialog_layout)
        countdownDialog.setCancelable(false)
        countdownDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        countDownTxt = countdownDialog.findViewById(R.id.countdown_txt)

        val handler = Handler()
        val atomicInteger = AtomicInteger(3)
        val counter: Runnable = object : Runnable {
            override fun run() {
                countDownTxt.text = atomicInteger.get().toString()
                if (atomicInteger.getAndDecrement() >= 1) {
                    handler.postDelayed(this, 1000)
                } else {
                    when (context) {
                        is MathGameActivity -> {
                            mathGameActivity = context
                            mathGameActivity?.initializeMathGame()
                            mathGameActivity?.startTimer()
                        }
                        is MemoryGameActivity -> {
                            memoryGameActivity = context
                            memoryGameActivity?.startTimer()
                            memoryGameActivity?.startGame()
                        }
                        is QuizGameActivity -> {
                            quizGameActivity = context
                            quizGameActivity?.startTimer()
                            quizGameActivity?.initializeQuizGame()
                        }
                    }

                    countdownDialog.dismiss()
                }
            }
        }
        handler.postDelayed(counter, 1000)


        countdownDialog.show()
    }
}
