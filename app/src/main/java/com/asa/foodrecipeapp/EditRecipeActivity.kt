package com.asa.foodrecipeapp
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditRecipeActivity : AppCompatActivity() {
    private val imageUris = mutableListOf<Uri>() // Store selected image URIs
    private lateinit var database: AppDatabase
    private var recipeId: Long = -1

    // Register for activity result (image picker)
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Take persistent permission right away
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e("EditRecipeActivity", "Failed to take permission: ${e.message}")
                }

                imageUris.add(uri)
                displayImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setTitle("Edit Recipe")

        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Initialize views
        val recipeTitleInput: EditText = findViewById(R.id.recipeTitleInput)
        val recipeDescriptionInput: EditText = findViewById(R.id.recipeDescriptionInput)
        val addImageButton: Button = findViewById(R.id.addImageButton)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Get the passed data from the intent
        val position = intent.getIntExtra("position", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        recipeId = intent.getLongExtra("recipeId", -1)

        // Pre-fill the inputs with the current recipe data
        recipeTitleInput.setText(title)
        recipeDescriptionInput.setText(description)

        // Load existing images
        if (recipeId != -1L) {
            loadExistingImages(recipeId)
        }

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

    // Load existing images for the recipe
    private fun loadExistingImages(recipeId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // The collect function operates differently than a one-time fetch
                database.recipeDao().getImagesForRecipe(recipeId).collect { images ->
                    withContext(Dispatchers.Main) {
                        // Clear image container first to avoid duplicates if this is called multiple times
                        val imageContainer: LinearLayout = findViewById(R.id.imageContainer)
                        imageContainer.removeAllViews()

                        // Clear our tracked URIs and rebuild the list
                        imageUris.clear()

                        // Add each image from the database
                        images.forEach { image ->
                            try {
                                val uri = Uri.parse(image.imageUri)
                                imageUris.add(uri) // Add to our tracked list
                                displayImage(uri)
                                Log.d("EditRecipeActivity", "Loaded image URI: $uri")
                            } catch (e: Exception) {
                                Log.e("EditRecipeActivity", "Error parsing URI: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("EditRecipeActivity", "Error loading images: ${e.message}")
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