package com.star.games.android

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class GamesCenterActivity : AppCompatActivity() {

    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyLimit = db.collection("DAILY GAME LIMIT").document(uid)
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)
    private lateinit var loadingDialog : LoadingDialog
    private var unlockGames : Boolean = false
    private var principalDailyLimit by Delegates.notNull<Long>()
    private var useTicketsDailyMission by Delegates.notNull<Int>()
    private var gamerTickets by Delegates.notNull<Long>()
    private lateinit var backBtn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_center)

        val backToHomeActivity = Intent(this@GamesCenterActivity, HomeActivity::class.java)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        backBtn = findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            startActivity(backToHomeActivity)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(backToHomeActivity)
                finish()
            }
        })

        userData.get()
            .addOnSuccessListener { document ->
                if (document.contains("gamerTickets")) {
                    gamerTickets = document.getLong("gamerTickets")!!
                    if(gamerTickets > 0){
                        dailyLimit.get()
                            .addOnSuccessListener { snapshot ->
                                loadingDialog.hide()
                                principalDailyLimit = snapshot.getLong("principalDailyLimit")!!
                                if (principalDailyLimit >= 15){
                                    unlockGames = false
                                    loadingDialog.hide()
                                    Toast.makeText(this, "SEM CHANCES DIÁRIAS", Toast.LENGTH_LONG).show()
                                }
                                else {
                                    unlockGames = true
                                }
                            }

                    } else {
                        loadingDialog.hide()
                        unlockGames = false
                        Toast.makeText(this, "SEM TICKETS SUFICIENTES", Toast.LENGTH_LONG).show()
                        loadingDialog.hide()
                    }

                } else {
                    loadingDialog.hide()
                    unlockGames = false
                    loadingDialog.hide()
                }

            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
                loadingDialog.hide()
            }

        val goToMemoryGameBtn : ImageView = findViewById(R.id.go_to_memory_game_btn)
        goToMemoryGameBtn.setOnClickListener {
            if (unlockGames){
                loadingDialog.show()
                gamerTickets -= 1
                principalDailyLimit += 1
                val data = hashMapOf(
                    "gamerTickets" to gamerTickets
                )
                val newData = hashMapOf(
                    "principalDailyLimit" to principalDailyLimit
                )
                dailyEvent.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        if (document.contains("useTicketsQuest")) {
                            val getUsedTickets = document.getLong("useTicketsQuest") ?: 0
                            useTicketsDailyMission = getUsedTickets.toInt()
                            dailyEvent.update("useTicketsQuest", getUsedTickets + 1)
                        } else {
                            dailyEvent.update("useTicketsQuest", 1)
                                .addOnSuccessListener {
                                    dailyEvent.addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            // ocorreu um erro ao receber a atualização
                                            return@addSnapshotListener
                                        }
                                        if (snapshot!!.exists()) {
                                            val map = snapshot.data
                                            val ticketsDailyMission = map?.get("useTicketsQuest") as Long
                                            useTicketsDailyMission = ticketsDailyMission.toInt()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    loadingDialog.hide()
                                }
                        }
                    } else {
                        // O documento não existe, cria com o campo e valor inicial 1
                        val addData = hashMapOf(
                            "ticketsDailyMission" to 1
                        )
                        dailyEvent.set(addData)
                            .addOnSuccessListener {
                                dailyEvent.addSnapshotListener { snapshot, e ->
                                    if (e != null) {
                                        // ocorreu um erro ao receber a atualização
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null && snapshot.exists()) {
                                        val map = snapshot.data
                                        val ticketsDailyMission = map?.get("ticketsDailyMission") as Long
                                        useTicketsDailyMission = ticketsDailyMission.toInt()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                loadingDialog.hide()
                            }
                    }
                }.addOnFailureListener { exception ->
                    loadingDialog.hide()
                    Log.e("Firestore", "Erro ao checar/atualizar/criar o campo", exception)
                }
                userData.update(data as Map<String, Any>)
                dailyLimit.update(newData as Map<String, Any>)
                loadingDialog.hide()
                val intent = Intent(this, MemoryGameActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                loadingDialog.hide()
                Toast.makeText(this, "TICKETS INSUFICIENTES OU CHANCES DIÁRIAS ESGOTADAS", Toast.LENGTH_LONG).show()
            }
        }

        val goToMathGameBtn : ImageView = findViewById(R.id.go_to_math_game_btn)
        goToMathGameBtn.setOnClickListener {
            val goToMathActivity = Intent(this, MathGameActivity::class.java)
            startActivity(goToMathActivity)
            finish()
        }
        val goToNumericGameBtn : ImageView = findViewById(R.id.go_to_numeric_repetition_btn)
        goToNumericGameBtn.setOnClickListener {
            val goToNumericRepetitionActivity = Intent(this, NumericRepetitionGameActivity::class.java)
            startActivity(goToNumericRepetitionActivity)
            finish()
        }
    }
}