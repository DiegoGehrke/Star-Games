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
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class LoginActivity : AppCompatActivity() {

    private lateinit var textView : TextView
    private lateinit var signupTxt : TextView
    private lateinit var loginTxt : TextView
    private lateinit var forgotPasswordTxt: TextView

    private lateinit var editTextPassword : EditText
    private lateinit var editTextEmail : EditText

    private lateinit var loginBtn : ImageView
    private lateinit var sharedPrefs : SharedPreferences

    private lateinit var checkBox: CheckBox

    private lateinit var goToSignUpActivityBtn: LinearLayout

    private lateinit var colorFilter : ColorMatrixColorFilter

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setOnClickListeners()
        textsUI()
        checkCredentials()
    }

    private fun initializeViews() {
        loadingDialog = LoadingDialog(this@LoginActivity)
        textView = findViewById(R.id.welcome_txt)
        signupTxt = findViewById(R.id.signup_txt)
        loginTxt = findViewById(R.id.login_txt)
        editTextPassword = findViewById(R.id.pass_field)
        editTextEmail = findViewById(R.id.email_field)
        loginBtn = findViewById(R.id.login_btn)
        goToSignUpActivityBtn = findViewById(R.id.dont_have_account_btn)
        forgotPasswordTxt = findViewById(R.id.text_forgot_password)
        checkBox = findViewById(R.id.checkbox_show_hide_pass)

        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    private fun setOnClickListeners() {
        loginBtn.setOnClickListener {
            loadingDialog.show()
            loginUser(
                email = editTextEmail.text.toString(),
                password = editTextPassword.text.toString()
            )
        }

        goToSignUpActivityBtn.setOnClickListener {
            val intent = Intent(
                this,
                SignupActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        forgotPasswordTxt.setOnClickListener {
            val resetPasswordDialog = ResetPasswordDialog(this)
            resetPasswordDialog.showResetPassDialog()
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextPassword.transformationMethod = null
            } else {
                editTextPassword.transformationMethod = PasswordTransformationMethod()
            }
        }
    }

    private fun textsUI() {
        val textShader: Shader = LinearGradient(
            0f, 12f, 0f, textView.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader

        val textShader2: Shader = LinearGradient(
            0f, 12f, 0f, textView.textSize,
            intArrayOf(
                Color.parseColor("#D7B684"),
                Color.parseColor("#8B7744")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        signupTxt.paint.shader = textShader2

        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val colorMatrix = ColorMatrix(matrix)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        //BLACK AND WHITE FILTER!
        loginBtn.colorFilter = colorFilter
        loginBtn.isEnabled = false
    }

    private fun checkCredentials() {
        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValidEmail = isValidEmail(editTextEmail.text.toString())
                val isValidPassword = isValidPassword(editTextPassword.text.toString())

                if (isValidEmail && isValidPassword) {
                    loginBtn.colorFilter = null
                    loginBtn.isEnabled = true
                    val textEnabledColor = Color.parseColor("#291000")
                    loginTxt.setTextColor(textEnabledColor)
                } else {
                    loginBtn.colorFilter = colorFilter
                    loginBtn.isEnabled = false
                    val textEnabledColor = Color.parseColor("#656565")
                    loginTxt.setTextColor(textEnabledColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        })
        editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValidEmail = isValidEmail(editTextEmail.text.toString())
                val isValidPassword = isValidPassword(editTextPassword.text.toString())

                if (isValidEmail && isValidPassword) {
                    loginBtn.colorFilter = null
                    loginBtn.isEnabled = true
                    val textEnabledColor = Color.parseColor("#291000")
                    loginTxt.setTextColor(textEnabledColor)
                } else {
                    loginBtn.colorFilter = colorFilter
                    loginBtn.isEnabled = false
                    val textEnabledColor = Color.parseColor("#656565")
                    loginTxt.setTextColor(textEnabledColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }

        })
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains(".") && email.substring(email.indexOf(".") + 1).isNotEmpty() &&
                email.contains("@") && email.substring(email.indexOf("@") + 1).isNotEmpty()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length > 7
    }

    private fun loginUser(email: String, password: String) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    loadingDialog.hide()
                    val user = FirebaseAuth.getInstance().currentUser!!
                    val email1 = user.email
                    with(sharedPrefs.edit()) {
                        putString("email", email1)
                        putString("uid", user.toString())
                        apply()
                    }
                    val isEmailVerified = user.isEmailVerified
                    if (isEmailVerified) {
                        val goToEditProfileActivity = Intent(this, EditProfileActivity::class.java)
                        startActivity(goToEditProfileActivity)
                        finish()
                    } else {
                        val goToConfirmEmailActivity = Intent(this, ConfirmEmailActivity::class.java)
                        startActivity(goToConfirmEmailActivity)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    loadingDialog.hide()
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            showCustomErrorToast(
                                this@LoginActivity,
                                getString(R.string.invalid_credentials),
                                Toast.LENGTH_LONG,
                                this.window
                            )
                        }
                        is FirebaseAuthInvalidUserException -> {
                            showCustomErrorToast(
                                this@LoginActivity,
                                getString(R.string.user_not_found),
                                Toast.LENGTH_LONG,
                                this.window
                            )
                        }
                        else -> {
                            showCustomErrorToast(
                                this@LoginActivity,
                                exception.message.toString(),
                                Toast.LENGTH_LONG,
                                this.window
                            )
                        }
                    }

                }
        }
}