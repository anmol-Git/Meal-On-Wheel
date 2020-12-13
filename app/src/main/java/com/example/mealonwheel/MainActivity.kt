package com.example.mealonwheel

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mealonwheel.Adapter.FoodAdapter
import com.example.mealonwheel.Adapter.IFoodAdapter
import com.example.mealonwheel.Adapter.MySingleton
import com.example.mealonwheel.Adapter.Place
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import im.delight.android.location.SimpleLocation
import org.json.JSONObject


class MainActivity : AppCompatActivity(), IFoodAdapter {
    private lateinit var  mfoodApadter :FoodAdapter
    private lateinit var location :SimpleLocation
    private  var sinInActivity =  SignInActivity()

   companion object {
       var isUserLogOut=false
       var latitude: Double = 0.0
       var longitude: Double = 0.0
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        location= SimpleLocation(this)
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(object : DexterBuilder.SinglePermissionListener, PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                location.beginUpdates()
                latitude = location.latitude
                longitude = location.longitude
                getPlaces(location.latitude, location.longitude)

            }
            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                 Toast.makeText(applicationContext,"Cannot display data without the location permission",Toast.LENGTH_LONG).show()
            }

            override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                TODO("Not yet implemented")
            }

            override fun withListener(p0: PermissionListener?): DexterBuilder {
                TODO("Not yet implemented")
            }

        }).check()

        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }





        val recycle = findViewById<RecyclerView>(R.id.recycle)
        recycle.layoutManager = LinearLayoutManager(this)

         mfoodApadter = FoodAdapter(this, this)
        recycle.adapter = mfoodApadter

    }





    override fun onItemClicked(place: Place) {
        val builder= CustomTabsIntent.Builder()
        val customTabsIntent=builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(place.order_url))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if (id==R.id.Log_out){
            val user =FirebaseAuth.getInstance().currentUser
            if (user!=null){
              isUserLogOut=true
                val intent =Intent(this,SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

   private  fun getPlaces( latitude :Double , longitude : Double) {
        Log.v("in"," getPlaces method")
       Log.v("$latitude","$longitude")
        val url="https://developers.zomato.com/api/v2.1/geocode?lat=${latitude}&lon=${longitude}"
        try {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null,
                    { response ->
                        val jsonArray = response.getJSONArray("nearby_restaurants")
                        val foodArray = ArrayList<Place>()
                        for (i in 0 until jsonArray.length()) {
                            val foodJsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val placeObject: JSONObject = foodJsonObject.getJSONObject("restaurant")
                            val address: JSONObject = placeObject.getJSONObject("location")
                            val rate: JSONObject = placeObject.getJSONObject("user_rating")
                            val places = Place(
                                    placeObject.getString("name"),
                                    address.getString("address"),
                                    rate.getString("aggregate_rating"),
                                    placeObject.getString("is_delivering_now"),
                                    placeObject.getString("featured_image"),
                                    placeObject.getString("url"),
                                    address.getDouble("latitude"),
                                    address.getDouble("longitude")
                            )
                            foodArray.add(places)
                        }
                        mfoodApadter.updateFood(foodArray)
                    },
                    {
                        Toast.makeText(this, "Volley failed to get result :(  ", Toast.LENGTH_LONG)
                                .show()


                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map: HashMap<String, String> = HashMap()
                    map.put("user-key", "da840d878a667110cd95910fe32536c0")
                    map.put("Accept", "application/json");
                    return map
                }
            }
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }


}




