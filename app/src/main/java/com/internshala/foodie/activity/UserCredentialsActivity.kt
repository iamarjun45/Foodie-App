//9ca84225702ca4
package com.internshala.foodie.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.internshala.foodie.R
import com.internshala.foodie.fragment.LoginFragment
import org.json.JSONObject

class UserCredentialsActivity : AppCompatActivity() {


    lateinit var sharedpreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedpreference =
            getSharedPreferences(getString(R.string.shared_preferences_save), Context.MODE_PRIVATE)

        val isLoggedin = sharedpreference.getBoolean("isLoggedIn", false)
        if (isLoggedin) {

            openactivity()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.usercredentialsframe, LoginFragment()).commit()
        }

        setContentView(R.layout.activity_user_credentials)


    }


    fun openactivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.usercredentialsframe)
        when (frag) {
            !is LoginFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.usercredentialsframe, LoginFragment()).commit()

            }

            else -> finishAffinity()
        }

    }

}
