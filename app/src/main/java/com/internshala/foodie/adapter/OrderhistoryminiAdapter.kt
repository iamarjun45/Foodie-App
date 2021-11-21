package com.internshala.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodie.R
import com.internshala.foodie.model.Fooditemdetails

class OrderhistoryminiAdapter(val context: Context, val fooditemlist: ArrayList<Fooditemdetails>) :
    RecyclerView.Adapter<OrderhistoryminiAdapter.orderhistoryminiViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderhistoryminiViewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ordercart_single_row_layout, parent, false)
        return orderhistoryminiViewholder(view)
    }

    override fun getItemCount(): Int {
        return fooditemlist.size
    }

    override fun onBindViewHolder(holder: orderhistoryminiViewholder, position: Int) {

        holder.dishname.text = fooditemlist[position].name
        holder.dishprice.text = "₹" + fooditemlist[position].cost

    }

    class orderhistoryminiViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val dishname: TextView = view.findViewById(R.id.ordercartdishname)
        val dishprice: TextView = view.findViewById(R.id.ordercartdishprice)

    }

}