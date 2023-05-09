package com.star.games.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ConfigurationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurations)

        val goToHelpSupportActivity : ImageView = findViewById(R.id.help_support_btn)
        goToHelpSupportActivity.setOnClickListener {
            val intent = Intent(this, HelpSupportActivity::class.java)
            startActivity(intent)
            finish()
        }
        val goToManageAccountActivity : ImageView = findViewById(R.id.manage_account_btn)
        goToManageAccountActivity.setOnClickListener {
            val intent = Intent(this, ManageAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}