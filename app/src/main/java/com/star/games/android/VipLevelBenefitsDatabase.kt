package com.star.games.android

import android.content.Context
import android.widget.ImageView
import android.widget.TextView

class VipLevelBenefitsDatabase(
    val context: Context,
    var starGainPercentage: TextView,
    var showingBenefitsFromLevel: TextView,
    var vipLevel: Int,
    var userCurrentVipPoints: Int,
    var showGoalToNextVipLevel: TextView,
    var iconUnlockedOrLockedVip: ImageView
) {

    init {
        if (vipLevel == 0) {
            iconUnlockedOrLockedVip.setImageResource(R.drawable.vip_locked)
        }
        levelOneUI()
    }

    fun levelOneUI(currentPosition: Int? = 0) {
        when (3 * (currentPosition!! + 1)) {
            99 -> starGainPercentage.text = context.getString(
                R.string.star_gain_percentage,
                100
            )

            else -> starGainPercentage.text = context.getString(
                R.string.star_gain_percentage,
                3 * (currentPosition + 1)
            )
        }

        showingBenefitsFromLevel.text = context.getString(R.string.vip_benefits,
            currentPosition + 1
        )

        showGoalToNextVipLevel.text = context.getString(
            R.string.barra,
            userCurrentVipPoints,
            ((vipLevel + 1) * 500)
        )

        if (vipLevel >= (currentPosition + 1)) {
            iconUnlockedOrLockedVip.setImageResource(R.drawable.vip_unlocked_icon)
        } else {
            iconUnlockedOrLockedVip.setImageResource(R.drawable.vip_locked)
        }
    }
}