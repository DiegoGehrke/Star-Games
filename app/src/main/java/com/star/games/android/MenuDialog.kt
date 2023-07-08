package com.star.games.android

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView

class MenuDialog(val context : Context) {
    private lateinit var menuDialog: Dialog
    private lateinit var dialogTitle: TextView
    private lateinit var firstOption : ImageView
    private lateinit var secondOption : ImageView
    private lateinit var thirdOption : ImageView
    private lateinit var thirdOptionTxt : TextView
    private lateinit var secondOptionTxt : TextView
    private lateinit var firstOptionTxt : TextView

    fun showMenuDialog(vipLevel: Int) {
        menuDialog = Dialog(context)
        menuDialog.setContentView(R.layout.menu_dialog)
        menuDialog.setCancelable(true)
        menuDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogTitle = menuDialog.findViewById(R.id.title_txt)
        thirdOptionTxt = menuDialog.findViewById(R.id.withdraw_history_txt)
        secondOptionTxt = menuDialog.findViewById(R.id.change_stars_txt)
        firstOptionTxt = menuDialog.findViewById(R.id.referral_program_txt)

        firstOption = menuDialog.findViewById(R.id.first_option)
        secondOption = menuDialog.findViewById(R.id.second_option)
        thirdOption = menuDialog.findViewById(R.id.third_option)

        firstOption.setOnClickListener {
            val intent = Intent(context, ReferralActivity::class.java)
            context.startActivity(intent)
            menuDialog.dismiss()
        }

        secondOption.setOnClickListener {
                val intent = Intent(context, ExchangeStarsActivity::class.java)
                intent.putExtra("vipLevel", vipLevel)
                context.startActivity(intent)
                menuDialog.dismiss()
                if (context is Activity) {
                    context.finish()
                }
        }

        thirdOption.setOnClickListener {
            val intent = Intent(context, UserWithdrawHistoryActivity::class.java)
            context.startActivity(intent)
            menuDialog.dismiss()
            if (context is Activity) {
                context.finish()
            }
        }

        textsUI()
        menuDialog.show()
    }

    private fun textsUI() {
        val firstShader: Shader = LinearGradient(
            0f, 8f, 0f, firstOptionTxt.textSize,
            intArrayOf(
                Color.parseColor("#7484AA"),
                Color.parseColor("#BCD0FF")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        firstOptionTxt.paint.shader = firstShader

        val secondShader: Shader = LinearGradient(
            0f, 8f, 0f, secondOptionTxt.textSize,
            intArrayOf(
                Color.parseColor("#7484AA"),
                Color.parseColor("#BCD0FF")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        secondOptionTxt.paint.shader = secondShader

        val thirdShadow: Shader = LinearGradient(
            0f, 8f, 0f, thirdOptionTxt.textSize,
            intArrayOf(
                Color.parseColor("#7484AA"),
                Color.parseColor("#BCD0FF")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        thirdOptionTxt.paint.shader = thirdShadow

        val titleShader: Shader = LinearGradient(
            0f, 8f, 0f, dialogTitle.textSize,
            intArrayOf(
                Color.parseColor("#9CB1E1"),
                Color.parseColor("#ACBFEC")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dialogTitle.paint.shader = titleShader
    }
}