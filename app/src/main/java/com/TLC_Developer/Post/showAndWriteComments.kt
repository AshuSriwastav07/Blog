package com.TLC_Developer.Post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.TLC_Developer.functions.functionsManager

class showAndWriteComments : DialogFragment() {

    // This method is called when the fragment's view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment (create the view hierarchy from the XML layout)
        val view: View = inflater.inflate(R.layout.fragment_show_and_write_comments, container, false)

        // Find the ListView where comments will be displayed
        val commentListView: ListView = view.findViewById(R.id.commentListView)

        // Find the EditText where users can type their comments
        val enterComment: EditText = view.findViewById(R.id.typeComment)

        // Get the blog ID and username passed to this fragment through arguments
        val blogId = arguments?.getString("blogId").toString()
        val userName = arguments?.getString("commentByUserName").toString()

        // Find the Button that users will press to submit their comments
        val submitComment: Button = view.findViewById(R.id.submitComment)

        // Call the function to get and display comments, and handle new comment submissions
        functionsManager().getComments(requireContext(), blogId, commentListView, enterComment, submitComment, userName)

        return view // Return the view to be displayed
    }
}
