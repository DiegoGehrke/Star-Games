package com.star.games.android

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.BannerView.IListener
import com.unity3d.services.banners.UnityBanners
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.properties.Delegates


class HomeActivity : AppCompatActivity(), IUnityAdsInitializationListener {

    private lateinit var userStarsTxt : TextView
    private lateinit var gamesTxt : TextView
    private lateinit var categoriesTxt : TextView
    private lateinit var dailyEntriesTxt : TextView
    private lateinit var access1 : TextView
    private lateinit var access2 : TextView
    private lateinit var specialCategoryTxt : TextView
    private lateinit var specialDailyCountTxt : TextView
    private lateinit var gamerTicketsAmount : TextView
    private lateinit var dailyEvent : ImageView
    private var confirmBackInt : Int = 0
    private val backTimer = Timer()
    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    lateinit var username : TextView
    private lateinit var fullName : TextView
    private val dailyLimit = db.collection("DAILY GAME LIMIT").document(uid)
    private val userData = db.collection("USERS").document(uid)
    private var coin by Delegates.notNull<Long>()
    private lateinit var loadingDialog: LoadingDialog
    private var canAccessPrincipalGames : Boolean = false
    private var canAccessEspecialGames : Boolean = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmBackInt ++
                if (confirmBackInt == 1){
                    Toast.makeText(applicationContext, "Clique novamente para fechar o app", Toast.LENGTH_LONG).show()
                }
                else if (confirmBackInt > 1){
                    finishAndRemoveTask()
                }
                backTimer.schedule(object : TimerTask() {
                    override fun run() {
                        confirmBackInt = 0
                    }
                }, 3000)
            }
        })
        val menuDialog = MenuDialog(this)
        val openMenu : ImageView = findViewById(R.id.menu_btn)
        openMenu.setOnClickListener {
            menuDialog.showMenuDialog()
        }
        val openConfigurationsActivity : ImageView = findViewById(R.id.configs_btn)
        openConfigurationsActivity.setOnClickListener {
            val abrirConfigs = Intent(this, ConfigurationsActivity::class.java)
            startActivity(abrirConfigs)
        }
        username = findViewById(R.id.username)
        fullName = findViewById(R.id.username_txt)
        /*val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val firstName = sharedPrefs.getString("FirstName", "")
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val lastName = sharedPrefs.getString("LastName", "")
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val concatNames = "$firstName $lastName"
        val name = firstName!!.trim()
        val getFirstNameLetter = name.substring(0, 1)
        username.text = getFirstNameLetter
        val pieces = concatNames.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val getLastName = pieces[1]
        val firstLastNameLetter = getLastName[0]
        fullName.text = "$firstName $firstLastNameLetter."*/

        userData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    coin = document.getLong("coins")!!
                    userStarsTxt.text = "$coin"
                    loadingDialog.hide()
                    if (document.contains("gamerTickets")) {
                        val getUserGamerTickets = document.getLong("gamerTickets")!!
                        gamerTicketsAmount.text = "$getUserGamerTickets"
                    } else {
                        gamerTicketsAmount.text = "0"
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        dailyLimit.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    if (document.contains("principalDailyLimit")) {
                        val getPrincipalDailyLimit : Long = document.getLong("principalDailyLimit")!!
                        canAccessPrincipalGames = getPrincipalDailyLimit < 15
                        if (getPrincipalDailyLimit.toInt() >= 0){
                            dailyEntriesTxt.text = getString(R.string.main_daily_entries, getPrincipalDailyLimit)
                        }
                    } else {
                        dailyEntriesTxt.text = getString(R.string.main_daily_entries, 0)
                        canAccessPrincipalGames = false
                    }
                        if (document.contains("specialDailyLimit")) {
                            val getSpecialDailyLimit: Long = document.getLong("specialDailyLimit")!!
                            canAccessEspecialGames = getSpecialDailyLimit < 15
                            if (getSpecialDailyLimit.toInt() > 0) {
                                specialDailyCountTxt.text =
                                    getString(R.string.special_daily_entries, getSpecialDailyLimit)
                            }
                        } else {
                            specialDailyCountTxt.text = getString(R.string.main_daily_entries, 0)
                            canAccessEspecialGames = false
                        }
                }
                else {
                    var mapes = hashMapOf(
                        "specialDailyLimit" to 0,
                        "principalDailyLimit" to 0
                    )
                    dailyLimit.set(mapes, SetOptions.merge())
                        .addOnSuccessListener {
                            dailyLimit.addSnapshotListener { snapshot, _ ->
                                if (snapshot!!.exists()){
                                    val map = snapshot.data
                                    val getSpecialDailyLimit = map?.get("specialDailyLimit") as Long
                                    val getPrincipalDailyLimit = map.get("principalDailyLimit") as Long
                                    dailyEntriesTxt.text = getString(R.string.main_daily_entries, getPrincipalDailyLimit)
                                    specialDailyCountTxt.text = getString(R.string.special_daily_entries, getSpecialDailyLimit)
                                }
                            }
                        }
                }
            }
            .addOnFailureListener {exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                canAccessEspecialGames  = false
                canAccessPrincipalGames = false
            }

        userStarsTxt = findViewById(R.id.stars_txt)
        gamesTxt = findViewById(R.id.games_txt)
        categoriesTxt = findViewById(R.id.categories)
        dailyEntriesTxt = findViewById(R.id.daily_entries)
        access1 = findViewById(R.id.access1_txt)
        access2 = findViewById(R.id.access2_txt)
        specialCategoryTxt = findViewById(R.id.special_category)
        specialDailyCountTxt = findViewById(R.id.special_daily_entries)
        dailyEvent = findViewById(R.id.daily_event)
        gamerTicketsAmount = findViewById(R.id.tickets_amount)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        dailyEvent.setOnClickListener {
            val intent = Intent(this, DailyEventActivity::class.java)
            startActivity(intent)
            finish()
        }

        val accessPrincipalGamesButton : LinearLayout = findViewById(R.id.access_principal_games_btn)
        accessPrincipalGamesButton.setOnClickListener {
            if (canAccessPrincipalGames){
                val intent = Intent(this, GamesCenterActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "CHANCES DIÁRIAS ESGOTADAS", Toast.LENGTH_LONG).show()
            }

        }

        val accessSpecialGamesButton : LinearLayout = findViewById(R.id.access_special_btn)
        accessSpecialGamesButton.setOnClickListener {
            if (canAccessEspecialGames){
                val intent = Intent(this, GamesCenterActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "CHANCES DIÁRIAS ESGOTADAS", Toast.LENGTH_LONG).show()
            }

        }

        textsColorUI()

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarHomeColor)


    }

    private fun textsColorUI(){
        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, userStarsTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#EED479"), // First color
                Color.parseColor("#B6941D") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        userStarsTxt.paint.shader = textShader1

        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, gamesTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        gamesTxt.paint.shader = textShader

        val textShader2: Shader = LinearGradient(
            0f, 0f, 0f, categoriesTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#8388A5"), // First color
                Color.parseColor("#414455") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        categoriesTxt.paint.shader = textShader2

        val textShader3: Shader = LinearGradient(
            0f, 0f, 0f, dailyEntriesTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#575B72"), // First color
                Color.parseColor("#4D5062") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dailyEntriesTxt.paint.shader = textShader3

        val textShader4: Shader = LinearGradient(
            0f, 24f, 0f, access1.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#66422A"), // First color
                Color.parseColor("#291000") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        access1.paint.shader = textShader4

        val textShader5: Shader = LinearGradient(
            0f, 0f, 0f, specialCategoryTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#8388A5"), // First color
                Color.parseColor("#414455") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        specialCategoryTxt.paint.shader = textShader5

        val textShader6: Shader = LinearGradient(
            0f, 0f, 0f, specialDailyCountTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#575B72"), // First color
                Color.parseColor("#4D5062") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        specialDailyCountTxt.paint.shader = textShader6

        val textShader7: Shader = LinearGradient(
            0f, 24f, 0f, access2.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#66422A"), // First color
                Color.parseColor("#291000") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        access2.paint.shader = textShader7

        val usernameFirstLetterShader: Shader = LinearGradient(
            0f, 12f, 0f, username.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#F6DD84"), // First color
                Color.parseColor("#B6941C") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        username.paint.shader = usernameFirstLetterShader


    }

    override fun onInitializationComplete() {
        TODO("Not yet implemented")
    }

    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        TODO("Not yet implemented")
    }


}