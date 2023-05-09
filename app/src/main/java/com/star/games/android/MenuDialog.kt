package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

class MenuDialog(private var context : Context) {
    private lateinit var menuDialog: Dialog
    private lateinit var referralProgramTxt: TextView
    private lateinit var dialogTitle: TextView
    private lateinit var firstOption : ImageView

    fun showMenuDialog() {
        menuDialog = Dialog(context)
        menuDialog.setContentView(R.layout.menu_dialog)
        menuDialog.setCancelable(true)
        menuDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        referralProgramTxt = menuDialog.findViewById(R.id.referral_program_txt)
        dialogTitle = menuDialog.findViewById(R.id.title_txt)
        firstOption = menuDialog.findViewById(R.id.first_option)
        firstOption.setOnClickListener {
            val intent = Intent(context, ReferralActivity::class.java)
            context.startActivity(intent)
            menuDialog.dismiss()
        }

        //textUI()
        menuDialog.show()
    }
}