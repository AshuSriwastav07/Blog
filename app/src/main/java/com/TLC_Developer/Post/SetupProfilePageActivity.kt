package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.functions.function
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SetupProfilePageActivity : AppCompatActivity() {

    private lateinit var firebaseFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val TAG = "ProfileLogs"

    // UI Items
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var InstagramURLEditText: TextInputEditText
    private lateinit var FacebookEditText: TextInputEditText
    private lateinit var XEditText: TextInputEditText
    private lateinit var YouTubeEditText: TextInputEditText
    private lateinit var saveProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile_page)

        // Initialize Firebase
        firebaseFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid.toString()
        val userProfileUrl=auth.currentUser?.photoUrl

        // Initialize UI items
        userNameEditText = findViewById(R.id.profileSetupEnterUserName)
        InstagramURLEditText = findViewById(R.id.profileSetupEnterInstaLink)
        FacebookEditText = findViewById(R.id.profileSetupEnterFBLink)
        XEditText = findViewById(R.id.profileSetupEnterXLink)
        YouTubeEditText = findViewById(R.id.profileSetupEnterYTLink)
        saveProfileButton = findViewById(R.id.saveProfileButton)

        // Set Data in Edit Texts
        function().getUserSpecificData(currentUserID,"userName") { userName ->
            userNameEditText.setText(userName)
        }

        // Fetch existing profile data
        firebaseFireStore.collection("usersDetails").document(currentUserID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Populate the EditTexts with existing data
                    InstagramURLEditText.setText(document.getString("InstagramLink"))
                    FacebookEditText.setText(document.getString("FacebookLink"))
                    XEditText.setText(document.getString("XLink"))
                    YouTubeEditText.setText(document.getString("YoutubeLink"))
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error fetching profile data: ", e)
            }

        // Save profile button click listener
        saveProfileButton.setOnClickListener {
            val userName = userNameEditText.text.toString()
            var instaUrl = InstagramURLEditText.text.toString()
            var facebookUrl = FacebookEditText.text.toString()
            var xUrl = XEditText.text.toString()
            var ytUrl = YouTubeEditText.text.toString()

            // Check if fields are empty and if so, retain the previous data
            firebaseFireStore.collection("usersDetails").document(currentUserID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        if (instaUrl.isEmpty()) {
                            instaUrl = document.getString("InstagramLink").orEmpty()
                        }
                        if (facebookUrl.isEmpty()) {
                            facebookUrl = document.getString("FacebookLink").orEmpty()
                        }
                        if (xUrl.isEmpty()) {
                            xUrl = document.getString("XLink").orEmpty()
                        }
                        if (ytUrl.isEmpty()) {
                            ytUrl = document.getString("YoutubeLink").orEmpty()
                        }

                        // Create a map to store profile data
                        val profileData = hashMapOf(
                            "userName" to userName,
                            "InstagramLink" to instaUrl,
                            "FacebookLink" to facebookUrl,
                            "XLink" to xUrl,
                            "YoutubeLink" to ytUrl,
                            "ProfileSetupStatus" to "Done",
                            "UserprofileUrl" to userProfileUrl
                        )

                        // Update the user's profile data in Firestore

                        function().UpdateUserName(userName)

                        firebaseFireStore.collection("usersDetails").document(currentUserID)
                            .set(profileData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile is Complete", Toast.LENGTH_LONG).show()
                                // Navigate to MainActivity after completing the profile
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "Error saving profile data: ", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error fetching profile data: ", e)
                }



        }
    }
}
