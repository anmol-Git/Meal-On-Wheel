package com.example.mealonwheel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealonwheel.MainActivity.Companion.isUserLogOut
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity :AppCompatActivity() {

    companion object {
        private const val  RC_SIGN_IN =120

    }
    lateinit var mAuth: FirebaseAuth
    lateinit  var googleSignInClient : GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        this.supportActionBar?.hide()

        window.decorView.apply {
            systemUiVisibility= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient= GoogleSignIn.getClient(this, gso)

        mAuth= FirebaseAuth.getInstance()

        val btn=findViewById<ImageView>(R.id.google)
        btn.setOnClickListener {
            signIn()
        }

        if(isUserLogOut) {
            mAuth.signOut()
            googleSignInClient.signOut()
            isUserLogOut=false
        }

}
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val exception=task.exception
            if (task.isSuccessful){
                
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Toast.makeText(applicationContext, "login succesfull :)", Toast.LENGTH_LONG).show()
                    Log.d("", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Error", "Google sign in failed", e)
                    // ...
                }
            }else{
                Toast.makeText(applicationContext, "login failed :(", Toast.LENGTH_LONG).show()
                Log.w("Error", exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "login success", Toast.LENGTH_LONG).show()
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("sign in activity", "signInWithCredential:success")
                        val intent=Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        //Toast.makeText(applicationContext, "login failed", Toast.LENGTH_LONG).show()
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in activity", "signInWithCredential:failure", task.exception)
                            Toast.makeText(this,"Login failed ",Toast.LENGTH_LONG).show()
                    }
                }
    }




}