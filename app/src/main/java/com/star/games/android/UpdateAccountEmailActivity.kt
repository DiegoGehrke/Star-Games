package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast

class UpdateAccountEmailActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var credential : AuthCredential

    private lateinit var colorFilter : ColorMatrixColorFilter

    private lateinit var newUserEmail : EditText
    private lateinit var userPassword : EditText
    private lateinit var updateEmailBtn : ImageView
    private lateinit var changeEmailTxt : TextView
    private lateinit var activityName : TextView
    private lateinit var checkCredentialsBtn : ImageView
    private lateinit var checkCredentialsBtnTxt : TextView
    private lateinit var instructionTxt : TextView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var userPassString : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_account_email)

        changeEmailTxt = findViewById(R.id.change_email_txt)
        newUserEmail = findViewById(R.id.new_user_email)
        userPassword = findViewById(R.id.user_password)
        updateEmailBtn = findViewById(R.id.update_email_btn)
        checkCredentialsBtn = findViewById(R.id.check_credentials_btn)
        checkCredentialsBtnTxt = findViewById(R.id.check_credentials_btn_txt)
        instructionTxt = findViewById(R.id.textView8)
        activityName = findViewById(R.id.activity_name)
        textUI()

        loadingDialog = LoadingDialog(this)

        val backBtn : ImageView = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            val intent = Intent(this, ManageAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
        updateEmailBtn.isEnabled = false
        checkCredentialsBtn.isEnabled = false
        instructionTxt.text = getString(R.string.for_security_reasons_enter_your_pass)

        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val colorMatrix = ColorMatrix(matrix)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        //BLACK AND WHITE FILTER!
        updateEmailBtn.colorFilter = colorFilter
        checkCredentialsBtn.colorFilter = colorFilter

        newUserEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkEmail = s.toString()
                if (checkEmail.contains(".") && checkEmail.substring(checkEmail.indexOf(".") + 1).isNotEmpty() &&
                    checkEmail.contains("@") && checkEmail.substring(checkEmail.indexOf("@") + 1).isNotEmpty()) {

                    updateEmailBtn.colorFilter = null
                    updateEmailBtn.isEnabled = true
                    val textEnabledColor = Color.parseColor("#272D42")
                    changeEmailTxt.setTextColor(textEnabledColor)

                } else {
                    updateEmailBtn.colorFilter = colorFilter
                    updateEmailBtn.isEnabled = false
                    val textDisabledColor = Color.parseColor("#656565")
                    changeEmailTxt.setTextColor(textDisabledColor)

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        userPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 7) {
                    checkCredentialsBtn.colorFilter = null
                    checkCredentialsBtn.isEnabled = true
                    val textEnabledColor = Color.parseColor("#272D42")
                    checkCredentialsBtnTxt.setTextColor(textEnabledColor)
                } else {
                    checkCredentialsBtn.colorFilter = colorFilter
                    checkCredentialsBtn.isEnabled = false
                    val textDisabledColor = Color.parseColor("#656565")
                    checkCredentialsBtnTxt.setTextColor(textDisabledColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        updateEmailBtn.setOnClickListener {
            changeUserEmail()
        }

        checkCredentialsBtn.setOnClickListener {
            reAuthenticateUser()
        }
    }

    private fun changeUserEmail() {
        try {
            loadingDialog.show()
            val currentUser = FirebaseAuth.getInstance().currentUser!!
            currentUser.updateEmail(newUserEmail.text.toString())
                .addOnCompleteListener { task ->
                    val newCredentials = EmailAuthProvider.getCredential(
                        currentUser.email!!,
                        userPassString
                    )
                    if (task.isSuccessful) {
                        loadingDialog.hide()
                        showCustomSuccessToast(
                            this,
                            getString(R.string.email_successfully_updated),
                            Toast.LENGTH_LONG, this.window
                        )

                        currentUser.reauthenticate(newCredentials).addOnCompleteListener { reAuthTask ->
                            reAuthTask.isSuccessful.not().let {
                                loadingDialog.hide()

                                finishAffinity()
                                val restartApp = Intent(
                                    this,
                                    SignupActivity::class.java
                                )
                                startActivity(restartApp)

                                showCustomErrorToast(
                                    this,
                                    reAuthTask.exception!!.message.toString(),
                                    Toast.LENGTH_LONG,
                                    this.window
                                )
                            }
                        }
                    } else  {
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_LONG, this.window
                        )
                    }
                }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.you_need_an_account_to_use_the_app),
                        Toast.LENGTH_LONG, this.window
                    )
                    val kickUser = Intent(this, SignupActivity::class.java)
                    startActivity(kickUser)
                    finish()
                }

                is FirebaseAuthRecentLoginRequiredException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.recent_authentication_required),
                        Toast.LENGTH_LONG, this.window
                    )
                }

                is FirebaseNetworkException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.check_your_internet_connection),
                        Toast.LENGTH_LONG, this.window
                    )
                }

                is FirebaseTooManyRequestsException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.too_many_requests_in_a_short_period_of_time),
                        Toast.LENGTH_LONG, this.window
                    )
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.invalid_credentials),
                        Toast.LENGTH_LONG, this.window
                    )
                }

                is FirebaseAuthUserCollisionException -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.the_email_provided_is_already_in_use),
                        Toast.LENGTH_SHORT, this.window
                    )
                }

                else -> {
                    showCustomErrorToast(
                        this,
                        getString(R.string.unknown_error_try_contacting_support),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }
        }
    }

    private fun reAuthenticateUser() {
        loadingDialog.show()
        credential = EmailAuthProvider.getCredential(
            user!!.email!!,
            userPassword.text.toString()
        )
        userPassString = userPassword.text.toString()

        user.reauthenticate(credential)
            .addOnSuccessListener {
                loadingDialog.hide()
                instructionTxt.text = getString(R.string.change_email_textview_desc)
                checkCredentialsBtn.visibility = View.GONE
                userPassword.visibility = View.GONE
                checkCredentialsBtnTxt.visibility = View.GONE
                updateEmailBtn.visibility = View.VISIBLE
                changeEmailTxt.visibility = View.VISIBLE
                newUserEmail.visibility = View.VISIBLE

                showCustomSuccessToast(
                    this,
                    getString(R.string.successful_verification),
                    Toast.LENGTH_LONG,
                    this.window

                )
            }
            .addOnFailureListener {e: Exception ->
                loadingDialog.hide()
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        showCustomErrorToast(this,
                            getString(R.string.incorrect_password),
                            Toast.LENGTH_LONG,
                            this.window)
                    }
                    else -> {
                        showCustomErrorToast(this,
                            getString(R.string.error_, e),
                            Toast.LENGTH_LONG,
                            this.window)
                    }
                }
            }
    }

    private fun textUI() {
        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, activityName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        activityName.paint.shader = textShader
    }
}