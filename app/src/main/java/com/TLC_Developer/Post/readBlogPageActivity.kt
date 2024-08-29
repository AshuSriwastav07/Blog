package com.TLC_Developer.Post

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityReadBlogPageBinding
import com.TLC_Developer.functions.functionsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class readBlogPageActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityReadBlogPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding
        binding = ActivityReadBlogPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        // Retrieve the blog data passed through the intent
        val blogData: ArrayList<String>? = intent?.getStringArrayListExtra("blogData")

        // Use binding to access the views and set the data if needed

//        Toast.makeText(this,blogData?.size.toString(),Toast.LENGTH_LONG).show()

        if (blogData != null) {  //Check is data that received is null or not and assign data variable according
            val blogTitle=blogData[0]
            val blogDataTime=blogData[1]
            val blogBGImageUrl=blogData[2]
            val blogBodyContent=blogData[3]
            val blogUserProfileImageUrl=blogData[4]
            val blogWriterName=blogData[5]
            val blogDocumentID=blogData[6]
            val currentLoginUserName=blogData[8]
            val currentUserEmailAsUserID=FirebaseAuth.getInstance().currentUser?.email.toString()

            //get UserName


            //make ArrayList to store comments and liked users data
            val likesList: ArrayList<String> = arrayListOf()
            val commentsList: ArrayList<String> = arrayListOf()

            //Firestore reference
            val blogDocumentRef = FirebaseFirestore.getInstance().collection("BlogsData").document(blogDocumentID)

           blogDocumentRef  //check and update data in real time in app using Snapshot Listener
               .addSnapshotListener { document, e ->
                    if (e != null) {
                        Log.w("commentsList", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    likesList.clear() //clear previous data is available
                    commentsList.clear()

                   likesList.addAll(document?.get("likedBy") as ArrayList<String>) //Add data in list fetch from firestore
                   commentsList.addAll(document?.get("comments") as ArrayList<String>)

                    if(likesList.contains(currentUserEmailAsUserID)){ //first check if user is already liked the blog or not iy yes make it blue like
                        binding.LikeButton.setImageResource(R.mipmap.liked)
                    }

                   binding.LikeButton.setOnClickListener {  //Check if user click again on on like button so make blog dislike
                       if(likesList.contains(currentUserEmailAsUserID)){
                           binding.LikeButton.setImageResource(R.mipmap.like)
                           likesList.remove(currentUserEmailAsUserID)
                           blogDocumentRef.update("likedBy", likesList)


                       }else{ //else keep it blue like
                           binding.LikeButton.setImageResource(R.mipmap.liked)
                           likesList.add(currentUserEmailAsUserID)
                           blogDocumentRef.update("likedBy", likesList)

                       }

                   }

                   binding.countLikes.text=likesList.size.toString()  //count and show likes and comments in text view
                   binding.countComments.text=commentsList.size.toString()
                }


            //set all the other blog body data in reading page
            binding.BlogReadingTitle.text= blogTitle
            binding.BlogReadingDateTime.text= blogDataTime

            functionsManager().loadBlogBGImages(blogBGImageUrl,binding.BlogReadingImageView) //call function and load BG Image of the blog

            val result = Html.fromHtml(blogBodyContent) //if user write content using HTML tags to show accordingly
            binding.BlogReadingBody.text=result

            binding.BlogReadingWriterName.text= blogWriterName
            functionsManager().loadProfileImagesImage(blogUserProfileImageUrl,binding.readBlogUserProfileImage)

            binding.commentButton.setOnClickListener {  //open comment section pass current reading blog id and current Login user Name for adding in comment using by
                val commentsFragment = showAndWriteComments()

                // Pass the blog ID to the fragment
                val bundle = Bundle()
                bundle.putString("blogId", blogDocumentID)
                bundle.putString("commentByUserName", currentLoginUserName)
                commentsFragment.arguments = bundle

                commentsFragment.show(supportFragmentManager, "CommentsFragment")


            }


            }

    }
}
