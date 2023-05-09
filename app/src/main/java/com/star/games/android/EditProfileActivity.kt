package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var displayTextView: TextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firstNameEditText = findViewById(R.id.first_name)
        lastNameEditText = findViewById(R.id.last_name)
        displayTextView = findViewById(R.id.textView3)
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val textView: TextView = findViewById(R.id.welcome_txt)
        val saveTxt: TextView = findViewById(R.id.save_txt)

        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader1

        val textShader2: Shader = LinearGradient(
            0f, 24f, 0f, saveTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#66422A"), // First color
                Color.parseColor("#291000") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        saveTxt.paint.shader = textShader2

        val saveButton = findViewById<LinearLayout>(R.id.save_changes_btn)
        saveButton.setOnClickListener {
            if (firstNameEditText.text.length > 2 && lastNameEditText.text.length > 2 ){
                with(sharedPrefs.edit()) {
                    putString("FirstName", firstNameEditText.text.toString())
                    putString("LastName", lastNameEditText.text.toString())
                    apply()
                }
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "NOME MUITO PEQUENO", Toast.LENGTH_LONG).show()
            }

        }
        val firstName = sharedPrefs.getString("FirstName", "")
        val lastName = sharedPrefs.getString("LastName", "")
        firstNameEditText.setText(firstName)
        lastNameEditText.setText(lastName)


    }

}