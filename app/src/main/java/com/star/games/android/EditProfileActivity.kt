package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText

    private lateinit var displayTextView: TextView
    private lateinit var textView: TextView
    private lateinit var saveTxt: TextView

    private var firstName: String = ""
    private var lastName: String = ""

    private lateinit var saveButton: ImageView

    private lateinit var colorFilter : ColorMatrixColorFilter

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeViews()
        addOnClickedListeners()
        textUI()
    }

    private fun initializeViews() {
        firstNameEditText = findViewById(R.id.first_name)
        lastNameEditText = findViewById(R.id.last_name)
        displayTextView = findViewById(R.id.textView3)
        textView = findViewById(R.id.welcome_txt)
        saveTxt = findViewById(R.id.save_txt)
        saveButton = findViewById(R.id.save_changes_btn)
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firstName = sharedPrefs.getString("FirstName", "") ?: ""
        lastName = sharedPrefs.getString("LastName", "") ?: ""
        firstNameEditText.setText(firstName)
        lastNameEditText.setText(lastName)
        setupTextWatcher(firstNameEditText)
        setupTextWatcher(lastNameEditText)
    }

    private fun addOnClickedListeners() {
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
                showCustomErrorToast(
                    this,
                    getString(R.string.name_too_short),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }

        }
    }

    private fun setupTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValidPersonName = isValidPersonName(
                    firstNameEditText.text.toString(),
                    lastNameEditText.text.toString()
                )
                if (isValidPersonName ) {
                    saveButton.isEnabled = true
                    saveButton.colorFilter = null
                    val enabledTextColor = Color.parseColor("#291000")
                    saveTxt.setTextColor(enabledTextColor)
                } else {
                    saveButton.colorFilter = colorFilter
                    saveButton.isEnabled = false
                    val disabledTextColor = Color.parseColor("#656565")
                    saveTxt.setTextColor(disabledTextColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidPersonName(firstName: String, secondName: String): Boolean {
        return firstName.length > 2 && secondName.length > 2
    }

    private fun textUI() {
        val textShader1: Shader = LinearGradient(
            0f, 12f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader1

        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val colorMatrix = ColorMatrix(matrix)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        saveButton.colorFilter = colorFilter
    }

}