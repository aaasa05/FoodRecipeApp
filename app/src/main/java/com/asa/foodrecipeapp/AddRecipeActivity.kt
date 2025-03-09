package com.asa.foodrecipeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AddRecipeActivity : AppCompatActivity() {

    private val imageUris = mutableListOf<Uri>() // Store selected image URIs

    // Register for activity result (image picker)
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUris.add(uri)
                displayImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        setTitle("Add Recipe")

        // Initialize views
        val recipeTitleInput: EditText = findViewById(R.id.recipeTitleInput)
        val recipeDescriptionInput: EditText = findViewById(R.id.recipeDescriptionInput)
        val addImageButton: Button = findViewById(R.id.addImageButton)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Add Image Button Click Listener
        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImageLauncher.launch(intent)
        }

        // Save Button Click Listener
        saveButton.setOnClickListener {
            val title = recipeTitleInput.text.toString()
            val description = recipeDescriptionInput.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Pass the new recipe and image URIs back to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("title", title)
                    putExtra("description", description)
                    putParcelableArrayListExtra("imageUris", ArrayList(imageUris))
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to display selected images
    private fun displayImage(uri: Uri) {
        val imageContainer: LinearLayout = findViewById(R.id.imageContainer)
        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            400 // Set a fixed height for the image
        )
        imageView.adjustViewBounds = true
        Glide.with(this).load(uri).into(imageView)
        imageContainer.addView(imageView)
    }
}