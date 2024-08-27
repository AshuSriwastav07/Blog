package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivitySignupAndLoginPageBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupAndLoginPage : AppCompatActivity() {

    // Binding object to access UI components in the layout
    private lateinit var binding: ActivitySignupAndLoginPageBinding

    // FirebaseAuth instance for handling authentication
    private lateinit var auth: FirebaseAuth


    // One Tap client for Google Sign-In
    private var oneTapClient: SignInClient? = null

    // Request object to configure Google Sign-In options
    private lateinit var signInRequest: BeginSignInRequest

    // Variables for text animation (typewriter effect)
    private lateinit var typewriterTextView: TextView
    private val textToAnimate = "Welcome to the Post! Unleash your creativity and start crafting your stories today. Share your thoughts, experiences, and passions with the world. Your words have the power to inspire, influence, and ignite change. Dive in, write your best blogs, and let's make a difference together!"
    private var index = 0
    private val delay: Long = 100 // Delay between each character in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object to access UI elements
        binding = ActivitySignupAndLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the Google Sign-In button in the layout
        val signInButton: SignInButton = findViewById(R.id.signin_Button)

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Initialize One Tap client for Google Sign-In
        oneTapClient = Identity.getSignInClient(this)

        // Configure the Google Sign-In request
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) // Enable Google Sign-In
                    .setServerClientId(getString(R.string.WebclientID)) // Server client ID from Google Cloud
                    .setFilterByAuthorizedAccounts(false) // Show all accounts, not just authorized ones
                    .build()
            )
            .build()

        // Set up the click listener for the Google Sign-In button
        signInButton.setOnClickListener {
            signinGoogle() // Trigger Google Sign-In process when button is clicked
        }

        // Initialize the text animation (typewriter effect)
        typewriterTextView = findViewById(R.id.typewriterTextView)
        startTextAnimation()
    }

    // Function to start the text animation (typewriter effect)
    private fun startTextAnimation() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (index < textToAnimate.length) {
                    // Update the TextView with the next character in the text
                    typewriterTextView.text = textToAnimate.substring(0, index + 1)
                    index++
                    // Post the next update after a delay
                    handler.postDelayed(this, delay)
                }
            }
        })
    }

    // Function to trigger Google Sign-In
    private fun signinGoogle() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Start the Google Sign-In process
                val result = oneTapClient?.beginSignIn(signInRequest)?.await()

                if (result != null) {
                    // If a result is available, start the intent to complete the sign-in
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                    activityResultLauncher.launch(intentSenderRequest)
                } else {
                    // Show a message if no accounts were found
                    Toast.makeText(this@SignupAndLoginPage, "No accounts found.", Toast.LENGTH_LONG).show()
                }

            } catch (e: ApiException) {
                // Handle API exceptions during the sign-in process
                Toast.makeText(this@SignupAndLoginPage, "Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } catch (e: Exception) {
                // Handle any other exceptions
                Toast.makeText(this@SignupAndLoginPage, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    // Callback for handling the result of the Google Sign-In intent
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                try {
                    // Retrieve the Google Sign-In credential from the result
                    val credential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken

                    if (idToken != null) {
                        // Use the ID token to authenticate with Firebase
                        val firebaseCredentials = GoogleAuthProvider.getCredential(idToken, null)
                        binding.loginProcessProgressBar.visibility= View.VISIBLE
                        auth.signInWithCredential(firebaseCredentials)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign-in success, proceed to the main activity
                                    Toast.makeText(this, "Sign-In Complete", Toast.LENGTH_LONG).show()
                                    // Navigate to the main activity
                                    val intent= Intent(this,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    // Handle sign-in failure
                                    Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_LONG).show()
                                    // Log the exception
                                    task.exception?.let { e ->
                                        e.printStackTrace()
                                    }
                                }
                            }
                    }

                } catch (e: ApiException) {
                    // Handle API exception during credential retrieval
                    Toast.makeText(this, "Error in sign-in process: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

    override fun onStart() {
        super.onStart()

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If the user is signed in, navigate to the main activity
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

}
