package com.internshala.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodie.R
import com.internshala.foodie.activity.ForgotPassActivity
import com.internshala.foodie.activity.HomeActivity
import com.internshala.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mobilenumber: EditText
    lateinit var pass: EditText
    lateinit var btn: Button
    lateinit var clicktext1: TextView
    lateinit var clicktext2: TextView
    lateinit var loginfo: JSONObject
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var success: Boolean = false

    lateinit var sharedpreference: SharedPreferences

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        progresslayout = view.findViewById(R.id.loadingrell)
        progressBar = view.findViewById(R.id.loginprogbar)
        progressBar.visibility = View.GONE

        sharedpreference = getContext()!!.getSharedPreferences(
            getString(R.string.shared_preferences_save),
            Context.MODE_PRIVATE
        )

        clicktext1 = view.findViewById(R.id.txtnewuser)

        clicktext2 = view.findViewById((R.id.txtforgotpass))

        clicktext1.setOnClickListener()
        {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.usercredentialsframe, RegisterFragment())?.commit()

        }

        clicktext2.setOnClickListener()
        {
            val intent = Intent(activity, ForgotPassActivity::class.java)
            startActivity(intent)


        }


        btn = view.findViewById(R.id.btnlogin)

        btn.setOnClickListener()
        {


            val intent = Intent(activity as Context, HomeActivity::class.java)
            mobilenumber = view.findViewById(R.id.edtxtuser)
            pass = view.findViewById(R.id.edtxtpass)
            if (mobilenumber.text.toString().isEmpty() || pass.text.toString().isEmpty()) {
                Toast.makeText(activity, "Please enter credentials", Toast.LENGTH_SHORT).show()

            } else {
                if (ConnectionManager().checkConnectivity(activity as Context)) {
                    btn.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    clicktext1.visibility = View.GONE
                    clicktext2.visibility = View.GONE

                    val logindetails = JSONObject()
                    logindetails.put("mobile_number", mobilenumber.text.toString())
                    logindetails.put("password", pass.text.toString())

                    val queue = Volley.newRequestQueue(activity as Context)
                    val url = "http://13.235.250.119/v2/login/fetch_result/"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST, url, logindetails,
                        Response.Listener {

                            try {
                                val getdata = it.getJSONObject("data")
                                success = getdata.getBoolean("success")
                                if (success) {

                                    loginfo = getdata.getJSONObject("data")
                                    sharedpreference.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedpreference.edit()
                                        .putString("id", loginfo.getString("user_id")).apply()
                                    sharedpreference.edit()
                                        .putString("name", loginfo.getString("name")).apply()
                                    sharedpreference.edit()
                                        .putString("email", loginfo.getString("email")).apply()
                                    sharedpreference.edit()
                                        .putString("phoneno", loginfo.getString("mobile_number"))
                                        .apply()
                                    sharedpreference.edit()
                                        .putString("address", loginfo.getString("address")).apply()

                                    openactivity()


                                } else {
                                    if (activity != null) {
                                        btn.visibility = View.VISIBLE
                                        clicktext1.visibility = View.VISIBLE
                                        clicktext2.visibility = View.VISIBLE

                                        Toast.makeText(
                                            activity as Context,
                                            getdata.getString("errorMessage"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            } catch (e: JSONException) {
                                btn.visibility = View.VISIBLE
                                clicktext1.visibility = View.VISIBLE
                                clicktext2.visibility = View.VISIBLE

                                Toast.makeText(activity, "Json Error Occurred", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        Response.ErrorListener {
                            if (activity != null) {
                                btn.visibility = View.VISIBLE
                                clicktext1.visibility = View.VISIBLE
                                clicktext2.visibility = View.VISIBLE

                                Toast.makeText(
                                    activity,
                                    "Volley Error Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

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


            }


        }


        return view
    }

    fun openactivity() {
        val intent = Intent(activity as Context, HomeActivity::class.java)
        startActivity(intent)
        getActivity()?.finish()
    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {

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
            dialog.create()
            dialog.show()

        }

        super.onResume()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}