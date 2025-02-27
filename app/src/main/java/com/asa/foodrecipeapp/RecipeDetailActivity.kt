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

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

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
                                val imageView = ImageView(this@RecipeDetailActivity)
                                imageView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    400 // Set a fixed height for the image
                                )
                                imageView.adjustViewBounds = true
                                Glide.with(this@RecipeDetailActivity).load(Uri.parse(image.imageUri)).into(imageView)
                                imageContainer.addView(imageView)
                            }
                        }
                    }
                }
            }
        }
    }
}