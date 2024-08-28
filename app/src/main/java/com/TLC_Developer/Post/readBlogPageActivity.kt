package com.TLC_Developer.Post

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityReadBlogPageBinding
import com.TLC_Developer.functions.functionsManager
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
        if (blogData != null) {

            val likesList: ArrayList<String> = arrayListOf()
            val commentsList: ArrayList<String> = arrayListOf()
            val blogDocumentRef = FirebaseFirestore.getInstance().collection("BlogsData").document(blogData.get(6))

           blogDocumentRef
               .addSnapshotListener { document, e ->
                    if (e != null) {
                        Log.w("commentsList", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    likesList.clear()
                    commentsList.clear()

                    likesList.addAll(document?.get("likedBy") as ArrayList<String>)
                   commentsList.addAll(document?.get("comments") as ArrayList<String>)

                    if(likesList.contains(blogData.get(7))){
                        binding.LikeButton.setImageResource(R.mipmap.liked)
                    }

                   binding.LikeButton.setOnClickListener {
                       if(likesList.contains(blogData.get(7))){
                           binding.LikeButton.setImageResource(R.mipmap.like)
                           likesList.remove(blogData.get(7))
                           blogDocumentRef.update("likedBy", likesList)
                       }else{
                           binding.LikeButton.setImageResource(R.mipmap.liked)
                           likesList.add(blogData.get(7))
                           blogDocumentRef.update("likedBy", likesList)
                       }

                   }

                   binding.countLikes.text=likesList.size.toString()
                   binding.countComments.text=commentsList.size.toString()
                }


            binding.BlogReadingTitle.text= blogData[0]
            binding.BlogReadingDateTime.text= blogData[1]

            functionsManager().loadBlogImagesImage(blogData[2],binding.BlogReadingImageView)

            val result = Html.fromHtml(blogData[3])
            binding.BlogReadingBody.text=result

            binding.BlogReadingWriterName.text= blogData[5]
            functionsManager().loadProfileImagesImage(blogData[4],binding.readBlogUserProfileImage)

            binding.commentButton.setOnClickListener {
                val commentsFragment = showAndWriteComments()

                // Pass the blog ID to the fragment
                val bundle = Bundle()
                bundle.putString("blogId", blogData[6])
                bundle.putString("blogUserName", blogData[5])
                commentsFragment.arguments = bundle

                commentsFragment.show(supportFragmentManager, "CommentsFragment")


            }

//            functionsManager().countCommentAndLikes(this, blogData.get(6),binding.countLikes,binding.countComments)

//            binding.LikeButton.setOnClickListener{
//                functionsManager().likeTheBlogAndLikedBy(this,blogData.get(6),blogData.get(7),binding.LikeButton)
//            }


            }

    }
}
