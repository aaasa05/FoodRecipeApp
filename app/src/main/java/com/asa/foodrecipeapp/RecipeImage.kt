package com.asa.foodrecipeapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_images",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE // Delete images when recipe is deleted
    )]
)
data class RecipeImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long, // Foreign key to Recipe
    val imageUri: String // URI or file path of the image
)