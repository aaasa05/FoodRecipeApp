package com.asa.foodrecipeapp

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Recipe(
    val title: String,
    val description: String
) {
    // Convert a Recipe object to JSON
    fun toJson(): String {
        return Gson().toJson(this)
    }

    // Convert a JSON string to a Recipe object
    companion object {
        fun fromJson(json: String): Recipe {
            return Gson().fromJson(json, Recipe::class.java)
        }
    }
}