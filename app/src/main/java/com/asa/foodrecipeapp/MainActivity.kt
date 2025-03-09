package com.asa.foodrecipeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var adapter: RecipeAdapter
    private lateinit var database: AppDatabase

    // Register for activity results
    private val addRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title") ?: ""
            val description = data?.getStringExtra("description") ?: ""
            val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris") ?: ArrayList()

            // Take persistent permissions before saving
            takePersistentUriPermissions(imageUris)

            lifecycleScope.launch(Dispatchers.IO) {
                // Insert the recipe
                val recipeId = database.recipeDao().insertRecipe(Recipe(title = title, description = description))

                // Insert the images
                imageUris.forEach { uri ->
                    database.recipeDao().insertImage(RecipeImage(recipeId = recipeId, imageUri = uri.toString()))
                }

                loadRecipes()
            }
        }
    }

    private val editRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val position = data?.getIntExtra("position", -1) ?: -1
            val title = data?.getStringExtra("title") ?: ""
            val description = data?.getStringExtra("description") ?: ""
            val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris") ?: ArrayList()

            // Take persistent permissions
            takePersistentUriPermissions(imageUris)

            if (position != -1) {
                val recipe = recipeList[position]
                lifecycleScope.launch(Dispatchers.IO) {
                    // Update the recipe
                    database.recipeDao().updateRecipe(Recipe(id = recipe.id, title = title, description = description))

                    // Delete existing images for the recipe
                    database.recipeDao().deleteImagesForRecipe(recipe.id)

                    // Insert the new images
                    imageUris.forEach { uri ->
                        database.recipeDao().insertImage(RecipeImage(recipeId = recipe.id, imageUri = uri.toString()))
                    }

                    loadRecipes()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle("Recipe List")

        // Initialize Room Database
        database = AppDatabase.getDatabase(this)

        // Initialize the recipe list
        recipeList = mutableListOf()

        // Initialize the adapter
        updateAdapter()

        // Get the ListView and set the adapter
        val recipeListView: ListView = findViewById(R.id.recipeListView)
        recipeListView.adapter = adapter

        // Load recipes
        loadRecipes()

        // Add Button Click Listener
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            addRecipeLauncher.launch(intent)
        }
    }

    // Helper function to load recipes from the database
    private fun loadRecipes() {
        lifecycleScope.launch {
            database.recipeDao().getAllRecipes().collect { recipes ->
                recipeList.clear()
                recipeList.addAll(recipes)
                updateAdapter()
            }
        }
    }

    // Helper function to update the adapter
    private fun updateAdapter() {
        adapter = RecipeAdapter(
            this,
            recipeList,
            onDeleteClickListener = { position ->
                val recipe = recipeList[position]
                lifecycleScope.launch(Dispatchers.IO) {
                    database.recipeDao().deleteRecipe(recipe.id)
                    database.recipeDao().deleteImagesForRecipe(recipe.id)
                }
            },
            onEditClickListener = { position ->
                val recipe = recipeList[position]
                val intent = Intent(this, EditRecipeActivity::class.java).apply {
                    putExtra("position", position)
                    putExtra("title", recipe.title)
                    putExtra("description", recipe.description)
                    putExtra("recipeId", recipe.id)  // Pass the recipe ID
                }
                editRecipeLauncher.launch(intent)
            },
            onItemClickListener = { position ->
                val recipe = recipeList[position]
                val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra("recipeId", recipe.id)
                }
                startActivity(intent)
            }
        )
        val recipeListView: ListView = findViewById(R.id.recipeListView)
        recipeListView.adapter = adapter
    }

    private fun takePersistentUriPermissions(imageUris: ArrayList<Uri>) {
        imageUris.forEach { uri ->
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Log the error but continue with other URIs
                e.printStackTrace()
            }
        }
    }

}