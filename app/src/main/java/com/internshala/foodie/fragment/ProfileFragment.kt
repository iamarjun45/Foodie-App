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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.internshala.foodie.R
import com.internshala.foodie.activity.HomeActivity
import com.internshala.foodie.util.ConnectionManager
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var username: TextView
    lateinit var mobno: TextView
    lateinit var email: TextView
    lateinit var addr: TextView
    lateinit var progressbar: ProgressBar
    lateinit var progresslayout: RelativeLayout
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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        username = view.findViewById(R.id.profilename)
        mobno = view.findViewById(R.id.profilephone)
        email = view.findViewById(R.id.profilemail)
        addr = view.findViewById(R.id.profileaddr)
        progressbar = view.findViewById(R.id.proprogressbar)
        progresslayout = view.findViewById(R.id.profileprogresslayout)
        progresslayout.visibility = View.VISIBLE

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            progresslayout.visibility = View.GONE
            sharedPreferences = getContext()!!.getSharedPreferences(
                getString(R.string.shared_preferences_save),
                Context.MODE_PRIVATE
            )
            username.text = sharedPreferences.getString("name", "Internshala")
            mobno.text = sharedPreferences.getString("phoneno", "1111111111")
            email.text = sharedPreferences.getString("email", "intershala@awesome.com")
            addr.text = sharedPreferences.getString("address", "India")
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}