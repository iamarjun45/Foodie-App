package com.internshala.foodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.internshala.foodie.R

class OrderCompletionActivity : AppCompatActivity() {

    lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_completion)

        btn = findViewById(R.id.orderplacedbtn)
        Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
        btn.setOnClickListener()
        {
            val intent = Intent(this, HomeActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }


    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}