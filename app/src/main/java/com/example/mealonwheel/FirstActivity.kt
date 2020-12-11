package com.example.mealonwheel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth

class FirstActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    lateinit var animations: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_activity)
        this.supportActionBar?.hide()

        window.decorView.apply {
            systemUiVisibility= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        mAuth= FirebaseAuth.getInstance()
        val user =mAuth.currentUser
        animations=findViewById(R.id.animationView)
        animations.playAnimation()


        Handler().postDelayed({
            if (user!=null){
               val dashboard=Intent(this,MainActivity::class.java)
                startActivity(dashboard)
                this.finish()
            }else{
                val sign=Intent(this,SignInActivity::class.java)
                startActivity(sign)
                this.finish()
            }
        },2000)


    }
}