package com.star.games.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class NumericRepetitionGameActivity : AppCompatActivity() {

    private lateinit var zeroBtn: ImageView
    private lateinit var oneBtn: ImageView
    private lateinit var twoBtn: ImageView
    private lateinit var threeBtn: ImageView
    private lateinit var fourBtn: ImageView
    private lateinit var fiveBtn: ImageView
    private lateinit var sixBtn: ImageView
    private lateinit var sevenBtn: ImageView
    private lateinit var eightBtn: ImageView
    private lateinit var nineBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numeric_repetition_game)

        zeroBtn = findViewById(R.id.null_btn)
        oneBtn = findViewById(R.id.first_btn)
        twoBtn = findViewById(R.id.second_btn)
        threeBtn = findViewById(R.id.third_btn)
        fourBtn = findViewById(R.id.fourth_btn)
        fiveBtn = findViewById(R.id.fifth_btn)
        sixBtn = findViewById(R.id.sixth_btn)
        sevenBtn = findViewById(R.id.seventh_btn)
        eightBtn = findViewById(R.id.eigth_btn)
        nineBtn = findViewById(R.id.ninth_btn)

        val imageList = listOf (
            zeroBtn,
            oneBtn,
            twoBtn,
            threeBtn,
            fourBtn,
            fiveBtn,
            sixBtn,
            sevenBtn,
            eightBtn,
            nineBtn
        )
        val loadGame = NumericRepetitionGameLogic(this@NumericRepetitionGameActivity, imageList)
        loadGame.startGame()
    }

}