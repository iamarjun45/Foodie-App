package com.internshala.foodie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert
    fun insertfav(foodEntities: FoodEntities)

    @Delete
    fun deletefav(foodEntities: FoodEntities)

    @Query("SELECT * FROM Food")
    fun getfav(): List<FoodEntities>

    @Query("SELECT * FROM Food WHERE food_id=:foodid")
    fun checkforfav(foodid: String): FoodEntities


}