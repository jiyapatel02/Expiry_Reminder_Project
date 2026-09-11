package com.example.expiry_reminder_project

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val btnLogin =
            findViewById<TextView>(R.id.btnLogin)

        val tvSignUp =
            findViewById<TextView>(R.id.tvSignUp)


        btnLogin.setOnClickListener {

            val email =
                etEmail.text.toString().trim()

            val password =
                etPassword.text.toString().trim()


            if (email.isEmpty()) {

                etEmail.error =
                    "Enter email or username"

                etEmail.requestFocus()

                return@setOnClickListener
            }


            if (password.isEmpty()) {

                etPassword.error =
                    "Enter password"

                etPassword.requestFocus()

                return@setOnClickListener
            }


            getSharedPreferences(
                "UserStorage",
                MODE_PRIVATE
            )
                .edit()
                .putString(
                    "email",
                    email
                )
                .putBoolean(
                    "loggedIn",
                    true
                )
                .apply()


            Toast.makeText(
                this,
                "Login successful",
                Toast.LENGTH_SHORT
            ).show()


            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
        }


        tvSignUp.setOnClickListener {

            Toast.makeText(
                this,
                "Sign Up feature coming next",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}