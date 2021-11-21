package com.internshala.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.internshala.foodie.R
import com.internshala.foodie.adapter.DBAsync
import com.internshala.foodie.adapter.HomeAdapter
import com.internshala.foodie.database.FoodDatabase
import com.internshala.foodie.database.FoodEntities
import com.internshala.foodie.model.Fooddetails
import com.internshala.foodie.util.ConnectionManager
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_single_row_layout.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var layoutmanager: RecyclerView.LayoutManager
    lateinit var homeadapter: HomeAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressbar: ProgressBar


    val foodlist = arrayListOf<Fooddetails>()

    val sortingComparison = Comparator<Fooddetails> { food1, food2 ->
        food1.cost_for_one.compareTo(food2.cost_for_one, true)


    }
    val ratingcomp = Comparator<Fooddetails> { food1, food2 ->
        food1.rating.compareTo(food2.rating, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerview)
        progressLayout = view.findViewById(R.id.progresslayout)
        progressbar = view.findViewById(R.id.progressbar)
        progressbar.visibility = View.VISIBLE


        setHasOptionsMenu(true)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    val jsonfoodobject = it.getJSONObject("data")
                    val success = jsonfoodobject.getBoolean("success")

                    try {
                        if (activity != null) {
                            progresslayout.visibility = View.GONE
                            if (success) {
                                val data = jsonfoodobject.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val foodjsonobject = data.getJSONObject(i)
                                    val foodobject = Fooddetails(

                                        foodjsonobject.getString("id"),
                                        foodjsonobject.getString("name"),
                                        foodjsonobject.getString("rating"),
                                        foodjsonobject.getString("cost_for_one"),
                                        foodjsonobject.getString("image_url")

                                    )
                                    foodlist.add(foodobject)

                                    recyclerView = view.findViewById(R.id.recyclerview)
                                    homeadapter = HomeAdapter(activity as Context, foodlist)
                                    layoutmanager = LinearLayoutManager(activity)
                                    recyclerView.adapter = homeadapter
                                    recyclerView.layoutManager = layoutmanager


                                }
                            }

                        } else {
                            if (activity != null)
                                Toast.makeText(
                                    activity as Context,
                                    "Some error occured",
                                    Toast.LENGTH_SHORT
                                ).show()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occured!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null)
                        Toast.makeText(
                            activity as Context,
                            "Volley Error occurred",
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connnection not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)


            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()

        }



        return view


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.sorting_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.lowhighsort) {

            Collections.sort(foodlist, sortingComparison)
            homeadapter.notifyDataSetChanged()


        } else if (id == R.id.highlow_sort) {
            Collections.sort(foodlist, sortingComparison)
            foodlist.reverse()

            homeadapter.notifyDataSetChanged()

        } else if (id == R.id.rating) {
            Collections.sort(foodlist, ratingcomp)

            homeadapter.notifyDataSetChanged()
            foodlist.reverse()
        }


        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}