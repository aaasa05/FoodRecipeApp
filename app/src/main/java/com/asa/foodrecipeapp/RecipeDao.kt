package com.asa.foodrecipeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>> // Use Flow for reactive updates

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Long)

    @Insert
    suspend fun insertImage(image: RecipeImage)

    @Query("SELECT * FROM recipe_images WHERE recipeId = :recipeId")
    fun getImagesForRecipe(recipeId: Long): Flow<List<RecipeImage>>

    @Query("DELETE FROM recipe_images WHERE recipeId = :recipeId")
    suspend fun deleteImagesForRecipe(recipeId: Long)
}