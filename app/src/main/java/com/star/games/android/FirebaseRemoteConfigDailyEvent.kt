package com.star.games.android

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class FirebaseRemoteConfigDailyEvent (val vipLevel: Int){

    private var firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings
        .Builder()
        .setMinimumFetchIntervalInSeconds(1)
        .build()

    private var baseValueRewards: Float = 12.00f
    /*var collectStarsQuestGoal: Long? = null
    var useTicketsQuestGoal: Long? = null*/

    private var map: HashMap<String, Any>

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        map = HashMap()
        map.apply {
            map["DAILY_EVENT_REWARD_VALUE"] = baseValueRewards
            map["DAILY_EVENT_COLLECT_STARS_GOAL"] = 250
            map["DAILY_EVENT_USE_TICKETS_GOAL"] = 50
        }
        firebaseRemoteConfig.setDefaultsAsync(map)
        firebaseRemoteConfig.activate()
    }

    fun calculateGiftRewards(callback: (Float) -> Unit) {
        firebaseRemoteConfig.fetch()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    baseValueRewards = firebaseRemoteConfig.getDouble("DAILY_EVENT_REWARD_VALUE").toFloat()
                    val bonusPercentage = if (vipLevel < 33) {
                        (vipLevel.toFloat() * 3) / 100f
                    } else {
                        (vipLevel.toFloat() * 4) / 100f
                    }
                    val bonusAmount = baseValueRewards * bonusPercentage
                    baseValueRewards += bonusAmount
                } else {
                    baseValueRewards = 12.00f
                    val bonusPercentage = if (vipLevel < 33) {
                        (vipLevel.toFloat() * 3) / 100f
                    } else {
                        (vipLevel.toFloat() * 4) / 100f
                    }
                    val bonusAmount = baseValueRewards * bonusPercentage
                    baseValueRewards += bonusAmount
                }
                callback(baseValueRewards)
            }
    }

   /* fun fetchGoalToCompleteMissions(callback: () -> Unit) {
        firebaseRemoteConfig.fetch()
            .addOnSuccessListener {
                collectStarsQuestGoal = firebaseRemoteConfig.getLong(
                    "DAILY_EVENT_COLLECT_STARS_GOAL"
                )
                useTicketsQuestGoal = firebaseRemoteConfig.getLong(
                    "DAILY_EVENT_USE_TICKETS_GOAL"
                )
                callback.invoke()
            }
            .addOnFailureListener {
                useTicketsQuestGoal = map.get("DAILY_EVENT_USE_TICKETS_GOAL") as Long?
                collectStarsQuestGoal = map.get("DAILY_EVENT_COLLECT_STARS_GOAL") as Long?
                callback.invoke()
            }
    }*/
}