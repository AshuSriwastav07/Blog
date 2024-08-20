package com.TLC_Developer.Post

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.BlogAdapter
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.Post.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var taskListener: ListenerRegistration
    private var BlogDataSet:ArrayList<DataClass> = ArrayList()
    private val TAG = "FireStoreData"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ShowUserDetails()

        binding.writeBlogButton.setOnClickListener{
            val intent=Intent(this, WriteBlogPage::class.java)
            startActivity(intent)

        }


    }

    override fun onStart() {
        super.onStart()
        blogRecyclerView = findViewById(R.id.blogRecyclerView)
        val viewManager = LinearLayoutManager(this)
        //Adapter Connect
        recyclerViewAdapter = BlogAdapter(BlogDataSet)
        blogRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }

        //Get Data in DataModel Class and Store it

        taskListener = db.collection("BlogsData")  //Get Tasks Data from FireStore
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                //Check fetch Data is null or not
                if (snapshots != null) {
                    BlogDataSet.clear()     //clear ArrayList
                    for (document in snapshots) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        val dataModel = DataClass(
                            //get data from document and add into given dataModel var
                            BlogTitle = document.getString("title") ?: document.id,
                            BlogBody = document.getString("body") ?: "",
                            BlogTags = document.getString("tags") ?: "",
                            BlogUserID = document.getString("userID") ?: "",
                            BlogDateAndTime = document.getString("BlogDateAndTime") ?: "",
                            BlogImageURL = document.getString("BlogImageURL") ?: "",
                            BlogWriterName = document.getString("writerName") ?:"",
                            BlogUserProfileUrl = document.getString("BlogUserProfileUrl") ?:""

                        )
                        BlogDataSet.add(dataModel) //Add data in ArrayList

                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }


        getBlogData()


    }


    private fun getBlogData(){
        db.collection("BlogsData")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    Log.d("BlogDataRetrieve","${document.id} => ${document.data}")
                }


            }

    }

    private fun ShowUserDetails() {
        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.uid
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified
//           binding.AppTitle.text = name
//            binding.txtEmail.text = email
//            if (emailVerified) {
//                binding.txtStatus.text = "Verified Email"
//            }
            var image: Bitmap? = null
            val imageURL = photoUrl.toString()
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    val `in`=java.net.URL(imageURL).openStream()
                    image=BitmapFactory.decodeStream(`in`)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            runOnUiThread{
                try {
                    Thread.sleep(1000)
                    binding.userProfile.setImageBitmap(image)

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}