package com.TLC_Developer.Post

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityReadBlogPageBinding
import com.TLC_Developer.functions.functionsManager

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
                commentsFragment.arguments = bundle

                commentsFragment.show(supportFragmentManager, "CommentsFragment")


            }

//            functionsManager().totalLiked(this, blogData.get(6),binding.countLikes)


            }

    }
}
