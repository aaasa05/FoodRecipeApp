package com.asa.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        // Initialize views
        val recipeTitleInput: EditText = findViewById(R.id.recipeTitleInput)
        val recipeDescriptionInput: EditText = findViewById(R.id.recipeDescriptionInput)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Get the passed data from the intent
        val position = intent.getIntExtra("position", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        // Pre-fill the inputs with the current recipe data
        recipeTitleInput.setText(title)
        recipeDescriptionInput.setText(description)

        // Save Button Click Listener
        saveButton.setOnClickListener {
            val updatedTitle = recipeTitleInput.text.toString()
            val updatedDescription = recipeDescriptionInput.text.toString()

            if (updatedTitle.isNotEmpty() && updatedDescription.isNotEmpty()) {
                // Pass the updated recipe back to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("title", updatedTitle)
                    putExtra("description", updatedDescription)
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            }
        }
    }
}