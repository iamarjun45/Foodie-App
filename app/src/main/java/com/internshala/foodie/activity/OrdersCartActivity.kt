package com.internshala.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodie.R
import com.internshala.foodie.adapter.OrderCartAdapter
import com.internshala.foodie.adapter.ResmenuAdapter
import com.internshala.foodie.model.Resdetails
import com.internshala.foodie.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OrdersCartActivity : AppCompatActivity() {
    var res_id: String? = null
    lateinit var food_ids: ArrayList<String>
    var user_id: String? = null
    var totalCost: Int = 0
    var resname: String? = ""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var orderCartAdapter: OrderCartAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var placeorderbtn: Button
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    lateinit var orderingfrom: TextView
    lateinit var loadingbtn: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_cart)

        progresslayout = findViewById(R.id.ordercartprogresslayout)
        progressbar = findViewById(R.id.ordercartprogressbar)
        loadingbtn = findViewById(R.id.ordercartplaceorderbtnloading)
        loadingbtn.visibility = View.GONE

        if (intent != null) {
            res_id = intent.getStringExtra("res_id")
            food_ids = intent.getStringArrayListExtra("food_ids")
            resname = intent.getStringExtra("resname")
        }
        orderingfrom = findViewById(R.id.cartorderingfromtxt)
        orderingfrom.text = "Ordering from: " + resname


        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences_save), Context.MODE_PRIVATE)
        user_id = sharedPreferences.getString("id", "-1")

        toolbar = findViewById(R.id.ordercarttoolbar)
        placeorderbtn = findViewById(R.id.ordercartplaceorderbtn)


        var orderitemlist = arrayListOf<Resdetails>()
        placeorderbtn.visibility = View.GONE


        placeorderbtn.setOnClickListener()
        {
            placeorderbtn.visibility = View.GONE
            loadingbtn.visibility = View.VISIBLE
            if (ConnectionManager().checkConnectivity(this)) {
                val foodidjsonarray = JSONArray()
                for (i in food_ids) {
                    val jsonObject = JSONObject()
                    jsonObject.put("food_item_id", i)
                    foodidjsonarray.put(jsonObject)
                }
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val params = JSONObject()
                params.put("user_id", user_id)
                params.put("restaurant_id", res_id)
                params.put("total_cost", totalCost)
                params.put("food", foodidjsonarray)

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    params,
                    Response.Listener {


                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {


                            val intent = Intent(this, OrderCompletionActivity::class.java)
                            startActivity(intent)


                        } else {
                            placeorderbtn.visibility = View.VISIBLE
                            loadingbtn.visibility = View.GONE
                            val responseMessageServer =
                                responseJsonObjectData.getString("errorMessage")
                            Toast.makeText(
                                this,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    },
                    Response.ErrorListener {

                        placeorderbtn.visibility = View.VISIBLE
                        loadingbtn.visibility = View.GONE


                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

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



        setUpToolbar(resname)

        if (ConnectionManager().checkConnectivity(this)) {
            progresslayout.visibility = View.VISIBLE
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
                        if (success) {
                            placeorderbtn.visibility = View.VISIBLE
                            progresslayout.visibility = View.GONE
                            val data = jsonresobject.getJSONArray("data")

                            for (i in 0 until data.length()) {

                                val foodjsonobject = data.getJSONObject(i)
                                if (food_ids.contains(foodjsonobject.getString("id"))) {
                                    val foodobject = Resdetails(

                                        foodjsonobject.getString("id"),
                                        foodjsonobject.getString("name"),
                                        foodjsonobject.getString("cost_for_one"),
                                        foodjsonobject.getString("restaurant_id")
                                    )
                                    totalCost = totalCost + foodobject.cost_for_one.toInt()
                                    orderitemlist.add(foodobject)
                                }

                                recyclerView = findViewById(R.id.ordercartrecyclerview)
                                orderCartAdapter = OrderCartAdapter(this, orderitemlist)
                                layoutManager = LinearLayoutManager(this)
                                recyclerView.adapter = orderCartAdapter
                                recyclerView.layoutManager = layoutManager


                            }
                            placeorderbtn.text = "Place order (₹" + totalCost.toString() + ")"
                        } else {
                            if (this != null)
                                Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT)
                                    .show()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some unexpected error occurred!!", Toast.LENGTH_SHORT)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


}