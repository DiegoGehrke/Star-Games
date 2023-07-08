@file:Suppress("DEPRECATION")

package com.star.games.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.properties.Delegates


@Suppress("OVERRIDE_DEPRECATION")
class HomeActivity : AppCompatActivity(), RewardedVideoListener {

    private lateinit var userStarsTxt: TextView
    private lateinit var gamesTxt: TextView
    private lateinit var categoriesTxt: TextView
    private lateinit var dailyEntriesTxt: TextView
    private lateinit var access1: TextView
    private lateinit var access2: TextView
    private lateinit var specialCategoryTxt: TextView
    private lateinit var specialDailyCountTxt: TextView
    private lateinit var gamerTicketsAmount: TextView
    private lateinit var username: TextView
    private lateinit var fullName: TextView
    private lateinit var vipLevelTxt: TextView
    private lateinit var dailyEventNameTxt: TextView

    private lateinit var dailyEvent: ImageView
    private lateinit var getMoreTickets: ImageView
    private lateinit var openMenu: ImageView
    private lateinit var openConfigurationsActivity: ImageView
    private lateinit var getMoreVip: ImageView
    private lateinit var surveyBtn: ImageView

    private lateinit var accessPrincipalGamesButton: LinearLayout
    private lateinit var accessSpecialGamesButton: LinearLayout

    private var confirmBackInt: Int = 0
    private var vipLevel: Int = 0
    private var getUserVipPoints: Long = 0
    private var getDailyTicketsGameLimit: Long = 0
    private var coin: Float = 0.00F
    private var getUserGamerTickets: Long = 0
    private var lastClickTime: Long = 0
    private var currentTime by Delegates.notNull<Long>()
    private var coolDownRemaining by Delegates.notNull<Long>()

    private val backTimer = Timer()

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEventRef = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var menuDialog: MenuDialog
    private lateinit var cooldownDialog: CooldownDialog

    private var successfullyRewarded: Boolean = false
    private var isPermissionRequested: Boolean = false
    private var isDailyEventActive: Boolean = false

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var cooldownSharedPreferences: SharedPreferences

    private lateinit var background: ConstraintLayout

    private var url: String = ""

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    private lateinit var mapFbRemoteConfigDefaultValues: HashMap<String, Any>

    private var dailyAdCap: Long = 50

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cooldownSharedPreferences = getSharedPreferences("getTicketCooldown", Context.MODE_PRIVATE)
        lastClickTime = cooldownSharedPreferences.getLong("lastClickTime", 0)

        initializeViews()
        loadingDialog.show()
        fetchUserData()
        fetchFirebaseRemoteConfigData()
        setViewsListeners()
        abbreviateUserName()
        backPressedListener()
        textsColorUI()
        if (!isPermissionRequested) {
            askPermissions()
            isPermissionRequested = true
        }

        if (!isInternetAvailable()) {
            val goToNoNetActivity = Intent(this, NoInternetConnectionActivity::class.java)
            startActivity(goToNoNetActivity)
            finish()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Checks connectivity on Android versions before Android 10 (Q)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        // Checks connectivity on Android versions from Android 10 (Q)
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }

