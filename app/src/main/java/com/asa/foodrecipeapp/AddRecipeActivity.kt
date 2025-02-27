package com.asa.foodrecipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_recipe)

        // Initialize views
        val recipeTitleInput: EditText = findViewById(R.id.recipeTitleInput)
        val recipeDescriptionInput: EditText = findViewById(R.id.recipeDescriptionInput)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Save Button Click Listener
        saveButton.setOnClickListener {
            val title = recipeTitleInput.text.toString()
            val description = recipeDescriptionInput.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Pass the new recipe back to MainActivity
                val resultIntent = Intent().apply {
                    putExtra("title", title)
                    putExtra("description", description)
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
        }
}
