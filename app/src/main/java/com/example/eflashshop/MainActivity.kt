package com.example.eflashshop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Go straight to Cart screen
        val intent = Intent(this, CartActivity :: class.java)
        startActivity(intent)
        finish()
    }
}