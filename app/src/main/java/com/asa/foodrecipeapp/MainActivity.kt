package com.asa.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var adapter: RecipeAdapter
    private lateinit var recipePrefs: RecipePrefs

    // Register for activity results
    private val addRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title") ?: ""
            val description = data?.getStringExtra("description") ?: ""
            recipeList.add(Recipe(title, description))
            updateAdapter() // Refresh the adapter with the updated list
            recipePrefs.saveRecipes(recipeList) // Save to SharedPreferences
        }
    }

    private val editRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val position = data?.getIntExtra("position", -1) ?: -1
            val title = data?.getStringExtra("title") ?: ""
            val description = data?.getStringExtra("description") ?: ""
            if (position != -1) {
                recipeList[position] = Recipe(title, description)
                updateAdapter() // Refresh the adapter with the updated list
                recipePrefs.saveRecipes(recipeList) // Save to SharedPreferences
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences helper
        recipePrefs = RecipePrefs(this)

        // Load recipes from SharedPreferences
        recipeList = recipePrefs.getRecipes().toMutableList()

        // If no recipes are found, initialize with default recipes
        if (recipeList.isEmpty()) {
            recipeList = mutableListOf(
                Recipe("Pasta", "Delicious Italian pasta with tomato sauce."),
                Recipe("Pizza", "Classic Margherita pizza with fresh basil."),
                Recipe("Burger", "Juicy beef burger with cheese and veggies.")
            )
            recipePrefs.saveRecipes(recipeList) // Save default recipes
        }

        // Initialize the adapter
        updateAdapter()

        // Get the ListView and set the adapter
        val recipeListView: ListView = findViewById(R.id.recipeListView)
        recipeListView.adapter = adapter

        // Add Button Click Listener
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            addRecipeLauncher.launch(intent)
        }
    }

    // Helper function to update the adapter
    private fun updateAdapter() {
        adapter = RecipeAdapter(
            this,
            recipeList,
            onDeleteClickListener = { position ->
                // Handle Delete Button Click
                recipeList.removeAt(position)
                updateAdapter() // Refresh the adapter with the updated list
                recipePrefs.saveRecipes(recipeList) // Save to SharedPreferences
            },
            onEditClickListener = { position ->
                // Handle Edit Button Click
                val recipe = recipeList[position]
                val intent = Intent(this, EditRecipeActivity::class.java).apply {
                    putExtra("position", position)
                    putExtra("title", recipe.title)
                    putExtra("description", recipe.description)
                }
                editRecipeLauncher.launch(intent)
            },
            onItemClickListener = { position ->
                // Handle Item Click for Details
                val recipe = recipeList[position]
                val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra("title", recipe.title)
                    putExtra("description", recipe.description)
                }
                startActivity(intent)
            }
        )
        val recipeListView: ListView = findViewById(R.id.recipeListView)
        recipeListView.adapter = adapter
    }
}