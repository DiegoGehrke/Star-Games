package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class CustomUpdateDialog(private val context: Context, private val firebaseRemoteConfig: FirebaseRemoteConfig) {

    private lateinit var updateDialog: Dialog
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var updateButton: LinearLayout

    fun buildUpdateDialog() {
        updateDialog = Dialog(context)
        updateDialog.setContentView(R.layout.update_avaliable_dialog_layout)
        updateDialog.setCancelable(false)
        updateDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        title = updateDialog.findViewById(R.id.title_txt)
        message = updateDialog.findViewById(R.id.update_dialog_message)
        updateButton = updateDialog.findViewById(R.id.go_update_app_btn)

        title.text = firebaseRemoteConfig.getString(RemoteConfigUtils.TITLE)
        message.text = firebaseRemoteConfig.getString(RemoteConfigUtils.MESSAGE)
        updateButton.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }
}