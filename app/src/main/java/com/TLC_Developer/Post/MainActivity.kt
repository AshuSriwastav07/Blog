package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.BlogAdapter
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.Post.databinding.ActivityMainBinding
import com.TLC_Developer.functions.functionsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var taskListener: ListenerRegistration
    private var BlogDataSet: ArrayList<DataClass> = ArrayList()
    private val TAG = "FireStoreData"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: BlogAdapter
    val auth = FirebaseAuth.getInstance()
    private var doubleBackToExitPressedOnce = false


    // Handle the back button press
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity() // Close the app
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        // Reset the back press flag after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Show user details on the UI
        val profileImageUrl=FirebaseAuth.getInstance().currentUser?.photoUrl
        functionsManager().loadProfileImagesImage(profileImageUrl.toString(),binding.userProfile)

        // Set up click listeners for buttons
        binding.writeBlogButton.setOnClickListener {
            // Navigate to the WriteBlogPage activity when the button is clicked
            val intent = Intent(this, WriteBlogPage::class.java)
            startActivity(intent)
        }

        binding.profileButton.setOnClickListener {
            // Navigate to the UserProfilePageActivity when the button is clicked
            val intent = Intent(this, UserProfilePageActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        // Initialize and set up the RecyclerView for displaying blog posts
        blogRecyclerView = findViewById(R.id.blogRecyclerView)
        val viewManager = LinearLayoutManager(this)


        // Create an adapter for the RecyclerView
        recyclerViewAdapter = BlogAdapter(BlogDataSet,this,)
        blogRecyclerView.apply {
            setHasFixedSize(true) // Optimize RecyclerView performance
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }

        // Listen for changes in the Firestore collection "BlogsData"
        taskListener = db.collection("BlogsData")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Log an error if there is an issue with the snapshot listener
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Clear the existing blog data
                    BlogDataSet.clear()
                    for (document in snapshots) {
                        // Create a DataClass object for each blog document
                        val dataModel = DataClass(
                            blogTitle = document.getString("title") ?: "",
                            blogBody = document.getString("body") ?: "",
                            blogTags = document.getString("tags") ?: "",
                            blogUserID = document.getString("userID") ?: "",
                            blogDateAndTime = document.getString("BlogDateAndTime") ?: "",
                            blogImageURL = document.getString("BlogImageURL") ?: "",
                            blogUserProfileUrl = document.getString("BlogUserProfileUrl") ?: "",
                            blogDocumentID = document.id,
                            BlogUserName = document.getString("userName").toString()
                        )
                        BlogDataSet.add(dataModel) // Add the blog data to the ArrayList
                    }
                    

                    // Sort the blogs by date in descending order
                    BlogDataSet.sortByDescending {
                        functionsManager().convertStringToDate(it.BlogDateAndTime)?.time
                    }

                    // Notify the adapter that the data has changed
                    recyclerViewAdapter.notifyDataSetChanged()
                } else {
                    // Log if no data is found
                    Log.d(TAG, "Current data: null")
                }
            }

        makeDefaultProfile()
    }


    //it will create default profile for user when in login 1st time
    fun makeDefaultProfile(){
        val firebaseFireStore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val documentName = auth.currentUser?.email ?: return  // User email as document name
        val userProfileUrl = auth.currentUser?.photoUrl
        val GoogleUserName = auth.currentUser?.displayName

        // Create or update user profile document
        val profileData = hashMapOf(
            "userName" to (GoogleUserName),
            "InstagramLink" to "",
            "FacebookLink" to "",
            "XLink" to "",
            "YoutubeLink" to "",
            "ProfileSetupStatus" to "Complete",
            "UserprofileUrl" to userProfileUrl.toString(),
            "UserEmail" to documentName

        )

        // Check if the document exists
        firebaseFireStore.collection("usersDetails")
            .document(documentName)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // If the document doesn't exist, create it with default data
                    firebaseFireStore.collection("usersDetails")
                        .document(documentName)
                        .set(profileData)
                        .addOnSuccessListener {
                            Log.d(TAG, "Document successfully created!")
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "Error creating document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error checking document existence", e)
            }

    }

}
