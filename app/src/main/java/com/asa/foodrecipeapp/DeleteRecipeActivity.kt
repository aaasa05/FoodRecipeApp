package com.asa.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class DeleteRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_recipe)

        // Get the list of recipes from the intent
        val recipeList = intent.getStringArrayListExtra("recipeList") ?: ArrayList()

        // Initialize views
        val deleteRecipeListView: ListView = findViewById(R.id.deleteRecipeListView)
        val confirmDeleteButton: Button = findViewById(R.id.confirmDeleteButton)

        // Set up the ListView adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recipeList)
        deleteRecipeListView.adapter = adapter

        // Handle item selection in ListView
        var selectedPosition = -1
        deleteRecipeListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedPosition = position
        }

        // Confirm Delete Button Click Listener
        confirmDeleteButton.setOnClickListener {
            if (selectedPosition != -1) {
                // Pass the selected position back to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("position", selectedPosition)
                }

                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Please select a recipe to delete!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}