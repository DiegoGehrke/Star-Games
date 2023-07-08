package com.star.games.android

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class NoInternetConnectionActivity : AppCompatActivity() {

    private lateinit var checkNetConnectionBtn: ImageView

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet_connection)

        initViews()
        setViewsOnClickListener()
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

    private fun setViewsOnClickListener() {
        checkNetConnectionBtn.setOnClickListener {
            loadingDialog.show()
            if (isInternetAvailable()) {
                loadingDialog.hide()
                val goToNoNetActivity = Intent(this, HomeActivity::class.java)
                startActivity(goToNoNetActivity)
                finish()
            } else {
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.no_internet_connection),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
        }
    }

    private fun initViews() {
        checkNetConnectionBtn = findViewById(R.id.check_internet_button)
        loadingDialog = LoadingDialog(this)
    }
}