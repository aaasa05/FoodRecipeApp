package com.asa.foodrecipeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class EditRecipeActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_edit_recipe)

        setTitle("Edit Recipe")

        // Initialize views
        val recipeTitleInput: EditText = findViewById(R.id.recipeTitleInput)
        val recipeDescriptionInput: EditText = findViewById(R.id.recipeDescriptionInput)
        val addImageButton: Button = findViewById(R.id.addImageButton)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Get the passed data from the intent
        val position = intent.getIntExtra("position", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        // Pre-fill the inputs with the current recipe data
        recipeTitleInput.setText(title)
        recipeDescriptionInput.setText(description)

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
            val updatedTitle = recipeTitleInput.text.toString()
            val updatedDescription = recipeDescriptionInput.text.toString()

            if (updatedTitle.isNotEmpty() && updatedDescription.isNotEmpty()) {
                // Pass the updated recipe and image URIs back to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("title", updatedTitle)
                    putExtra("description", updatedDescription)
                    putParcelableArrayListExtra("imageUris", ArrayList(imageUris))
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
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