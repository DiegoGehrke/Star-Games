package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class HelpSupportActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        sharedPreferences = getSharedPreferences("getHelpRequest", MODE_PRIVATE)

        val goToSupportActivity : ImageView = findViewById(R.id.go_to_support_activity)
        goToSupportActivity.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("helpType", "support")
                apply()
            }
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            finish()
        }
        val goToAccountSection : ImageView = findViewById(R.id.go_to_account_section)
        goToAccountSection.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("helpType", "account")
                apply()
            }
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            finish()
        }
        val goToEventsSection : ImageView = findViewById(R.id.go_to_events_section)
        goToEventsSection.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("helpType", "events")
                apply()
            }
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            finish()
        }
        val goToCommonQuestsSection : ImageView = findViewById(R.id.go_to_common_quests_section)
        goToCommonQuestsSection.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("helpType", "commonQuests")
                apply()
            }
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}