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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mealonwheel.Adapter.*
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


class MainActivity : AppCompatActivity() {
   companion object {
       var isUserLogOut=false
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar =findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
     //   setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        supportActionBar ?.setDisplayHomeAsUpEnabled(true)


        val fragments =ArrayList<Fragment>()
        fragments.add(places_fragment())
        fragments.add(maps_fragment())

        val titles = ArrayList<String>()
        titles.add("Restaurants")
        titles.add("Maps")

        val adapter = PagerAdapter(fragments,titles,supportFragmentManager)

        val viewPager =findViewById<androidx.viewpager.widget.ViewPager>(R.id.viewPager)
        viewPager.adapter=adapter
        val tabLayout =findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)

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
}




