package com.example.expiry_reminder_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        val tvProfileEmail =
            findViewById<TextView>(
                R.id.tvProfileEmail
            )

        val btnLogout =
            findViewById<Button>(R.id.btnLogout)


        btnBack.setOnClickListener {
            finish()
        }


        val preferences =
            getSharedPreferences(
                "UserStorage",
                MODE_PRIVATE
            )

        val email =
            preferences.getString(
                "email",
                "Not available"
            )

        tvProfileEmail.text =
            email


        btnLogout.setOnClickListener {

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}