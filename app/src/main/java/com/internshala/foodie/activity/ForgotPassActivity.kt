package com.internshala.foodie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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

class ForgotPassActivity : AppCompatActivity() {

    lateinit var mob: EditText
    lateinit var email: EditText
    lateinit var btn: Button
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null) {
            setContentView(R.layout.activity_forgot_pass)

        }
        progresslayout = findViewById(R.id.forgotpassprogresslayout)
        progressBar = findViewById(R.id.forgotpassprogbar)
        progresslayout.visibility = View.GONE

        btn = findViewById(R.id.btnnext)
        btn.setOnClickListener()
        {

            mob = findViewById(R.id.edtxtmob)
            email = findViewById(R.id.edtxtemail)
            if (mob.text.toString().isEmpty() || email.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            } else {
                val queue = Volley.newRequestQueue(this@ForgotPassActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mob.text.toString())
                jsonParams.put("email", email.text.toString())

                if (ConnectionManager().checkConnectivity(this@ForgotPassActivity)) {
                    progresslayout.visibility = View.VISIBLE
                    btn.visibility = View.GONE

                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(
                                Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {

                                    try {

                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {
                                            val intent = Intent(
                                                this,
                                                ResetPassActivity::class.java
                                            )
                                            intent.putExtra(
                                                "mobile_number",
                                                mob.text.toString()
                                            )
                                            if (data.getBoolean("first_try")) {
                                                Toast.makeText(
                                                    this,
                                                    "OTP has been sent to ${email.text}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "OTP has already been sent to ${email.text}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            startActivity(intent)

                                        } else {
                                            val msg = data.getString("errorMessage")
                                            Toast.makeText(
                                                this,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            progresslayout.visibility = View.GONE
                                            btn.visibility = View.VISIBLE
                                        }

                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this,
                                            "JSON error occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        progresslayout.visibility = View.GONE
                                        btn.visibility = View.VISIBLE
                                    }
                                },
                                Response.ErrorListener {

                                    Toast.makeText(
                                        this,
                                        "Volley error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    progresslayout.visibility = View.GONE
                                    btn.visibility = View.VISIBLE
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


}
