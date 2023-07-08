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
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import java.util.Random


class SignupActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    private val db = FirebaseFirestore.getInstance()
    private lateinit var user: FirebaseUser

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var textView: TextView
    private lateinit var signupTxt: TextView
    private lateinit var loginTxt: TextView

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPassEditText: EditText
    private lateinit var friendCodeEditText: EditText

    private lateinit var signupButton: ImageView
    private lateinit var signInButton: ImageView

    private lateinit var checkBox: CheckBox

    private lateinit var background: ConstraintLayout

    private lateinit var colorFilter: ColorMatrixColorFilter

    private lateinit var domains: List<String>

    private var getInvitedFriendCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        initializeViews()
        setViewsListeners()
        applyTextUI()
    }

    private fun initializeViews() {
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        domains = listOf(
            "@gmail.com",
            "@yahoo.com",
            "@hotmail.com",
            "@outlook.com",
            "@mail.ru",
            "@qq.com",
            "@163.com",
            "@gmx.com",
            "@aol.com",
            "@yandex.com",
            "@zoho.com",
            "@protonmail.com",
            "@icloud.com"
        )
        signupButton = findViewById(R.id.register_btn)
        signupButton.isEnabled = false
        background = findViewById(R.id.background)
        textView = findViewById(R.id.welcome_txt)
        signupTxt = findViewById(R.id.signup_txt)
        loginTxt = findViewById(R.id.login_txt)
        checkBox = findViewById(R.id.checkbox_show_hide_pass)
        signInButton = findViewById(R.id.login_btn)
        emailEditText = findViewById(R.id.email_field)
        passwordEditText = findViewById(R.id.pass_field)
        confirmPassEditText = findViewById(R.id.confirm_pass)
        friendCodeEditText = findViewById(R.id.enter_friend_referral_code)
        loadingDialog = LoadingDialog(this)

        setupTextWatcher(emailEditText)
        setupTextWatcher(passwordEditText)
        setupTextWatcher(confirmPassEditText)
        setupTextWatcher(friendCodeEditText)
    }

    private fun applyTextUI() {
        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader

        val textShader2: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize,
            intArrayOf(
                Color.parseColor("#D7B684"),
                Color.parseColor("#8B7744")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        loginTxt.paint.shader = textShader2

        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val colorMatrix = ColorMatrix(matrix)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        signupButton.colorFilter = colorFilter
    }

    private fun setViewsListeners() {
        background.setOnClickListener {
            emailEditText.clearFocus()
            passwordEditText.clearFocus()
            confirmPassEditText.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText) {
                imm.hideSoftInputFromWindow(emailEditText.windowToken, 0)
                imm.hideSoftInputFromWindow(passwordEditText.windowToken, 0)
                imm.hideSoftInputFromWindow(confirmPassEditText.windowToken, 0)
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = null
                confirmPassEditText.transformationMethod = null
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod()
                confirmPassEditText.transformationMethod = PasswordTransformationMethod()
            }
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val friendCode = friendCodeEditText.text.toString().trim()
            loadingDialog.show()

            if (friendCode.isNotEmpty()) {
                checkFriendCode(friendCode) { isValidFriendCode ->
                    if (isValidFriendCode) {
                        createUser(email, password, friendCode)
                    } else {
                        showCustomErrorToast(
                            this,
                            getString(R.string.invalid_friend_code),
                            Toast.LENGTH_SHORT,
                            this.window
                        )
                        loadingDialog.hide()
                    }
                }
            } else {
               showCustomErrorToast(
                   this,
                   getString(R.string.enter_an_referral_code),
                   Toast.LENGTH_LONG,
                   this.window
               )
                loadingDialog.hide()
            }

        }

        signInButton.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkFriendCode(friendCode: String, callback: (Boolean) -> Unit) {
        val friendCodesCollection = db.collection("FRIENDS CODES")
        val query = friendCodesCollection.whereEqualTo("code", friendCode).limit(1)

        query.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_SHORT,
                    this.window
                )
                loadingDialog.hide()
            }
    }

    private fun createUser(
        email: String,
        password: String,
        friendCode: String
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = FirebaseAuth.getInstance().currentUser!!
                    val email1 = user.email
                    val referralCode = generateReferralCode()

                    with(sharedPrefs.edit()) {
                        putString("email", email1)
                        putString("uid", user.toString())
                        putString("friendCode", friendCode)
                        apply()
                    }
                    val newUser: HashMap<String, Any> = hashMapOf(
                        "coins" to 100.00F,
                        "gamerTickets" to 0,
                        "vipPoints" to 0,
                        "myCode" to referralCode,
                        "invitedFriends" to 0,
                        "friendCode" to friendCode
                    )
                    db.collection("USERS")
                        .document(user.uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            val friendCodeData = hashMapOf(
                                "code" to referralCode,
                                "codeOwnerUID" to user.uid
                            )
                            db. collection("FRIENDS CODES")
                                .document(referralCode)
                                .set(friendCodeData)
                                .addOnSuccessListener {
                                    getFriendCodeOwnerUID(friendCode) { friendCodeOwnerUID ->
                                        val friendCodeOwnerRef = db
                                            .collection("USERS")
                                            .document(friendCodeOwnerUID!!)
                                        friendCodeOwnerRef
                                            .get()
                                            .addOnSuccessListener { snapshot ->
                                                snapshot
                                                    .getLong("invitedFriends")
                                                    .let { invitedFriends ->
                                                        getInvitedFriendCount = invitedFriends!!.toInt()
                                                        getInvitedFriendCount += 1
                                                        val data: HashMap<String, Any> = hashMapOf(
                                                            "invitedFriends" to getInvitedFriendCount
                                                        )
                                                        friendCodeOwnerRef.set(data, SetOptions.merge())
                                                            .addOnSuccessListener {
                                                                val intent = Intent(this, ConfirmEmailActivity::class.java)
                                                                startActivity(intent)
                                                                loadingDialog.hide()
                                                                finish()
                                                            }
                                                    }
                                            }

                                    }
                                }
                                .addOnFailureListener { exception ->
                                    showCustomErrorToast(
                                        this,
                                        getString(R.string.error_, exception.message.toString()),
                                        Toast.LENGTH_LONG,
                                        this.window
                                    )
                                    loadingDialog.hide()
                                }
                        }
                        .addOnFailureListener { exception ->
                            showCustomErrorToast(
                                this,
                                getString(R.string.error_, exception.message.toString()),
                                Toast.LENGTH_LONG,
                                this.window
                            )
                            loadingDialog.hide()
                        }
                } else {
                    showCustomErrorToast(
                        this,
                        getString(R.string.error_, task.exception!!.message.toString()),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                    loadingDialog.hide()
                }
            }
    }

    private fun getFriendCodeOwnerUID(friendCode: String, callback: (String?) -> Unit) {
        val usersCollection = db.collection("USERS")
        val query = usersCollection.whereEqualTo("myCode", friendCode).limit(1)

        query
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val codeOwnerUID = document.id
                    callback(codeOwnerUID)
                } else {
                    callback(null)
                    showCustomErrorToast(
                        this,
                        getString(R.string.friend_code_not_found),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_LONG,
                    this.window
                )
                callback(null)
            }
    }

    private fun generateReferralCode(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 6
        val random = Random(System.currentTimeMillis())

        val referralCode = StringBuilder(codeLength)
        repeat(codeLength) {
            val randomIndex = random.nextInt(characters.length)
            referralCode.append(characters[randomIndex])
        }

        return referralCode.toString()
    }

    private fun setupTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValidEmail = isValidEmail(emailEditText.text.toString())
                val isValidPassword = isValidPassword(
                    passwordEditText.text.toString(),
                    confirmPassEditText.text.toString()
                )
                val isValidCode = friendCodeEditText.text.length == 6

                if (isValidEmail && isValidPassword && isValidCode) {
                    signupButton.isEnabled = true
                    signupButton.colorFilter = null
                    val enabledTextColor = Color.parseColor("#291000")
                    signupTxt.setTextColor(enabledTextColor)
                } else {
                    signupButton.colorFilter = colorFilter
                    signupButton.isEnabled = false
                    val disabledTextColor = Color.parseColor("#656565")
                    signupTxt.setTextColor(disabledTextColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return domains.any { email.contains(it) }
    }

    private fun isValidPassword(password: String, reEnteredPass: String): Boolean {
        return password.length > 7 && reEnteredPass == password
    }
}