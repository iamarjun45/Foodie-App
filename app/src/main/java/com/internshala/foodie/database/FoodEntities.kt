package com.internshala.foodie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Food")
data class FoodEntities(
    @PrimaryKey val food_id: String,
    @ColumnInfo(name = "food_name") val name: String,
    @ColumnInfo(name = "food_rating") val rating: String,
    @ColumnInfo(name = "food_price") val cost_for_one: String,
    @ColumnInfo(name = "food_image") val image_url: String

)