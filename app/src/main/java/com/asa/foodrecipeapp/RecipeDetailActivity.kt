package com.asa.foodrecipeapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        setTitle("Recipe Details")

        // Initialize Room Database
        database = AppDatabase.getDatabase(this)

        // Get the recipe ID from the intent
        val recipeId = intent.getLongExtra("recipeId", -1)

        if (recipeId != -1L) {
            lifecycleScope.launch(Dispatchers.IO) {
                // Collect the list of recipes
                database.recipeDao().getAllRecipes().collect { recipes ->
                    // Find the recipe with the matching ID
                    val recipe = recipes.find { it.id == recipeId }

                    // Collect the list of images for the recipe
                    database.recipeDao().getImagesForRecipe(recipeId).collect { images ->
                        runOnUiThread {
                            // Display recipe details
                            findViewById<TextView>(R.id.recipeTitle).text = recipe?.title
                            findViewById<TextView>(R.id.recipeDescription).text = recipe?.description

                            // Display images
                            val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)
                            imageContainer.removeAllViews() // Clear existing views
                            images.forEach { image ->
                                try {
                                    val imageView = ImageView(this@RecipeDetailActivity)

                                    // Now we can safely use a larger height value
                                    imageView.layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        800  // Increased height value
                                    )

                                    // Add margins for better spacing
                                    val params = imageView.layoutParams as LinearLayout.LayoutParams
                                    params.setMargins(0, 0, 0, 24)  // Add margin at the bottom

                                    imageView.adjustViewBounds = true
                                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                                    val uri = Uri.parse(image.imageUri)

                                    // Log the URI for debugging
                                    Log.d("RecipeDetailActivity", "Loading image from URI: $uri")

                                    // Add error handling to Glide
                                    Glide.with(this@RecipeDetailActivity)
                                        .load(uri)
                                        .error(android.R.drawable.ic_dialog_alert)  // Show an icon if loading fails
                                        .into(imageView)

                                    imageContainer.addView(imageView)
                                } catch (e: Exception) {
                                    Log.e("RecipeDetailActivity", "Error loading image: ${e.message}", e)
                                    Toast.makeText(this@RecipeDetailActivity,
                                        "Failed to load an image",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}