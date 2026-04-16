package com.example.eflashshop.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.eflashshop.R
import com.example.eflashshop.login.AuthStore
import kotlin.math.abs

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthStore.isLoggedIn(this)) {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_landing)
        bindTrimmedLogo()

        val btnLogin = findViewById<Button>(R.id.btnLandingLogin)
        val btnRegister = findViewById<Button>(R.id.btnLandingRegister)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun bindTrimmedLogo() {
        val logoView = findViewById<ImageView>(R.id.ivLandingLogo)
        val original = BitmapFactory.decodeResource(resources, R.drawable.app_logo) ?: return
        logoView.setImageBitmap(trimBorder(original, tolerance = 20))
    }

    private fun trimBorder(bitmap: Bitmap, tolerance: Int): Bitmap {
        if (bitmap.width < 3 || bitmap.height < 3) {
            return bitmap
        }

        val borderColor = bitmap.getPixel(0, 0)
        var top = 0
        var bottom = bitmap.height - 1
        var left = 0
        var right = bitmap.width - 1

        while (top <= bottom && isRowBorder(bitmap, top, borderColor, tolerance)) top++
        while (bottom >= top && isRowBorder(bitmap, bottom, borderColor, tolerance)) bottom--
        while (left <= right && isColumnBorder(bitmap, left, borderColor, tolerance)) left++
        while (right >= left && isColumnBorder(bitmap, right, borderColor, tolerance)) right--

        if (left >= right || top >= bottom) {
            return bitmap
        }

        return Bitmap.createBitmap(bitmap, left, top, right - left + 1, bottom - top + 1)
    }

    private fun isRowBorder(bitmap: Bitmap, y: Int, borderColor: Int, tolerance: Int): Boolean {
        for (x in 0 until bitmap.width) {
            if (!isColorSimilar(bitmap.getPixel(x, y), borderColor, tolerance)) {
                return false
            }
        }
        return true
    }

    private fun isColumnBorder(bitmap: Bitmap, x: Int, borderColor: Int, tolerance: Int): Boolean {
        for (y in 0 until bitmap.height) {
            if (!isColorSimilar(bitmap.getPixel(x, y), borderColor, tolerance)) {
                return false
            }
        }
        return true
    }

    private fun isColorSimilar(a: Int, b: Int, tolerance: Int): Boolean {
        return abs(Color.alpha(a) - Color.alpha(b)) <= tolerance &&
            abs(Color.red(a) - Color.red(b)) <= tolerance &&
            abs(Color.green(a) - Color.green(b)) <= tolerance &&
            abs(Color.blue(a) - Color.blue(b)) <= tolerance
    }
}
