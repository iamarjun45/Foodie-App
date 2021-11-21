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
import com.internshala.foodie.activity.HomeActivity
import com.internshala.foodie.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var mobno: EditText
    lateinit var addr: EditText
    lateinit var pass: EditText
    lateinit var confpass: EditText
    lateinit var btn: Button
    lateinit var sharedpreference: SharedPreferences
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var goback: ImageView
    lateinit var logininstead: TextView


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
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        btn = view.findViewById(R.id.btnregister)
        name = view.findViewById(R.id.edtxtname)
        email = view.findViewById(R.id.edtxtmail)
        mobno = view.findViewById(R.id.edtxtmobile)
        addr = view.findViewById(R.id.edtxtaddress)
        pass = view.findViewById(R.id.edtxtpassword)
        goback = view.findViewById(R.id.registergoback)
        confpass = view.findViewById(R.id.edtxtconfirmpass)
        logininstead = view.findViewById(R.id.reglogininstead)
        progresslayout = view.findViewById(R.id.regloadingrell)
        progressBar = view.findViewById(R.id.regloginprogbar)
        progresslayout.visibility = View.GONE


        logininstead.setOnClickListener() {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.usercredentialsframe, LoginFragment())?.commit()

        }
        goback.setOnClickListener()
        {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.usercredentialsframe, LoginFragment())?.commit()

        }

        btn.setOnClickListener()
        {
            if (name.text.toString().isEmpty() || confpass.text.toString()
                    .isEmpty() || mobno.text.toString().isEmpty() || email.text.toString()
                    .isEmpty() || addr.text.toString().isEmpty() || pass.text.toString().isEmpty()
            ) {
                Toast.makeText(activity, "Please fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                if (pass.text.toString() != confpass.text.toString()) {
                    Toast.makeText(
                        activity,
                        "The password and confirm password do not match!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (name.text.toString().length < 3) {
                    Toast.makeText(activity, "Name is too short!!", Toast.LENGTH_SHORT).show()
                } else if (mobno.text.toString().length < 10 || pass.text.toString().length < 4) {
                    Toast.makeText(
                        activity,
                        "Mobile number or password length too short!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(
                        activity as Context,
                        HomeActivity::class.java
                    )

                    if (ConnectionManager().checkConnectivity(activity as Context)) {
                        progresslayout.visibility = View.VISIBLE
                        btn.visibility = View.GONE
                        sharedpreference = getContext()!!.getSharedPreferences(
                            getString(R.string.shared_preferences_save),
                            Context.MODE_PRIVATE
                        )
                        sharedpreference.edit().putBoolean("user_logged_in", false).apply()
                        val registerdetails = JSONObject()
                        registerdetails.put("name", name.text.toString())
                        registerdetails.put("mobile_number", mobno.text.toString())
                        registerdetails.put("password", pass.text.toString())
                        registerdetails.put("address", addr.text.toString())
                        registerdetails.put("email", email.text.toString())


                        val queue = Volley.newRequestQueue(activity as Context)
                        val url = "http://13.235.250.119/v2/register/fetch_result"

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST, url, registerdetails,
                            Response.Listener {
                                println("Response12 is " + it)
                                try {
                                    val getdata = it.getJSONObject("data")
                                    val success = getdata.getBoolean("success")
                                    if (success) {
                                        val reginfo = getdata.getJSONObject("data")
                                        sharedpreference.edit().putBoolean("isLoggedIn", true)
                                            .apply()
                                        sharedpreference.edit()
                                            .putString("id", reginfo.getString("user_id")).apply()
                                        sharedpreference.edit()
                                            .putString("name", reginfo.getString("name")).apply()
                                        sharedpreference.edit()
                                            .putString("email", reginfo.getString("email")).apply()
                                        sharedpreference.edit().putString(
                                            "phoneno",
                                            reginfo.getString("mobile_number")
                                        ).apply()
                                        sharedpreference.edit()
                                            .putString("address", reginfo.getString("address"))
                                            .apply()
                                        startActivity(intent)


                                    } else {
                                        progresslayout.visibility = View.GONE
                                        btn.visibility = View.VISIBLE
                                        Toast.makeText(
                                            activity, getdata.getString("errorMessage"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                } catch (e: JSONException) {
                                    progresslayout.visibility = View.GONE
                                    btn.visibility = View.VISIBLE
                                    Toast.makeText(
                                        activity,
                                        "Some Error Occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                progresslayout.visibility = View.GONE
                                btn.visibility = View.VISIBLE
                                Toast.makeText(
                                    activity,
                                    "Volley Error Occurred",
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


                }


            }


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
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}