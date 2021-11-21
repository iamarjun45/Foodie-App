package com.internshala.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodie.R
import com.internshala.foodie.adapter.OrderHistoryAdapter
import com.internshala.foodie.model.Orderhistdetails
import com.internshala.foodie.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var previousordetxt: TextView
    lateinit var orderhistadpater: OrderHistoryAdapter
    lateinit var layoutmanager: RecyclerView.LayoutManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressBar: ProgressBar
    lateinit var progresslayout: RelativeLayout
    lateinit var noorderhistimg: ImageView
    var orderhistlist = arrayListOf<Orderhistdetails>()

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
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences = getContext()!!.getSharedPreferences(
            getString(R.string.shared_preferences_save),
            Context.MODE_PRIVATE
        )
        progressBar = view.findViewById(R.id.previousorderprogressbar)
        progresslayout = view.findViewById(R.id.previousorderprogresslayout)
        noorderhistimg = view.findViewById(R.id.noorderhistimg)
        previousordetxt = view.findViewById(R.id.previousordertext)





        if (ConnectionManager().checkConnectivity(activity as Context)) {
            progresslayout.visibility = View.VISIBLE
            val queue = Volley.newRequestQueue(context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/" + sharedPreferences.getString(
                "id",
                "-1"
            )
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    val jsonfoodobject = it.getJSONObject("data")
                    val success = jsonfoodobject.getBoolean("success")

                    try {
                        if (activity != null) {
                            if (success) {
                                progresslayout.visibility = View.GONE
                                val data = jsonfoodobject.getJSONArray("data")
                                if (data.length() > 0) {
                                    for (i in 0 until data.length()) {
                                        val foodjsonobject = data.getJSONObject(i)
                                        val foodobject = Orderhistdetails(

                                            foodjsonobject.getString("order_id"),
                                            foodjsonobject.getString("restaurant_name"),
                                            foodjsonobject.getString("total_cost"),
                                            foodjsonobject.getString("order_placed_at"),
                                            foodjsonobject.getJSONArray("food_items")

                                        )
                                        orderhistlist.add(foodobject)

                                        recyclerView =
                                            view.findViewById(R.id.previousorderrecyclerview)
                                        orderhistadpater =
                                            OrderHistoryAdapter(activity as Context, orderhistlist)
                                        layoutmanager = LinearLayoutManager(activity)
                                        recyclerView.adapter = orderhistadpater
                                        recyclerView.layoutManager = layoutmanager


                                    }
                                } else {
                                    noorderhistimg.visibility = View.VISIBLE
                                    previousordetxt.text = "You have not placed any orders yet"

                                    Toast.makeText(
                                        activity,
                                        "No orders placed yet",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
            val dialog = AlertDialog.Builder(activity)
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
         * @return A new instance of fragment OrderHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}