package com.asa.foodrecipeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class RecipeAdapter(
    context: Context,
    private val recipeList: MutableList<Recipe>,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onEditClickListener: (Int) -> Unit,
    private val onItemClickListener: (Int) -> Unit // Add item click listener
) : ArrayAdapter<Recipe>(context, R.layout.recipe_item, recipeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)

        val recipe = recipeList[position]

        // Bind views
        val recipeTitle: TextView = view.findViewById(R.id.recipeTitle)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)

        // Set recipe title
        recipeTitle.text = recipe.title

        // Handle Edit Button Click
        editButton.setOnClickListener {
            onEditClickListener(position)
        }

        // Handle Delete Button Click
        deleteButton.setOnClickListener {
            onDeleteClickListener(position)
        }

        // Handle Item Click
        view.setOnClickListener {
            onItemClickListener(position)
        }

        return view
    }
}