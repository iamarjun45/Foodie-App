package com.internshala.foodie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internshala.foodie.R
import com.internshala.foodie.adapter.DBAsync
import com.internshala.foodie.adapter.ResmenuAdapter
import com.internshala.foodie.database.FoodDatabase
import com.internshala.foodie.database.FoodEntities
import com.internshala.foodie.model.Resdetails
import com.internshala.foodie.util.ConnectionManager
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.Response
import org.json.JSONException

class checkfav(val context: Context, val food_id: String, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {

            1 -> {

                val food: FoodEntities? = db.foodao().checkforfav(food_id)
                db.close()
                return food != null
            }


            else ->
                return false

        }
    }
}


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RestaurantmenuActivity : AppCompatActivity() {

    lateinit var res_id: String
    lateinit var recyclerView: RecyclerView
    lateinit var resmenuAdapter: ResmenuAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var resprice: String
    lateinit var resrating: String
    lateinit var resimgurl: String
    lateinit var likeButton: LikeButton
    lateinit var cartbtn: FloatingActionButton
    lateinit var resname: String
    lateinit var progressBar: ProgressBar
    lateinit var progresslayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurantmenu)
        likeButton = findViewById(R.id.resmenuheart)
        if (intent != null) {
            res_id = intent.getStringExtra("res_id")
            resname = intent.getStringExtra("resname")
            resprice = intent.getStringExtra("cost")
            resrating = intent.getStringExtra("rating")
            resimgurl = intent.getStringExtra("imgurl")
            cartbtn = findViewById(R.id.resmenucartbutton)
        }
        progressBar = findViewById(R.id.resmenuprogressbar)
        progresslayout = findViewById(R.id.resmenuprogresslayout)
        progresslayout.visibility = View.VISIBLE

        if (checkfav(this, res_id.toString(), 1).execute().get()) {
            likeButton.isLiked = true

        }
        val foodentity = FoodEntities(
            res_id,
            resname,
            resrating,
            resprice,
            resimgurl

        )
        likeButton.setOnLikeListener(object : OnLikeListener {

            override fun liked(likeButton: LikeButton) {
                val addfav = DBAsync(applicationContext, foodentity, 2).execute()
                if (addfav.get()) {
                    likeButton.isLiked = true
                    Toast.makeText(
                        applicationContext,
                        "Restaurant added to Favourite!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    likeButton.isLiked = false
                    Toast.makeText(applicationContext, "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                }


            }

            override fun unLiked(likeButton: LikeButton) {
                val delfav = DBAsync(applicationContext, foodentity, 3).execute()
                if (delfav.get()) {
                    likeButton.isLiked = false
                    Toast.makeText(
                        applicationContext,
                        "Restaurant removed from favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    likeButton.isLiked = true
                    Toast.makeText(applicationContext, "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                }


            }
        })


        val resmenulist = arrayListOf<Resdetails>()

        toolbar = findViewById(R.id.resmenutoolbar)
        setUpToolbar(resname)


        if (ConnectionManager().checkConnectivity(this)) {
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/" + res_id
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                com.android.volley.Response.Listener {
                    val jsonresobject = it.getJSONObject("data")
                    val success = jsonresobject.getBoolean("success")

                    try {
                        if (this != null) {
                            if (success) {
                                progresslayout.visibility = View.GONE
                                val data = jsonresobject.getJSONArray("data")

                                for (i in 0 until data.length()) {

                                    val foodjsonobject = data.getJSONObject(i)
                                    val foodobject = Resdetails(

                                        foodjsonobject.getString("id"),
                                        foodjsonobject.getString("name"),
                                        foodjsonobject.getString("cost_for_one"),
                                        foodjsonobject.getString("restaurant_id")
                                    )
                                    resmenulist.add(foodobject)

                                    recyclerView = findViewById(R.id.resmenurecyclerview)
                                    resmenuAdapter =
                                        ResmenuAdapter(
                                            this as Context,
                                            resmenulist,
                                            cartbtn,
                                            resname
                                        )
                                    layoutManager = LinearLayoutManager(this)
                                    recyclerView.adapter = resmenuAdapter
                                    recyclerView.layoutManager = layoutManager


                                }
                            } else {
                                if (this != null)
                                    Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT)
                                        .show()

                            }

                        }

                    } catch (e: JSONException) {
                        if (this != null)
                            Toast.makeText(
                                this,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    }
                },
                com.android.volley.Response.ErrorListener {
                    if (this != null)
                        Toast.makeText(this, "Volley Error occurred", Toast.LENGTH_SHORT).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9ca84225702ca4"
                    return headers

                }


            }


            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connnection not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this.finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)


            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }


    }

    fun setUpToolbar(resname: String?) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = resname
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onBackPressed() {

        if (resmenuAdapter.checkaddednum > 0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Hey!")
            dialog.setMessage("Going back will remove all items from the cart, are you sure?")
            dialog.setPositiveButton("Go Back") { text, listener ->
                super.onBackPressed()


            }
            dialog.setNegativeButton("Cancel") { text, listener ->

            }
            dialog.create()
            dialog.show()
        } else
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                if (resmenuAdapter.checkaddednum > 0) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Hey!")
                    dialog.setMessage("Going back will remove all items from the cart, are you sure?")
                    dialog.setPositiveButton("Go Back") { text, listener ->
                        super.onBackPressed()


                    }
                    dialog.setNegativeButton("Cancel") { text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                } else
                    super.onBackPressed()
            }


        }
        return super.onOptionsItemSelected(item)
    }
}