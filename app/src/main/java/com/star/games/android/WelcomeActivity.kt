package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random
import java.util.TimeZone

class WelcomeActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private val db = FirebaseFirestore.getInstance()
    private val dailyGamesLimit = FirebaseFirestore.getInstance()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var textView : TextView
    private lateinit var signupTxt : TextView
    private lateinit var loginTxt : TextView
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        FirebaseApp.initializeApp(this)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val user  = FirebaseAuth.getInstance().currentUser
            val db = Firebase.firestore
            val uid = user!!.uid
            val dailyEvent = db.collection("DAILY_EVENT").document(uid)
            val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate: String = dateFormat.format(Date())
            val addData = hashMapOf(
                "dailyLogin" to "y"
            )
            dailyEvent.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    if (document.contains("dailyLogin")) {
                        val intent = Intent(this, HomeActivity::class.java)
                        loadingDialog.hide()
                        startActivity(intent)
                        finish()
                    } else {
                        dailyEvent.update("dailyLogin", "y")
                            .addOnSuccessListener {
                                dailyEvent.addSnapshotListener { snapshot, e ->
                                    if (e != null) {
                                        // ocorreu um erro ao receber a atualização
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null && snapshot.exists()) {
                                        val intent = Intent(this, HomeActivity::class.java)
                                        loadingDialog.hide()
                                        startActivity(intent)
                                        finish()
                                    } else {

                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                                finishAndRemoveTask()
                            }
                    }
                } else {
                    val addData = hashMapOf(
                        "dailyLogin" to "y"
                    )
                    dailyEvent.set(addData)
                        .addOnSuccessListener {
                            dailyEvent.addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    // ocorreu um erro ao receber a atualização
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    val intent = Intent(this, HomeActivity::class.java)
                                    loadingDialog.hide()
                                    startActivity(intent)
                                    finish()
                                } else {

                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                            finishAndRemoveTask()
                        }
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao checar/atualizar/criar o campo", exception)
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                finishAndRemoveTask()
            }

        }
        else {
            loadingDialog.hide()
        }
        val checkBox : CheckBox = findViewById(R.id.checkbox_show_hide_pass)
        textView = findViewById(R.id.welcome_txt)
        signupTxt = findViewById(R.id.signup_txt)
        loginTxt = findViewById(R.id.login_txt)
        val signupButton: LinearLayout = findViewById(R.id.register_btn)
        val emailEditText: EditText = findViewById(R.id.email_field)
        val passwordEditText: EditText = findViewById(R.id.pass_field)
        val confirmPassEditText: EditText = findViewById(R.id.confirm_pass)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = null
                confirmPassEditText.transformationMethod = null
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod()
                confirmPassEditText.transformationMethod = PasswordTransformationMethod()
            }
        }

        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        signupButton.setOnClickListener {

            if (emailEditText.text.toString().isBlank() || passwordEditText.text.toString().isBlank() || confirmPassEditText.text.toString().isBlank())
            {
                Toast.makeText(this, "PREENCHA TODOS OS CAMPOS", Toast.LENGTH_LONG).show()
            } else
            {
                val emailInput: String = emailEditText.text.toString().trim()
                val firstChar = emailInput[0]
                if (firstChar == '.' || firstChar == '@') {
                    Toast.makeText(this, "INVALID EMAIL", Toast.LENGTH_LONG).show()
                } else {
                    if (emailEditText.text.toString().length < 6 || !emailEditText.text.toString()
                            .contains("@") || !emailEditText.text.toString().contains(".")
                    ) {
                        Toast.makeText(this, "INVALID EMAIL", Toast.LENGTH_LONG).show()
                    } else {
                        if (passwordEditText.text.toString().length <= 7) {
                            Toast.makeText(this, "SENHA FRACA", Toast.LENGTH_LONG).show()
                        } else {
                            if (passwordEditText.text.toString() != confirmPassEditText.text.toString()) {
                                Toast.makeText(this, "FALHA NA CONFIRMAÇÃO DA SENHA", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                loadingDialog.show()
                                val email = emailEditText.text.toString()
                                val password = passwordEditText.text.toString()
                                val code = "STAR" + String.format("%04d", Random().nextInt(10000))
                                createUser(email = email, password = password, referralCode = code)
                            }
                        }
                    }
                }
            }

        }
        applyTextUI()
    }

    private fun applyTextUI(){

        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader

        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#66422A"), // First color
                Color.parseColor("#291000") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        signupTxt.paint.shader = textShader1

        val textShader2: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#D7B684"), // First color
                Color.parseColor("#8B7744") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        loginTxt.paint.shader = textShader2
    }

    private fun createUser(email : String, password : String, referralCode: String?) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = FirebaseAuth.getInstance().currentUser!!
                    val email1 = user.email
                    with(sharedPrefs.edit()) {
                        putString("email", email1)
                        putString("uid", user.toString())
                        apply()
                    }
                    val newUser = hashMapOf(
                        "email" to email,
                        "referral" to referralCode, // Código de referência do usuário
                        "points" to 0, // Número de pontos do usuário
                        "referredBy" to "" // Código de referência do usuário que o indicou
                    )
                    db.collection("USERS")
                        .document(user.uid)
                        .set(newUser)
                        .addOnSuccessListener {
                                val newData = hashMapOf(
                                    "principalDailyLimit" to 0,
                                    "especialDailyLimit" to 0
                                )
                                dailyGamesLimit.collection("DAILY GAME LIMIT")
                                    .document(user.uid)
                                    .set(newData)
                                    .addOnSuccessListener {
                                        loadingDialog.hide()
                                        val intent = Intent(this, EditProfileActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        user.delete()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this, "UM ERRO OCORREU, TENTE CRIAR NOVAMENTE SUA CONTA", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        loadingDialog.hide()
                                    }

                        }
                        .addOnFailureListener {
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "UM ERRO OCORREU, TENTE CRIAR NOVAMENTE SUA CONTA", Toast.LENGTH_LONG).show()
                                    }
                                }
                            loadingDialog.hide()
                        }
                } else {
                    Toast.makeText(
                        this,
                        "User registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingDialog.hide()
                }
            }
    }


    /*private fun createUser(email : String, password : String, referralCode: String?) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = FirebaseAuth.getInstance().currentUser!!
                        val email1 = user.email
                        with(sharedPrefs.edit()) {
                            putString("email", email1)
                            putString("uid", user.toString())
                            apply()
                        }
                    val newUser = User(
                        user.uid,
                        referralCode,
                        0,
                        0,
                        0,
                        false
                    )

                        db.collection("USERS")
                            .document(user.uid)
                            .set(newUser)
                            .addOnSuccessListener {
                                if (!newUser.referralCode.isNullOrEmpty()) {
                                    db.collection("USERS").whereEqualTo("referralCode", newUser.referralCode)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            val referredUser = documents.documents[0].toObject(User::class.java)
                                            val currentUser = FirebaseAuth.getInstance().currentUser!!
                                            if (referredUser != null && referredUser.uid != currentUser.uid) {
                                                val amount : Long = 1000
                                                val referralCoins = (amount * 0.2).toInt() // Calcula 20% dos coins gastos
                                                db.collection("USERS").document(referredUser.uid)
                                                    .update("coins", FieldValue.increment(referralCoins.toLong()))

                                                // Adiciona dados à coleção "DAILY GAME LIMIT" somente se referredUser for diferente de null
                                                val newData = hashMapOf(
                                                    "principalDailyLimit" to 0,
                                                    "especialDailyLimit" to 0
                                                )
                                                dailyGamesLimit.collection("DAILY GAME LIMIT")
                                                    .document(referredUser.uid)
                                                    .set(newData)
                                            }
                                        }
                                }
                            }
                            .addOnFailureListener {
                                user.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "UM ERRO OCORREU, TENTE CRIAR NOVAMENTE SUA CONTA", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                loadingDialog.hide()
                            }

                } else {
                    Toast.makeText(
                        this,
                        "User registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingDialog.hide()
                }
            }
    }*/



    data class User(
        val uid: String,
        val referralCode: String?,
        val points: Int,
        val coins: Int,
        val gamerTickets: Int,
        var referralCodeUsed: Boolean = false
    )
    {
        constructor() : this("", "", 0, 0, 0, false)
    }
}