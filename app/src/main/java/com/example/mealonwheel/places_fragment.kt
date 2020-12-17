package com.example.mealonwheel

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mealonwheel.Adapter.FoodAdapter
import com.example.mealonwheel.Adapter.IFoodAdapter
import com.example.mealonwheel.Adapter.MySingleton
import com.example.mealonwheel.Adapter.Place
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import im.delight.android.location.SimpleLocation
import org.json.JSONObject

class places_fragment : Fragment() , IFoodAdapter {
    private lateinit var  mfoodApadter : FoodAdapter
    private lateinit var location : SimpleLocation

    companion object {
         var latitude : Double =0.0
        var longitude : Double =0.0
    }

    override fun onStart() {
        super.onStart()
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(context)
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        location= SimpleLocation(requireContext())

            location.beginUpdates()
            latitude = location.latitude
            longitude = location.longitude
            getPlaces(location.latitude, location.longitude)

        val mView = inflater.inflate(R.layout.fragment_places_fragment, container, false)
        Dexter.withContext(requireContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(object : DexterBuilder.SinglePermissionListener, PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                location.beginUpdates()
                latitude = location.latitude
                longitude = location.longitude
                getPlaces(location.latitude, location.longitude)

            }
            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(requireContext(),"Cannot display data without the location permission",Toast.LENGTH_LONG).show()
            }

            override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                TODO("Not yet implemented")
            }

            override fun withListener(p0: PermissionListener?): DexterBuilder {
                TODO("Not yet implemented")
            }

        }).check()



        val recycle = mView.findViewById<RecyclerView>(R.id.recycle)
        recycle.layoutManager = LinearLayoutManager(requireContext())

        mfoodApadter = FoodAdapter(requireContext(), this)
        recycle.adapter = mfoodApadter

        return mView

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
                        Toast.makeText(requireContext(), "Volley failed to get result :(  ", Toast.LENGTH_LONG)
                                .show()


                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map: HashMap<String, String> = HashMap()
                    map.put("user-key", "da840d878a667110cd95910fe32536c0")
                    map.put("Accept", "application/json");
                    return map
                }
            }
            MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
    override fun onItemClicked(place: Place) {
        val builder= CustomTabsIntent.Builder()
        val customTabsIntent=builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(place.order_url))
    }
}