package com.example.expiry_reminder_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val cardAdd = findViewById<CardView>(R.id.cardAdd)
        val cardDocuments = findViewById<CardView>(R.id.cardDocuments)
        val cardReminders = findViewById<CardView>(R.id.cardReminders)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)
        val btnViewReminders = findViewById<Button>(R.id.btnViewReminders)

        cardAdd.setOnClickListener {

            val intent = Intent(this, AddDocumentActivity::class.java)
            startActivity(intent)
        }

        cardDocuments.setOnClickListener {

            val intent = Intent(this, DocumentsActivity::class.java)
            startActivity(intent)
        }

        cardReminders.setOnClickListener {

            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        }

        cardSettings.setOnClickListener {

            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnViewReminders.setOnClickListener {

            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        }
    }
}