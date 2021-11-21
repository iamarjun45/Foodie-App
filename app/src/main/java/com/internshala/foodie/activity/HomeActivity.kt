package com.internshala.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.internshala.foodie.R
import com.internshala.foodie.fragment.*
import org.json.JSONException
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    lateinit var navigationview: NavigationView
    lateinit var coordinatorView: CoordinatorLayout
    lateinit var drawerlayout: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar1: androidx.appcompat.widget.Toolbar
    var previousmenuitem: MenuItem? = null
    lateinit var drawername: TextView
    lateinit var drawerno: TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigationview = findViewById(R.id.navigationview)
        coordinatorView = findViewById(R.id.coordinatorlayout)
        drawerlayout = findViewById(R.id.drawerlayout)
        toolbar1 = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.framelayout)

        val headerContent = navigationview.getHeaderView(0)
        setUpToolbar()
        toolbar1.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_sortitems))

        openhomeframent()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerlayout, R.string.open_drawer, R.string.close_drawer


        )

        drawerlayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        drawername = headerContent.findViewById(R.id.draweruser)
        drawerno = headerContent.findViewById(R.id.drawerno)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences_save), Context.MODE_PRIVATE)


        drawername.text = sharedPreferences.getString("name", "Intershala")
        drawerno.text = "+91 " + sharedPreferences.getString("phoneno", "1115555555")


        navigationview.setNavigationItemSelectedListener {


            if (it.itemId == R.id.logoutmenu) {
                /*if logout is clicked, do not check it since if the user presses "no" the fragment which was open
                perviously should remain checked*/
                it.isCheckable = false
                it.isChecked = false


            } else {
                if (previousmenuitem != null) {

                    previousmenuitem?.isChecked = false

                }
                it.isCheckable = true
                it.isChecked = true
                previousmenuitem = it
            }


            when (it.itemId) {
                R.id.homemenu -> {

                    openhomeframent()
                }
                R.id.favmenu -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FavouritesFragment()).commit()
                    supportActionBar?.title = "Favourites"

                }
                R.id.orderhistmenu -> {


                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"

                }
                R.id.profilemenu -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, ProfileFragment()).commit()
                    supportActionBar?.title = "Profile"

                }
                R.id.faqmenu -> {


                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FaqFragment()).commit()
                    supportActionBar?.title = "Frequently Asked Questions"

                }
                R.id.logoutmenu -> {


                    val Dialog = androidx.appcompat.app.AlertDialog.Builder(this)

                    Dialog.setTitle("Confirmation")
                    Dialog.setMessage("Do you really want to log out??")
                    Dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

                        val intent = Intent(this, UserCredentialsActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                    Dialog.setNegativeButton("No") { text, listener ->


                    }
                    Dialog.setCancelable(false)
                    Dialog.create()
                    Dialog.show()


                }
            }
            drawerlayout.closeDrawer(GravityCompat.START)

            return@setNavigationItemSelectedListener true

        }


    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)



        when (frag) {
            !is HomeFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, HomeFragment()).commit()
                supportActionBar?.title = "Home"
                navigationview.setCheckedItem(R.id.homemenu)
            }

            else -> {
                finishAffinity()
            }
        }
    }

    fun openhomeframent() {
        supportFragmentManager.beginTransaction().replace(R.id.framelayout, HomeFragment()).commit()
        supportActionBar?.title = "Home"

        navigationview.setCheckedItem(R.id.homemenu)


    }


    fun setUpToolbar() {
        setSupportActionBar(toolbar1)
        supportActionBar?.title = "Toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerlayout.openDrawer(GravityCompat.START)

        }
        return super.onOptionsItemSelected(item)
    }
}
