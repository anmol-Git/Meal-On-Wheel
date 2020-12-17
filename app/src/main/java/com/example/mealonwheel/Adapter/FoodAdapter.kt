package com.example.mealonwheel.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.mealonwheel.MainActivity
import com.example.mealonwheel.R
import com.example.mealonwheel.R.color.green
import com.example.mealonwheel.R.color.red
import com.example.mealonwheel.SignInActivity
import com.example.mealonwheel.places_fragment
import java.text.DecimalFormat
import kotlin.math.cos

class FoodAdapter(private val context: Context, private val listener: IFoodAdapter) : RecyclerView.Adapter<FoodView>() {
      private val allPlace : ArrayList<Place> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodView {

        val viewHolder=FoodView(LayoutInflater.from(context).inflate(R.layout.food_item_view,parent,false))
        viewHolder.place.setOnClickListener{
            listener.onItemClicked(allPlace[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FoodView, position: Int) {
       val currPlace= allPlace[position]
        holder.Placename.text=currPlace.name
        holder.PlaceAddress.text=currPlace.address
        holder.rating.text= currPlace.rating

        if (currPlace.available=="0") {
                holder.Placestatus.setBackgroundResource(R.drawable.rounded_text_green)
            holder.Placestatus.text="OPEN"
        }else if (currPlace.available=="1") {
            holder.Placestatus.setBackgroundResource(R.drawable.rounded_text_red)
            holder.Placestatus.text="CLOSED"
        }
        holder.OutOfRating.text="/ 5"

       if (currPlace.imageUrl!="0") {
           Glide.with(holder.itemView.context).load(currPlace.imageUrl).override(150, 150)
           .error(R.drawable.cooking).into(holder.PlaceImage)
       }

             var dist=distance(places_fragment.latitude,places_fragment.longitude,currPlace.latitude,currPlace.longitude)

        if (dist==0.0){
            holder.PlaceDistance.text="No data"
        }else
        {
            holder.PlaceDistance.text=DecimalFormat("#.##").format(dist)+" km"
        }

    }



    override fun getItemCount(): Int {
      return allPlace.size
    }

    fun updateFood(item : ArrayList<Place>){
        allPlace.clear()
        allPlace.addAll(item)
       notifyDataSetChanged()
        Log.v("in","update method")
    }

    fun distance(lat1 : Double, lon1 :Double , lat2: Double, lon2: Double): Double {
        if (lat2 ==0.0000000000){
            return 0.0
        }
        var p = 0.017453292519943295;    // Math.PI / 180

        var a = 0.5 - cos((lat2 - lat1) * p)/2 +
                cos(lat1 * p) * cos(lat2 * p) *
                (1 - cos((lon2 - lon1) * p))/2

        return 12742 * Math.asin(Math.sqrt(a)) // 2 * R; R = 6371 km
    }

}

class FoodView(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val place =itemView.findViewById<ConstraintLayout>(R.id.place)
    val Placename =itemView.findViewById<TextView>(R.id.name)
    val PlaceAddress =itemView.findViewById<TextView>(R.id.address)
    val rating  =itemView.findViewById<TextView>(R.id.curr_rating)
      val PlaceImage =itemView.findViewById<ImageView>(R.id.image)
    val Placestatus =itemView.findViewById<TextView>(R.id.status)
    val PlaceDistance=itemView.findViewById<TextView>(R.id.distance)
    val OutOfRating =itemView.findViewById<TextView>(R.id.outOf)
}

interface IFoodAdapter {
     fun onItemClicked (place :Place)
}