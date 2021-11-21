package com.internshala.foodie.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodie.R
import com.internshala.foodie.activity.HomeActivity
import com.internshala.foodie.activity.RestaurantmenuActivity
import com.internshala.foodie.database.FoodDatabase
import com.internshala.foodie.database.FoodEntities
import com.internshala.foodie.model.Fooddetails
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso

class DBAsync(val context: Context, val foodEntities: FoodEntities, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {

            1 -> {

                val food: FoodEntities? = db.foodao().checkforfav(foodEntities.food_id.toString())
                db.close()
                return food != null
            }

            2 -> {
                db.foodao().insertfav(foodEntities)
                db.close()
                return true

            }

            3 -> {

                db.foodao().deletefav(foodEntities)
                db.close()
                return true
            }
            4 -> {

                val foodlist = db.foodao().getfav()
                if (foodlist.isEmpty())
                    return false
                else
                    return true


            }

            else ->
                return false

        }
    }
}


class HomeAdapter(val context: Context, val foodlist: ArrayList<Fooddetails>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_single_row_layout, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodlist.size
    }

    override fun onBindViewHolder(holder: HomeAdapter.HomeViewHolder, position: Int) {

        val price: String = "₹" + foodlist[position].cost_for_one
        holder.foodname.text = foodlist[position].name
        holder.rating.text = foodlist[position].rating
        holder.price.text = price
        Picasso.get().load(foodlist[position].image_url).error(R.drawable.foodsubstitue)
            .into(holder.img)
        holder.img.clipToOutline = true

        val foodentity = FoodEntities(
            foodlist[position].id,
            foodlist[position].name,
            foodlist[position].rating,
            foodlist[position].cost_for_one,
            foodlist[position].image_url
        )
        holder.likebtn.setOnLikeListener(object : OnLikeListener {

            override fun liked(likeButton: LikeButton) {
                val addfav = DBAsync(context, foodentity, 2).execute()
                if (addfav.get()) {
                    holder.likebtn.isLiked = true
                    Toast.makeText(context, "Restaurant added to Favourite!!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    holder.likebtn.isLiked = false
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }


            }

            override fun unLiked(likeButton: LikeButton) {
                val delfav = DBAsync(context, foodentity, 3).execute()
                if (delfav.get()) {
                    holder.likebtn.isLiked = false
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    holder.likebtn.isLiked = true
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }


            }
        })
        val checkfav = DBAsync(context, foodentity, 1).execute()
        holder.likebtn.isLiked = checkfav.get()//if false isLiked will be false else true

        holder.clickres.setOnClickListener()
        {
            val intent = Intent(context, RestaurantmenuActivity::class.java)
            intent.putExtra("res_id", foodlist[position].id)
            intent.putExtra("resname", foodlist[position].name)
            intent.putExtra("rating", foodlist[position].rating)
            intent.putExtra("cost", foodlist[position].cost_for_one)
            intent.putExtra("imgurl", foodlist[position].image_url)
            context.startActivity(intent)

        }


    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val clickres: LinearLayout = view.findViewById(R.id.homellayout)
        val foodname: TextView = view.findViewById(R.id.foodname)
        val rating: TextView = view.findViewById(R.id.foodrating)
        val price: TextView = view.findViewById(R.id.foodprice)
        val img: ImageView = view.findViewById(R.id.foodimg)

        val likebtn: LikeButton = view.findViewById(R.id.heartbutton)

    }


}