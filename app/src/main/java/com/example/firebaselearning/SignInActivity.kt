package com.example.firebaselearning

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.firebaselearning.daos.UserDao
import com.example.firebaselearning.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private var RC_SIGN_IN = 123
    private var googleSignInClient:GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
          auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val login = findViewById<Button>(R.id.loginButton)
        login.setOnClickListener { signIn() }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //  val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInIntent()
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //  val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val loginButton = findViewById<Button>(R.id.loginButton)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        loginButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        // ...
                        //  val view
                        // Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // ...
                }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {

        if ( firebaseUser != null) {

            val User = firebaseUser.displayName?.let { User(firebaseUser.uid, it, firebaseUser.photoUrl.toString()) }
            val UsersDao = UserDao()
            UsersDao.addUser(User)

            val user = firebaseUser.displayName?.let {
                User(firebaseUser.uid,
                    it, firebaseUser.photoUrl.toString())
            }
            val usersDao = UserDao()
            usersDao.addUser(user)

            val progressBar = findViewById<ProgressBar>(R.id.progressBar)

            progressBar.visibility = View.GONE
         //   val login = findViewById<TextView>(R.id.login)
          //  login.visibility = View.VISIBLE
         //   val logInButton = findViewById<Button>(R.id.loginButton)
          //  logInButton.visibility = View.GONE
            val mainActivityIntent = Intent(this,MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
            //  Toast.makeText(this,"login successful",Toast.LENGTH_LONG).show()
            //   binding.signInButton.visibility = View.GONE
            //    binding.signOutAndDisconnect.visibility = View.VISIBLE
        } else {
            //  binding.status.setText(R.string.signed_out)
            //   binding.detail.text = null
            val loginButton = findViewById<Button>(R.id.loginButton)
            loginButton.visibility = View.VISIBLE
            val login = findViewById<TextView>(R.id.login)
            login.visibility = View.GONE
            //    Toast.makeText(this,"login faild",Toast.LENGTH_LONG).show()
            //   binding.signInButton.visibility = View.VISIBLE
            // binding.signOutAndDisconnect.visibility = View.GONE
        }
    }


}