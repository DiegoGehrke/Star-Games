package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class RewardDialog(private var context : Context) {
    private lateinit  var rewardDialog: Dialog
    private lateinit var closeBtn: LinearLayout
    private lateinit var closeTxt: TextView
    private lateinit var dialogTitle: TextView
    private lateinit var itemImage : ImageView
    private lateinit var itemAmount : TextView

    fun showRewardDialog(rewardImg: Int, rewardAmount: Float) {
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
        itemAmount = rewardDialog.findViewById(R.id.item_reward_amount)
        textUI()
        itemImage.setImageResource(rewardImg)
        itemAmount.text = rewardAmount.toString()
        rewardDialog.show()
    }

    private fun textUI() {
        val textShader: Shader = LinearGradient(
            0f, 0f, 0f, dialogTitle.textSize,
            intArrayOf(
                Color.parseColor("#FFF3C9"),
                Color.parseColor("#BFA239")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dialogTitle.paint.shader = textShader

        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, closeTxt.textSize,
            intArrayOf(
                Color.parseColor("#308A4C"),
                Color.parseColor("#004511")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        closeTxt.paint.shader = textShader1
    }
}