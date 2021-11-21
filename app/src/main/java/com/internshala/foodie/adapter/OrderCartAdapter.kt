package com.internshala.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodie.R
import com.internshala.foodie.model.Resdetails

class OrderCartAdapter(val context: Context, val ordercartlist: ArrayList<Resdetails>) :
    RecyclerView.Adapter<OrderCartAdapter.OrdercartViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdercartViewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ordercart_single_row_layout, parent, false)
        return OrdercartViewholder(view)
    }

    override fun getItemCount(): Int {
        return ordercartlist.size
    }

    override fun onBindViewHolder(holder: OrdercartViewholder, position: Int) {
        holder.dishname.text = ordercartlist[position].name
        holder.dishprice.text = "₹" + ordercartlist[position].cost_for_one
    }

    class OrdercartViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val dishname: TextView = view.findViewById(R.id.ordercartdishname)
        val dishprice: TextView = view.findViewById(R.id.ordercartdishprice)

    }

}