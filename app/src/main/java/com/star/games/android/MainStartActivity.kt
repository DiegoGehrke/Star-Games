package com.star.games.android

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.sdk.InitializationListener
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class MainStartActivity : AppCompatActivity(), InitializationListener {

    private var firebaseAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    companion object {
        const val APP_KEY: String = "ADICIONE SUA PRÓPRIA CHAVE DO IRONSOURCE DASHBOARD!"
    }

    private lateinit var initializationStatusTxt: TextView

    private lateinit var firebaseApp: FirebaseApp

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    private lateinit var map: HashMap<String, Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_start)

        firebaseApp = FirebaseApp.initializeApp(this)!!
        initializationStatusTxt = findViewById(R.id.initialization_status_txt)
        checkIfUserHaveTheLatestVersion()
    }

    private fun checkIfUserHaveTheLatestVersion() {
        runOnUiThread {
            initializationStatusTxt.text = getString(R.string.checking_installed_version)
        }
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        map = HashMap()
        map[RemoteConfigUtils.VERSION] = BuildConfig.VERSION_CODE
        firebaseRemoteConfig.setDefaultsAsync(map)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    if (firebaseRemoteConfig.getLong(RemoteConfigUtils.VERSION) > BuildConfig.VERSION_CODE) {
                        val intent = Intent(this, UpdateAppActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        initializeIronSourceSdk()
                    }
                } else {
                    showCustomErrorToast(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                    initializationStatusTxt.text = getString(R.string.an_error_occurred_please_try_again_later)
                }
            }
    }

    private fun initializeIronSourceSdk() {
        runOnUiThread {
            initializationStatusTxt.text = getString(R.string.initializing_components)
        }
        IronSource.setMetaData("Pangle_COPPA", "0")
        IronSource.setMetaData("AdColony_COPPA", "false")
        IronSource.setMetaData("UnityAds_coppa", "false")
        IronSource.setMetaData("InMobi_AgeRestricted", "true")
        IronSource.setMetaData("Vungle_coppa", "false")
        IronSource.init(
            this,
            APP_KEY,
            this@MainStartActivity
        )
        //IntegrationHelper.validateIntegration(this@MainStartActivity)
    }

    override fun onInitializationComplete() {
        checkIfAreUserLoggedIn()
    }

    private fun checkIfAreUserLoggedIn() {
        runOnUiThread {
            initializationStatusTxt.text = getString(R.string.checking_authentication)
        }
        firebaseAuth = FirebaseAuth.getInstance()
            currentUser = firebaseAuth!!.currentUser
            if (currentUser != null) {
                val isEmailVerified = currentUser!!.isEmailVerified
                if (isEmailVerified) {
                    val intent = Intent(this@MainStartActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@MainStartActivity, ConfirmEmailActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this@MainStartActivity, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }
    }
}
