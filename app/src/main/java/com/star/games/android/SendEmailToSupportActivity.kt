package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SendEmailToSupportActivity : AppCompatActivity() {

        private lateinit var screenName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email_to_support)

       screenName = findViewById(R.id.screen_name)
        val sendEmail : ImageView = findViewById(R.id.send_email)
        val subjectText = findViewById<EditText>(R.id.subject_editText)
        val messageText = findViewById<EditText>(R.id.message_editText)
        val subjectRemainingCharsCounter: TextView = findViewById(R.id.subject_remaining_chars_counter)
        val messageRemainingCharsCounter: TextView = findViewById(R.id.message_remaining_chars_counter)
        val background: ConstraintLayout = findViewById(R.id.background)
        val backBtn: ImageView = findViewById(R.id.back_btn)
        val subjectMaxChars = 100
        val messageMaxChars = 1000

        backBtn.setOnClickListener {
            val intent = Intent(this, HelpSupportActivity::class.java)
            startActivity(intent)
            finish()
        }

        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val sharedPreferences = EncryptedSharedPreferences.create(
            "secretEmail",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit().putString("theEmail", "minecraftpirata000@gmail.com").apply()


        subjectText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (messageText.length() > 0 && messageText.length() > 0) {
                    sendEmail.visibility = View.VISIBLE
                }
                else {
                    sendEmail.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val subjectRemainingChars = subjectMaxChars - s?.length!! ?: 0
                subjectRemainingCharsCounter.text = "$subjectRemainingChars Remaining Characters"
            }
        })
        subjectRemainingCharsCounter.visibility = View.INVISIBLE
        subjectText.setBackgroundResource(0)
        subjectText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                subjectRemainingCharsCounter.visibility = View.VISIBLE
                subjectText.setBackgroundResource(R.drawable.big_edittext_subject)
            } else {
                subjectRemainingCharsCounter.visibility = View.INVISIBLE
                subjectText.setBackgroundResource(0)
            }
        }

        messageText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (messageText.length() > 0 && messageText.length() > 0) {
                    sendEmail.visibility = View.VISIBLE
                }
                else {
                    sendEmail.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val messageRemainingChars = messageMaxChars - s?.length!! ?: 0
                messageRemainingCharsCounter.text = "$messageRemainingChars Remaining Characters"
            }
        })
        messageRemainingCharsCounter.visibility = View.INVISIBLE
        messageText.setBackgroundResource(0)
        messageText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                messageRemainingCharsCounter.visibility = View.VISIBLE
                messageText.setBackgroundResource(R.drawable.big_edittext)
            } else {
                messageRemainingCharsCounter.visibility = View.INVISIBLE
                messageText.setBackgroundResource(0)
            }
        }

        sendEmail.visibility = View.GONE
        sendEmail.setOnClickListener {
            val getEmail = sharedPreferences.getString("theEmail", null)

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getEmail.toString()))
                putExtra(Intent.EXTRA_SUBJECT, subjectText.toString())
                putExtra(Intent.EXTRA_TEXT, messageText.toString())
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        background.setOnClickListener {
            subjectText.clearFocus()
            messageText.clearFocus()
        }

        textUI()

    }

    private fun textUI() {
        val ts11111: Shader = LinearGradient(
            0f, 12f, 0f, screenName.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = ts11111
    }

}