package com.example.firebaselearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.firebaselearning.daos.PostDao

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao:PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postDao = PostDao()
        val postButton = findViewById<Button>(R.id.postButton)
        postButton.setOnClickListener {
            val postInput = findViewById<EditText>(R.id.postInput)
            val input = postInput.text.toString().trim()
            if (input.isNotEmpty() ){
                postDao.addPost(input)
                finish()
            }
        }

    }
}