        return false
    }

    private fun fetchFirebaseRemoteConfigData() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mapFbRemoteConfigDefaultValues = HashMap()
        mapFbRemoteConfigDefaultValues["SURVEY_LINK_URL"] = url
        mapFbRemoteConfigDefaultValues["IS_DAILY_EVENT_ACTIVE"] = isDailyEventActive
        mapFbRemoteConfigDefaultValues["DAILY_AD_CAP"] = dailyAdCap
        firebaseRemoteConfig.setDefaultsAsync(mapFbRemoteConfigDefaultValues)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    url = firebaseRemoteConfig.getString("SURVEY_LINK_URL")
                    isDailyEventActive = firebaseRemoteConfig.getBoolean("IS_DAILY_EVENT_ACTIVE")
                    dailyAdCap = firebaseRemoteConfig.getLong("DAILY_AD_CAP")
                    updateUI()
                } else {
                    showCustomErrorToast(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val notificationPermissionGranted = permissions[if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            } else {

            }
        }] ?: false

       if (locationPermissionGranted && notificationPermissionGranted) {
            // Both permissions were granted
        } else {
            // At least one of the permissions was denied
        }
    }

    private fun askPermissions() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            } else {
                "" // Default empty value for notification in APIs less than "M"
            }
        }

        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, locationPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(locationPermission)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, notificationPermission) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(notificationPermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            val permissionsArray = permissionsToRequest.toTypedArray()
            requestPermissionLauncher.launch(permissionsArray)
        } else {
            // All permissions have already been granted
        }
    }

    private fun initializeViews() {
        username = findViewById(R.id.username)
        fullName = findViewById(R.id.username_txt)
        access1 = findViewById(R.id.access1_txt)
        access2 = findViewById(R.id.access2_txt)
        userStarsTxt = findViewById(R.id.stars_txt)
        gamesTxt = findViewById(R.id.games_txt)
        categoriesTxt = findViewById(R.id.categories)
        dailyEntriesTxt = findViewById(R.id.daily_entries)
        specialCategoryTxt = findViewById(R.id.special_category)
        specialDailyCountTxt = findViewById(R.id.special_daily_entries)
        vipLevelTxt = findViewById(R.id.vip_level_txt)
        gamerTicketsAmount = findViewById(R.id.tickets_amount)
        surveyBtn = findViewById(R.id.survey_btn)
        dailyEventNameTxt = findViewById(R.id.daily_event_name)

        openMenu = findViewById(R.id.menu_btn)
        openConfigurationsActivity = findViewById(R.id.configs_btn)
        dailyEvent = findViewById(R.id.daily_event)
        getMoreTickets = findViewById(R.id.get_more_tickets)
        accessSpecialGamesButton = findViewById(R.id.access_special_btn)
        accessPrincipalGamesButton = findViewById(R.id.access_principal_games_btn)
        getMoreVip = findViewById(R.id.get_more_vip)
        openMenu = findViewById(R.id.menu_btn)
        background = findViewById(R.id.background)

        loadingDialog = LoadingDialog(this)
        menuDialog = MenuDialog(this)
        cooldownDialog = CooldownDialog(this, false)
        IronSource.setRewardedVideoListener(this@HomeActivity)
    }

    private fun fetchUserData() {
        userData
            .get()
            .addOnSuccessListener { document ->
                coin = document.getField("coins")!!
                getUserVipPoints = document.getLong("vipPoints")!!
                vipLevel = calculateVipLevel(getUserVipPoints)
                getUserGamerTickets = document.getLong("gamerTickets")!!
                updateUI()
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_,
                        exception.message.toString()
                    ),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }

        dailyEventRef
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    if (document.contains("usedTickets")) {
                        getDailyTicketsGameLimit = document.getLong("usedTickets")!!
                       updateUI()
                    }
                }
            }
            .addOnFailureListener {exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_,
                        exception.message.toString()
                    ),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
    }

    private fun calculateVipLevel(vipPoints: Long): Int {
        return (vipPoints / 500).toInt()
    }

    private fun setViewsListeners() {
        openMenu.setOnClickListener {
            menuDialog.showMenuDialog(vipLevel)
        }

        openConfigurationsActivity.setOnClickListener {
            val openConfigsActivity = Intent(this, ConfigurationsActivity::class.java)
            startActivity(openConfigsActivity)
        }

        accessPrincipalGamesButton.setOnClickListener {
            val intent = Intent(this, GamesCenterActivity::class.java)
            startActivity(intent)
            finish()
        }

        accessSpecialGamesButton.setOnClickListener {
            showCustomSuccessToast(
                this,
                getString(R.string.coming_soon),
                Toast.LENGTH_LONG,
                this.window
            )
        }

        getMoreVip.setOnClickListener {
            val intent = Intent(this, VipActivity::class.java)
            startActivity(intent)
            finish()
        }

        getMoreTickets.setOnClickListener {
            currentTime = System.currentTimeMillis()
            lastClickTime = cooldownSharedPreferences.getLong("lastClickTime", 0)
            coolDownRemaining = lastClickTime - currentTime
            if (coolDownRemaining > 0) {
                cooldownDialog.showCooldownDialog(coolDownRemaining)
            } else {
                val limit = dailyAdCap - getUserGamerTickets
                if (limit <= 0) {
                    showCustomErrorToast(
                        this,
                        getString(R.string.daily_limit_reached),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
                else {
                    if (IronSource.isRewardedVideoAvailable()) {
                        val newClickTime = System.currentTimeMillis() + 1 * 70 * 1000

                        with(cooldownSharedPreferences.edit()) {
                            putLong("lastClickTime", newClickTime)
                            apply()
                        }
                        IronSource.showRewardedVideo()
                    }
                    else {
                        showCustomErrorToast(
                            this,
                            getString(R.string.please_try_again_in_a_few_seconds),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                }
            }
        }

        dailyEvent.setOnClickListener {
            val intent = Intent(this, DailyEventActivity::class.java)
            startActivity(intent)
            finish()
        }

        surveyBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                val goToAppWebView = Intent(this, WebViewActivity::class.java)
                goToAppWebView.putExtra("url", url)
                startActivity(goToAppWebView)
            }
        }
    }

    private fun updateUI() {
        loadingDialog.hide()
        vipLevelTxt.text = vipLevel.toString()
        val decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        decimalFormatSymbols.decimalSeparator = ','
        decimalFormatSymbols.groupingSeparator = '.'
        val decimalFormat = DecimalFormat("#,##0.00", decimalFormatSymbols)
        val formattedNumber = decimalFormat.format(coin.toDouble())
        userStarsTxt.text = formattedNumber
        gamerTicketsAmount.text = "$getUserGamerTickets"
            dailyEntriesTxt.text = getString(
                R.string.main_daily_entries,
                getDailyTicketsGameLimit,
                dailyAdCap
            )
        specialDailyCountTxt.text = getString(
            R.string.special_daily_entries
        )

        if (url != "") {
            surveyBtn.visibility = View.VISIBLE
        } else {
            surveyBtn.visibility = View.GONE
        }
        if (isDailyEventActive) {
            dailyEvent.visibility = View.VISIBLE
            dailyEventNameTxt.visibility = View.VISIBLE
        } else {
            dailyEvent.visibility = View.GONE
            dailyEventNameTxt.visibility = View.GONE
        }

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarHomeColor)
    }

    @SuppressLint("SetTextI18n")
    private fun abbreviateUserName() {
        sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val firstName: String = sharedPrefs.getString("FirstName", "")?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }!!
        val lastName: String = sharedPrefs.getString("LastName", "")?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }!!

        val name: String = firstName.trim()
        val firstLetter: String = name.getOrNull(0)?.toString() ?: ""

        username.text = firstLetter

        val lastNamePieces: List<String> = lastName.split(" ")
        val lastInitial: String = lastNamePieces.getOrNull(0)?.getOrNull(0)?.toString() ?: ""

        fullName.text = "$firstName $lastInitial."
    }

    private fun backPressedListener() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmBackInt ++
                if (confirmBackInt == 1){
                    showCustomSuccessToast(
                        this@HomeActivity,
                        getString(R.string.touch_again_to_close_the_app),
                        Toast.LENGTH_LONG,
                        this@HomeActivity.window
                    )
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
    }

    private fun textsColorUI(){
        val textShader1: Shader = LinearGradient(
            0f, 12f, 0f, userStarsTxt.textSize,
            intArrayOf(
                Color.parseColor("#EED479"),
                Color.parseColor("#B6941D")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        userStarsTxt.paint.shader = textShader1

        val textShader: Shader = LinearGradient(
            0f, 12f, 0f, gamesTxt.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        gamesTxt.paint.shader = textShader

        val textShader2: Shader = LinearGradient(
            0f, 0f, 0f, categoriesTxt.textSize,
            intArrayOf(
                Color.parseColor("#8388A5"),
                Color.parseColor("#414455")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        categoriesTxt.paint.shader = textShader2

        val textShader3: Shader = LinearGradient(
            0f, 0f, 0f, dailyEntriesTxt.textSize,
            intArrayOf(
                Color.parseColor("#575B72"),
                Color.parseColor("#4D5062")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dailyEntriesTxt.paint.shader = textShader3

        val textShader4: Shader = LinearGradient(
            0f, 12f, 0f, access1.textSize,
            intArrayOf(
                Color.parseColor("#66422A"),
                Color.parseColor("#291000")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        access1.paint.shader = textShader4

        val textShader5: Shader = LinearGradient(
            0f, 0f, 0f, specialCategoryTxt.textSize,
            intArrayOf(
                Color.parseColor("#8388A5"),
                Color.parseColor("#414455")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        specialCategoryTxt.paint.shader = textShader5

        val textShader6: Shader = LinearGradient(
            0f, 0f, 0f, specialDailyCountTxt.textSize,
            intArrayOf(
                Color.parseColor("#575B72"),
                Color.parseColor("#4D5062")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        specialDailyCountTxt.paint.shader = textShader6

        val textShader7: Shader = LinearGradient(
            0f, 12f, 0f, access2.textSize,
            intArrayOf(
                Color.parseColor("#66422A"),
                Color.parseColor("#291000")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        access2.paint.shader = textShader7

        val usernameFirstLetterShader: Shader = LinearGradient(
            0f, 12f, 0f, username.textSize,
            intArrayOf(
                Color.parseColor("#FFE693"),
                Color.parseColor("#FFDB63")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        username.paint.shader = usernameFirstLetterShader
    }

    override fun onRewardedVideoAdOpened() {

    }

    override fun onRewardedVideoAdClosed() {
        if (successfullyRewarded) {
            successfullyRewarded = false
            val rewardDialog = RewardDialog(this)
            rewardDialog.showRewardDialog(R.drawable.ticket_reward_one_item, 1F)
        }
    }

    override fun onRewardedVideoAvailabilityChanged(p0: Boolean) {

    }

    override fun onRewardedVideoAdStarted() {
    }

    override fun onRewardedVideoAdEnded() {

    }

    override fun onRewardedVideoAdRewarded(p0: Placement?) {
        val addTickets: Long = getUserGamerTickets + 1
        val data: HashMap<String, Any> = hashMapOf(
            "gamerTickets" to addTickets
        )
        userData.update(data)
            .addOnSuccessListener {
                userData.addSnapshotListener { value, _ ->
                    val getDocumentFieldValue: Long = value!!.getLong("gamerTickets")!!
                    getUserGamerTickets = value.getLong("gamerTickets")!!
                    gamerTicketsAmount.text = getDocumentFieldValue.toString()
                }
            }
        successfullyRewarded = true
    }

    override fun onRewardedVideoAdShowFailed(p0: IronSourceError?) {
        showCustomErrorToast(
            this,
            getString(R.string.error_, p0!!.errorMessage.toString()),
            Toast.LENGTH_LONG,
            this.window
        )
    }

    override fun onRewardedVideoAdClicked(p0: Placement?) {

    }
}