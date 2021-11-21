package com.internshala.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.FileUtils
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.wear.widget.CircularProgressLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodie.R
import com.internshala.foodie.adapter.DBAsync
import com.internshala.foodie.adapter.HomeAdapter
import com.internshala.foodie.database.FoodDatabase
import com.internshala.foodie.database.FoodEntities
import com.internshala.foodie.model.Fooddetails
import com.internshala.foodie.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class checkforfav(val context: Context, val mode: Int) : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()
        when (mode) {
            1 -> {
                val foodlist = db.foodao().getfav()
                if (foodlist.isEmpty())
                    return false
                else
                    return true


            }

        }
        return false
    }


}

class FavouritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var favadapter: HomeAdapter
    lateinit var favlayoutmanager: RecyclerView.LayoutManager
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var nofavtxt: TextView


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
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerView = view.findViewById(R.id.favrecyclerview)
        progressBar = view.findViewById(R.id.favprogressbar)
        progressLayout = view.findViewById(R.id.favprogresslayout)
        nofavtxt = view.findViewById(R.id.nofavtxt)
        progressBar.visibility = View.VISIBLE
        val nofavimg: ImageView = view.findViewById(R.id.nofavimg)

        val favfoodlist = arrayListOf<Fooddetails>()

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            if (!checkforfav(activity as Context, 1).execute().get()) {
                progressLayout.visibility = View.GONE
                nofavimg.visibility = View.VISIBLE
                Toast.makeText(activity, "No Favourites added yet!!", Toast.LENGTH_SHORT).show()
                nofavtxt.text = "It's lonely in here!"
                nofavimg.setImageResource(R.drawable.nofavscreen)

            } else {


                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                        try {
                            val getdata = it.getJSONObject("data")
                            val success = getdata.getBoolean("success")
                            if (success) {
                                favfoodlist.clear()
                                progressLayout.visibility = View.GONE
                                val data = getdata.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val foodjsonobject = data.getJSONObject(i)
                                    val foodEntities = FoodEntities(
                                        foodjsonobject.getString("id"),
                                        foodjsonobject.getString("name"),
                                        foodjsonobject.getString("rating"),
                                        foodjsonobject.getString("cost_for_one"),
                                        foodjsonobject.getString("image_url")


                                    )
                                    if (activity != null)
                                        if (DBAsync(activity as Context, foodEntities, 1).execute()
                                                .get()
                                        ) {
                                            val foodobject = Fooddetails(

                                                foodjsonobject.getString("id"),
                                                foodjsonobject.getString("name"),
                                                foodjsonobject.getString("rating"),
                                                foodjsonobject.getString("cost_for_one"),
                                                foodjsonobject.getString("image_url")

                                            )
                                            favfoodlist.add(foodobject)
                                            recyclerView = view.findViewById(R.id.favrecyclerview)
                                            favadapter =
                                                HomeAdapter(activity as Context, favfoodlist)
                                            favlayoutmanager = LinearLayoutManager(activity)
                                            recyclerView.adapter = favadapter
                                            recyclerView.layoutManager = favlayoutmanager
                                        }


                                }


                            } else {
                                Toast.makeText(activity, "Some Error occurred", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(activity, "Some error occorred", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }, Response.ErrorListener {
                        if (activity != null)
                            Toast.makeText(activity, "Volley error occurred!", Toast.LENGTH_SHORT)
                                .show()

                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9ca84225702ca4"
                            return headers

                        }
                    }
                queue.add(jsonObjectRequest)


            }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}