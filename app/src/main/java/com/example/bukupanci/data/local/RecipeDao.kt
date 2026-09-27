package com.example.bukupanci.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bukupanci.data.model.Ingredient
import com.example.bukupanci.data.model.Recipe
import com.example.bukupanci.data.model.RecipeWithDetails
import com.example.bukupanci.data.model.Step
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun observeAll(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun search(query: String): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun observeDetail(recipeId: Int): Flow<RecipeWithDetails?>

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Insert
    suspend fun insertIngredients(items: List<Ingredient>)

    @Insert
    suspend fun insertSteps(items: List<Step>)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsOf(recipeId: Int)

    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsOf(recipeId: Int)
}