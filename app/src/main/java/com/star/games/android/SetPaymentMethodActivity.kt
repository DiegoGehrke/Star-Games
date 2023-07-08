package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SetPaymentMethodActivity : AppCompatActivity() {

    private lateinit var setPixBtn: ImageView
    private lateinit var setEmailBtn: ImageView
    private lateinit var backBtn: ImageView

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val bankInfo = db.collection("USERS BANK INFO").document(uid)

    private lateinit var dialogSettingsPaymentMethod: DialogSettingsPaymentMethod

    private lateinit var screenName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_payment_method)

        setPixBtn = findViewById(R.id.open_set_pix_dialog)
        setEmailBtn = findViewById(R.id.open_set_email_dialog)
        screenName = findViewById(R.id.screen_name)
        backBtn = findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            val backIntent = Intent(this, ManageAccountActivity::class.java)
            startActivity(backIntent)
            finish()
        }

        dialogSettingsPaymentMethod = DialogSettingsPaymentMethod(
            this,
            bankInfo
        )

        setPixBtn.setOnClickListener {
            dialogSettingsPaymentMethod.showSetPaymentDialog(
                getString(R.string.define_pix_key),
                getString(R.string.set_a_pix_key_for_us_to_send_you_the_money_to_your_bank_account),
                getString(R.string.enter_your_pix_key),
                isPix = true
            )
        }

        setEmailBtn.setOnClickListener {
            dialogSettingsPaymentMethod.showSetPaymentDialog(
                getString(R.string.define_an_email),
                getString(R.string.set_an_email_to_which_we_will_send_your_digital_gift_card_code),
                getString(R.string.enter_an_email),
                isPix = false
            )
        }

        textsUI()
    }

    private fun textsUI() {
        val textShader: Shader = LinearGradient(
            0f, 12f, 0f, screenName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = textShader
    }
}