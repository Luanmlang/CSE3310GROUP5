package com.example.eflashshop.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.R
import com.example.eflashshop.login.AuthStore

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.etUsername)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerLink = findViewById<TextView>(R.id.tvGoRegister)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (AuthStore.loginUser(this, username, password)) {
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}