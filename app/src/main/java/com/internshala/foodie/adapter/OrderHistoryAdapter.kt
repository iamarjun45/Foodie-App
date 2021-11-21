package com.internshala.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodie.R
import com.internshala.foodie.model.Fooditemdetails
import com.internshala.foodie.model.Orderhistdetails
import org.json.JSONArray
import org.json.JSONObject

class OrderHistoryAdapter(val context: Context, val orderhistlist: ArrayList<Orderhistdetails>) :
    RecyclerView.Adapter<OrderHistoryAdapter.orderhistoryViewholder>() {
    val fooditemdetails = arrayListOf<Fooditemdetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderhistoryViewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.orderhistory_single_row_layout, parent, false)
        return OrderHistoryAdapter.orderhistoryViewholder(view)
    }

    override fun getItemCount(): Int {
        return orderhistlist.size
    }

    override fun onBindViewHolder(holder: orderhistoryViewholder, position: Int) {
        val fulldate = orderhistlist[position].order_placed_at
        val date =
            fulldate.substring(0, 2) + "/" + fulldate.substring(3, 5) + "/" + fulldate.substring(
                6,
                8
            )
        holder.res_name.text = orderhistlist[position].restaurant_name
        holder.date.text = date
        val food_items = orderhistlist[position].food_items
        for (i in 0 until food_items.length()) {
            val food_items_object = food_items.getJSONObject(i)
            val food_items_details = Fooditemdetails(
                food_items_object.getString("food_item_id"),
                food_items_object.getString("name"),
                food_items_object.getString("cost")
            )
            fooditemdetails.add(food_items_details)
            holder.orderhistrecyclerview.adapter = OrderhistoryminiAdapter(context, fooditemdetails)
            holder.orderhistrecyclerview.layoutManager = LinearLayoutManager(context)


        }
    }

    class orderhistoryViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val res_name: TextView = view.findViewById(R.id.orderHistoryRestaurantName)
        val date: TextView = view.findViewById(R.id.orderHistoryDate)
        val orderhistrecyclerview: RecyclerView =
            view.findViewById(R.id.recyclerOrderHistorySingleRow)

    }

}