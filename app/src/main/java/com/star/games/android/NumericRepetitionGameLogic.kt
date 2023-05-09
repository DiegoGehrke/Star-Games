package com.star.games.android

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import kotlin.properties.Delegates

class NumericRepetitionGameLogic(val context: Context, private val views: List<ImageView>) {

    private var points = 0
    private var correctImages = mutableListOf<ImageView>()
    private var clickEnabled = false
    private var generateMore by Delegates.notNull<Int>()
    private var expectedImages = mutableListOf<ImageView>()
    private var currentExpectedIndex = 0

    fun startGame() {
        expectedImages.clear()
        correctImages.clear()
        currentExpectedIndex = 0
        generateMore = 1
        showNewImage()
    }

    private fun showNewImage() {
        if (correctImages.size >= 6) {
            return
        }

        // Adiciona a próxima imagem esperada na lista
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            expectedImages.add(views.random())
            val imageToChange = expectedImages.last()

            correctImages.add(imageToChange)
            imageToChange.setImageResource(R.drawable.green_btn)
            imageToChange.setImageResource(R.drawable.math_btn)
            if (expectedImages.size >= views.size) {
                return@postDelayed
            }
            handler.postDelayed({
                showNewImage()
            }, 500)
        }, 1000)
        val addDelay: Long = generateMore * 1000L
        val handler1 = Handler(Looper.getMainLooper())
        handler1.postDelayed({
            clickEnabled = true
            enableClicks()
        }, addDelay)

    }

    private fun enableClicks() {
        for (view in views) {
            view.setOnClickListener {
                if (clickEnabled) {
                    if (correctImages.contains(view)) {
                        if (view == expectedImages[currentExpectedIndex]) {
                            points++
                            currentExpectedIndex++
                            correctImages.remove(view)
                            if (correctImages.isEmpty()) {
                                showNewImage()
                            }
                        } else {
                            Toast.makeText(view.context, "Errado!", Toast.LENGTH_SHORT).show()
                            startGame()
                        }
                    } else {
                        Toast.makeText(view.context, "Errado!", Toast.LENGTH_SHORT).show()
                        startGame()
                    }
                }
            }
        }
    }
}


