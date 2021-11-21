package com.internshala.foodie.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodie.R
import com.internshala.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPassActivity : AppCompatActivity() {


    lateinit var otp: EditText
    lateinit var newpass: EditText
    lateinit var confirmpass: EditText
    lateinit var submitbtn: Button
    lateinit var mobno: String
    lateinit var progressBar: ProgressBar
    lateinit var progresslayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        otp = findViewById(R.id.otpedtxt)
        newpass = findViewById(R.id.otpnewpassword)
        confirmpass = findViewById(R.id.otpconfirmpassword)
        submitbtn = findViewById(R.id.otpbtnsubmit)
        progressBar = findViewById(R.id.otpprogbar)
        progresslayout = findViewById(R.id.otpprogresslayout)
        if (intent != null) {
            mobno = intent.getStringExtra("mobile_number")

        }

        submitbtn.setOnClickListener()
        {
            if (otp.text.toString().isEmpty() || newpass.text.toString()
                    .isEmpty() || confirmpass.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please enter all the credentials!", Toast.LENGTH_SHORT).show()
            } else if (newpass.text.toString() != confirmpass.text.toString()) {
                Toast.makeText(
                    this,
                    "Password and confirm password do not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (ConnectionManager().checkConnectivity(this)) {
                    submitbtn.visibility = View.GONE
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                    var params = JSONObject()
                    params.put("mobile_number", mobno)
                    params.put("password", newpass.text)
                    params.put("otp", otp.text)

                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(
                                Method.POST,
                                url,
                                params,
                                Response.Listener {

                                    try {

                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {
                                            val intent = Intent(
                                                this,
                                                UserCredentialsActivity::class.java
                                            )

                                            Toast.makeText(
                                                this,
                                                data.getString("successMessage"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(intent)

                                        } else {
                                            submitbtn.visibility = View.VISIBLE
                                            val msg = data.getString("errorMessage")
                                            Toast.makeText(
                                                this,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                    } catch (e: JSONException) {
                                        submitbtn.visibility = View.VISIBLE
                                        Toast.makeText(
                                            this,
                                            "JSON error occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                },
                                Response.ErrorListener {
                                    submitbtn.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this,
                                        "Volley error occurred",
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
                        finish()

                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)


                    }
                    dialog.setCancelable(false)
                    dialog.create()
                    dialog.show()
                }


            }


        }


    }

    override fun onBackPressed() {
        val intent = Intent(this, UserCredentialsActivity::class.java)
        finish()
        startActivity(intent)
    }
}