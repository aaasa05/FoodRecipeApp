package com.asa.foodrecipeapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Get the passed data from the intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        // Set the data to the views
        val recipeTitle: TextView = findViewById(R.id.recipeTitle)
        val recipeDescription: TextView = findViewById(R.id.recipeDescription)

        recipeTitle.text = title
        recipeDescription.text = description
    }
}