package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class RewardDialog(private var context : Context) {
    private lateinit  var rewardDialog: Dialog
    private lateinit var closeBtn: LinearLayout
    private lateinit var closeTxt: TextView
    private lateinit var dialogTitle: TextView
    private lateinit var itemImage : ImageView

    fun showRewardDialog(image : Int) {
        rewardDialog = Dialog(context)
        rewardDialog.setContentView(R.layout.reward_dialog_layout)
        rewardDialog.setCancelable(true)
        rewardDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn = rewardDialog.findViewById(R.id.claim_reward_btn)
        closeBtn.setOnClickListener {
            rewardDialog.dismiss()
        }
        closeTxt = rewardDialog.findViewById(R.id.close_txt)
        dialogTitle = rewardDialog.findViewById(R.id.title_txt)
        itemImage = rewardDialog.findViewById(R.id.item_img)
        textUI()
        itemImage.setImageResource(image)
        rewardDialog.show()
    }

    private fun textUI() {
        val textShader: Shader = LinearGradient(
            0f, 0f, 0f, dialogTitle.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFEAA9"), // First color
                Color.parseColor("#4E3B00") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dialogTitle.paint.shader = textShader

        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, closeTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#308A4C"), // First color
                Color.parseColor("#004511") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        closeTxt.paint.shader = textShader1
    }
}