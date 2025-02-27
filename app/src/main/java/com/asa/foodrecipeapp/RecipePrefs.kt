package com.asa.foodrecipeapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipePrefs(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save the list of recipes to SharedPreferences
    fun saveRecipes(recipes: List<Recipe>) {
        val json = gson.toJson(recipes)
        sharedPreferences.edit().putString("recipes", json).apply()
    }

    // Retrieve the list of recipes from SharedPreferences
    fun getRecipes(): List<Recipe> {
        val json = sharedPreferences.getString("recipes", null)
        return if (json != null) {
            val type = object : TypeToken<List<Recipe>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}