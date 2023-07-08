package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView

class CustomDialogRewardDetail(private val context : Context) {
    private lateinit var rewardDetailDialog: Dialog
    private lateinit var closeBtn: ImageView
    private lateinit var dialogTitle: TextView
    private lateinit var itemImage : ImageView

    fun showDialogRewardDetail(image : Int, anchorView: ImageView) {
        rewardDetailDialog = Dialog(context)
        rewardDetailDialog.setContentView(R.layout.custom_dialog_reward_detail)
        rewardDetailDialog.setCancelable(true)
        rewardDetailDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn = rewardDetailDialog.findViewById(R.id.close_btn)
        dialogTitle = rewardDetailDialog.findViewById(R.id.title_txt)
        itemImage = rewardDetailDialog.findViewById(R.id.item_img)
        textUI()
        itemImage.setImageResource(image)
        closeBtn.setOnClickListener {
            rewardDetailDialog.dismiss()
        }
// Get the width and height of the anchor view
        val anchorWidth = anchorView.width
        val anchorHeight = anchorView.height

        // Calculate the x and y coordinates of the dialog
        val x = anchorView.x.toInt() + (anchorWidth / 2) - (rewardDetailDialog.window?.decorView?.measuredWidth?.div(2) ?: 0)
        val y = anchorView.y.toInt() + anchorHeight

        // Set the position of the dialog
        rewardDetailDialog.window?.setGravity(Gravity.TOP or Gravity.START)
        rewardDetailDialog.window?.attributes?.x = x
        rewardDetailDialog.window?.attributes?.y = y
        rewardDetailDialog.show()
    }

    private fun textUI() {
        val textShader: Shader = LinearGradient(
            0f, 0f, 0f, dialogTitle.textSize,
            intArrayOf(
                Color.parseColor("#FFEC8A"),
                Color.parseColor("#998100")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        dialogTitle.paint.shader = textShader

    }
}