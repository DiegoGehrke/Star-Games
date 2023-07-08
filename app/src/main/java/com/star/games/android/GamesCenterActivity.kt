package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import java.text.NumberFormat
import java.util.Locale

class GamesCenterActivity : AppCompatActivity() {

    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var showAttentionDialog: DialogStartingGameWithoutTicket
    
    private var userHaveTickets: Boolean = false

    private var useTicketsDailyMission: Int = 0
    private var gamerTickets: Int = 0
    private var friendCode: String = ""

    private lateinit var backBtn: ImageView
    private lateinit var goToMemoryGameBtn: ImageView
    private lateinit var goToMathGameBtn: ImageView
    private lateinit var goToQuizGameBtn: ImageView
    private lateinit var helpBtn: ImageView

    private lateinit var screenName: TextView

    private lateinit var goToMathGameIntent: Intent
    private lateinit var goToMemoryGameIntent: Intent
    private lateinit var goToQuizGameIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_center)

        initializeViews()
        fetchUserData()
        setBackToPreviousScreenListener()
        textsUI()
    }

    private fun initializeViews() {
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        backBtn = findViewById(R.id.back_btn)
        goToMemoryGameBtn = findViewById(R.id.go_to_memory_game_btn)
        goToMathGameBtn = findViewById(R.id.go_to_math_game_btn)
        goToQuizGameBtn = findViewById(R.id.go_to_quiz_game_btn)
        screenName = findViewById(R.id.main_games_activity_name)
        helpBtn = findViewById(R.id.help_icon)
        goToMemoryGameIntent = Intent(this, MemoryGameActivity::class.java)
        goToMathGameIntent = Intent(this, MathGameActivity::class.java)
        goToQuizGameIntent = Intent(this, QuizGameActivity::class.java)
        val rewardDialog = RewardDialog(this)
        if (intent.extras != null) {
            val coins: Float = intent.getFloatExtra("earnedCoins", 0.0f)
            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2
            val rewardNumberFormatted = numberFormat.format(coins)
            val rewardNumberFloat = rewardNumberFormatted.replace(",", ".").toFloat()
            rewardDialog.showRewardDialog(R.drawable.star_reward_icon, rewardNumberFloat)
        }

    }

    private fun fetchUserData() {
        userData.get()
            .addOnSuccessListener { document ->
                    friendCode = document.getString("friendCode")!!
                    gamerTickets = document.getLong("gamerTickets")!!.toInt()
                    if (gamerTickets > 0) {
                        userHaveTickets = true
                        fetchUserDailyEventData()
                    } else {
                        loadingDialog.hide()
                        userHaveTickets = false
                        setViewsListeners()
                    }
                }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(
                        R.string.error_,
                        exception.message.toString()
                    ),
                    Toast.LENGTH_LONG,
                    this.window
                )
                loadingDialog.hide()
            }
    }

    private fun fetchUserDailyEventData() {
        dailyEvent.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (document.contains("usedTickets")) {
                        loadingDialog.hide()
                        val getUsedTickets: Long = document.getLong("usedTickets") ?: 0
                        useTicketsDailyMission = getUsedTickets.toInt()
                        setViewsListeners()
                    } else {
                        dailyEvent.update("usedTickets", 0)
                            .addOnSuccessListener {
                                dailyEvent.addSnapshotListener { snapshot, e ->
                                    if (e != null) {
                                        return@addSnapshotListener
                                    }
                                    if (snapshot!!.exists()) {
                                        loadingDialog.hide()
                                        val map: Map<String, Any> = snapshot.data!!
                                        val ticketsDailyMission: Long = map["usedTickets"] as Long
                                        useTicketsDailyMission = ticketsDailyMission.toInt()
                                        setViewsListeners()
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
                                setViewsListeners()
                            }
                    }
                } else {
                    val addData: HashMap<String, Int> = hashMapOf(
                        "usedTickets" to 0
                    )
                    dailyEvent.set(addData)
                        .addOnSuccessListener {
                            dailyEvent.addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    loadingDialog.hide()
                                    val map: Map<String, Any> = snapshot.data!!
                                    val ticketsDailyMission: Long = map["usedTickets"] as Long
                                    useTicketsDailyMission = ticketsDailyMission.toInt()
                                    setViewsListeners()
                                }
                            }
                        }
                        .addOnFailureListener {exception ->
                            showCustomErrorToast(
                                this,
                                getString(R.string.error_, exception.message.toString()),
                                Toast.LENGTH_LONG,
                                this.window
                            )
                            loadingDialog.hide()
                            setViewsListeners()
                        }
                }
            }
            .addOnFailureListener { exception ->
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_LONG,
                    this.window
                )
                setViewsListeners()
            }
    }

    private fun setViewsListeners() {
        goToMemoryGameBtn.setOnClickListener {
            loadingDialog.show()
            if (userHaveTickets && useTicketsDailyMission < 50){
                gamerTickets -= 1
                useTicketsDailyMission += 1
                val data: HashMap<String, Int> = hashMapOf(
                    "gamerTickets" to gamerTickets
                )
                val newData: HashMap<String, Int> = hashMapOf(
                    "usedTickets" to useTicketsDailyMission
                )
                userData.update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        getFriendCodeOwnerUID(friendCode) { friendCodeOwnerUID ->
                            val friendCodeOwnerRef = db
                                .collection("USERS")
                                .document(friendCodeOwnerUID!!)
                            friendCodeOwnerRef
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    snapshot
                                        .getLong("coins")
                                        .let { friendCoinSnapshot ->
                                            val friendsCoin = friendCoinSnapshot?.plus(1)
                                            val addData: HashMap<String, Any> = hashMapOf(
                                                "coins" to friendsCoin!!.toLong()
                                            )
                                            friendCodeOwnerRef.set(addData, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    startActivity(goToMemoryGameIntent)
                                                    finish()
                                                }
                                        }
                                }

                        }
                    }
                    .addOnFailureListener { exception ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            getString(
                                R.string.error_,
                                exception.message.toString()
                            ),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                dailyEvent.update(newData as Map<String, Any>)
            } else {
                loadingDialog.hide()
                showAttentionDialog = DialogStartingGameWithoutTicket(this)
                showAttentionDialog.showAttentionDialog("Memory Game")
            }
        }

        goToMathGameBtn.setOnClickListener {
            loadingDialog.show()
            if (userHaveTickets && useTicketsDailyMission < 50){
                gamerTickets -= 1
                useTicketsDailyMission += 1
                val data: HashMap<String, Int> = hashMapOf(
                    "gamerTickets" to gamerTickets
                )
                val newData: HashMap<String, Int> = hashMapOf(
                    "usedTickets" to useTicketsDailyMission
                )
                userData.update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        getFriendCodeOwnerUID(friendCode) { friendCodeOwnerUID ->
                            val friendCodeOwnerRef = db
                                .collection("USERS")
                                .document(friendCodeOwnerUID!!)
                            friendCodeOwnerRef
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    snapshot
                                        .getLong("coins")
                                        .let { friendCoinSnapshot ->
                                            val friendsCoin = friendCoinSnapshot?.plus(1)
                                            val addData: HashMap<String, Any> = hashMapOf(
                                                "coins" to friendsCoin!!.toLong()
                                            )
                                            friendCodeOwnerRef.set(addData, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    startActivity(goToMathGameIntent)
                                                    finish()
                                                }
                                        }
                                }

                        }
                    }
                    .addOnFailureListener { exception ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            getString(
                                R.string.error_,
                                exception.message.toString()
                            ),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                dailyEvent.update(newData as Map<String, Any>)
            } else {
                loadingDialog.hide()
                showAttentionDialog = DialogStartingGameWithoutTicket(this)
                showAttentionDialog.showAttentionDialog("Math Game")
            }
        }

        goToQuizGameBtn.setOnClickListener {
            loadingDialog.show()
            if (userHaveTickets && useTicketsDailyMission < 50){
                gamerTickets -= 1
                useTicketsDailyMission += 1
                val data: HashMap<String, Int> = hashMapOf(
                    "gamerTickets" to gamerTickets
                )
                val newData: HashMap<String, Int> = hashMapOf(
                    "usedTickets" to useTicketsDailyMission
                )
                userData.update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        getFriendCodeOwnerUID(friendCode) { friendCodeOwnerUID ->
                            val friendCodeOwnerRef = db
                                .collection("USERS")
                                .document(friendCodeOwnerUID!!)
                            friendCodeOwnerRef
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    snapshot
                                        .getLong("coins")
                                        .let { friendCoinSnapshot ->
                                            val friendsCoin = friendCoinSnapshot?.plus(1)
                                            val addData: HashMap<String, Any> = hashMapOf(
                                                "coins" to friendsCoin!!.toLong()
                                            )
                                            friendCodeOwnerRef.set(addData, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    startActivity(goToQuizGameIntent)
                                                    finish()
                                                }
                                        }
                                }

                        }
                    }
                    .addOnFailureListener { exception ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            getString(
                                R.string.error_,
                                exception.message.toString()
                            ),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                dailyEvent.update(newData as Map<String, Any>)
            } else {
                loadingDialog.hide()
                showAttentionDialog = DialogStartingGameWithoutTicket(this)
                showAttentionDialog.showAttentionDialog("Quiz Game")
            }
        }

        helpBtn.setOnClickListener {
            val rulesDialog = RulesDialog(this@GamesCenterActivity)
            val rulesList: List<String> = listOf(
                getString(R.string.on_this_screen_you_can_choose_the_games_you_want_to_play),
                getString(R.string.when_playing_a_game_you_can_earn_up_to_),
                getString(R.string.when_playing_you_can_close_the_screen_not_the_app_and_time_will_pause_and_start_running_again_when_you_return_to_the_app)
            )
            rulesDialog.showEventRulesDialog(rulesList)
        }
    }

    private fun getFriendCodeOwnerUID(friendCode: String, callback: (String?) -> Unit) {
        val usersCollection = db.collection("USERS")
        val query = usersCollection.whereEqualTo("myCode", friendCode).limit(1)

        query.get()
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

    private fun setBackToPreviousScreenListener() {
        val backToHomeActivity = Intent(
            this@GamesCenterActivity,
            HomeActivity::class.java
        )

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
    }

    private fun textsUI() {
        val ts11111: Shader = LinearGradient(
            0f, 12f, 0f, screenName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = ts11111
    }
}
