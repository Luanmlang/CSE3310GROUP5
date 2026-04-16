package com.example.eflashshop.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.R
import com.example.eflashshop.login.AuthStore

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameInput = findViewById<EditText>(R.id.etRegisterUsername)
        val emailInput = findViewById<EditText>(R.id.etRegisterEmail)
        val passwordInput = findViewById<EditText>(R.id.etRegisterPassword)
        val confirmInput = findViewById<EditText>(R.id.etRegisterConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val backButton = findViewById<ImageButton>(R.id.btnBackRegister)

        backButton.setOnClickListener { finish() }

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim().lowercase()
            val password = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val created = AuthStore.registerUser(this, username, email, password)
            if (!created) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Account created. Please login.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
