package com.internshala.foodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.internshala.foodie.R

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed(
            {
                val intent = Intent(this@SplashScreenActivity, UserCredentialsActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000
        )
    }


}
