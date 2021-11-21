package com.internshala.foodie.model

import org.json.JSONArray

data class Orderhistdetails(
    val order_id: String,
    val restaurant_name: String,
    val total_cost: String,
    val order_placed_at: String,
    val food_items: JSONArray


)