package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.BlogAdapter
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.Post.databinding.ActivityMainBinding
import com.TLC_Developer.functions.function
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var taskListener: ListenerRegistration
    private var BlogDataSet: ArrayList<DataClass> = ArrayList()
    private val TAG = "FireStoreData"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show user details on the UI
        val profileImageUrl=FirebaseAuth.getInstance().currentUser?.photoUrl
        Picasso.get().load(profileImageUrl).into(binding.userProfile)


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
        recyclerViewAdapter = BlogAdapter(BlogDataSet,this)
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
                            blogDocumentID = document.id
                        )
                        BlogDataSet.add(dataModel) // Add the blog data to the ArrayList
                    }

                    // Sort the blogs by date in descending order
                    BlogDataSet.sortByDescending {
                        function().convertStringToDate(it.BlogDateAndTime)?.time
                    }

                    // Notify the adapter that the data has changed
                    recyclerViewAdapter.notifyDataSetChanged()
                } else {
                    // Log if no data is found
                    Log.d(TAG, "Current data: null")
                }
            }


    }

}
