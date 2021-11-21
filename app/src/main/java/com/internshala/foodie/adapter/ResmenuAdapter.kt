package com.internshala.foodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.FtsOptions
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internshala.foodie.R
import com.internshala.foodie.activity.OrdersCartActivity
import com.internshala.foodie.model.Resdetails
import kotlin.contracts.contract

/*class DBasync():AsyncTask<Void,Void,Boolean>(){
    override fun doInBackground(vararg p0: Void?): Boolean {

    }


}*/

class ResmenuAdapter(
    val context: Context,
    val dishlist: ArrayList<Resdetails>,
    cartbtn: FloatingActionButton,
    val resname: String?
) : RecyclerView.Adapter<ResmenuAdapter.ResmenuViewHolder>() {

    val cartbtnshow = cartbtn
    var checkaddednum = 0
    var food_ids = arrayListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResmenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.resmenu_single_row, parent, false)
        return ResmenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dishlist.size
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ResmenuViewHolder, position: Int) {
        var counter: Int = 0


        if (food_ids.contains(dishlist[position].id)) {
            holder.addtocart.setBackgroundResource(R.drawable.addedtocartroundbtn)
            holder.addtocart.text = "Added!"
        } else {
            holder.addtocart.setBackgroundResource(R.drawable.addtocartroundbtn)
            holder.addtocart.text = "Add"
        }

        holder.addtocart.setOnClickListener()
        {
            if (holder.addtocart.text == "Add") {
                holder.addtocart.setBackgroundResource(R.drawable.addedtocartroundbtn)
                holder.addtocart.text = "Added!"
                food_ids.add(holder.addtocart.getTag().toString())
                checkaddednum++
                if (checkaddednum == 1) {
                    cartbtnshow.visibility = View.VISIBLE
                    cartbtnshow.startAnimation(loadAnimation(context, R.anim.bounce))
                }

            } else {
                holder.addtocart.setBackgroundResource(R.drawable.addtocartroundbtn)
                holder.addtocart.text = "Add"
                food_ids.remove(holder.addtocart.getTag().toString())

                checkaddednum--
                if (checkaddednum == 0) {

                    cartbtnshow.visibility = View.GONE
                    cartbtnshow.startAnimation(loadAnimation(context, R.anim.zoomout))


                }
            }


        }
        cartbtnshow.setOnClickListener()
        {
            val intent = Intent(context, OrdersCartActivity::class.java)
            intent.putExtra("res_id", dishlist[position].restaurant_id)
            intent.putExtra("food_ids", food_ids)
            intent.putExtra("resname", resname)
            context.startActivity(intent)
        }
        holder.addtocart.setTag(dishlist[position].id)
        holder.dishname.text = dishlist[position].name
        holder.dishprice.text = "₹" + dishlist[position].cost_for_one

    }

    class ResmenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishname: TextView = view.findViewById(R.id.resmenuname)
        val dishprice: TextView = view.findViewById(R.id.resmenuprice)
        val addtocart: Button = view.findViewById(R.id.resmenubtnaddcart)


    }
}