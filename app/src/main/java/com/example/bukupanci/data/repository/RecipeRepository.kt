package com.example.bukupanci.data.repository

import androidx.room.withTransaction
import com.example.bukupanci.data.local.AppDatabase
import com.example.bukupanci.data.local.RecipeDao
import com.example.bukupanci.data.model.Ingredient
import com.example.bukupanci.data.model.Recipe
import com.example.bukupanci.data.model.RecipeWithDetails
import com.example.bukupanci.data.model.Step
import kotlinx.coroutines.flow.Flow

data class StepDraft(val title: String, val description: String)

interface RecipeRepository {
    val recipes: Flow<List<Recipe>>
    fun search(query: String): Flow<List<Recipe>>
    fun observeDetail(recipeId: Int): Flow<RecipeWithDetails?>

    suspend fun saveNewRecipe(recipe: Recipe, ingredients: List<String>, steps: List<StepDraft>)
    suspend fun updateRecipe(recipe: Recipe, ingredients: List<String>, steps: List<StepDraft>)
    suspend fun deleteRecipe(recipe: Recipe)
}

class RoomRecipeRepository(
    private val dao: RecipeDao,
    private val database: AppDatabase
) : RecipeRepository {

    override val recipes: Flow<List<Recipe>> = dao.observeAll()

    override fun search(query: String): Flow<List<Recipe>> = dao.search(query)

    override fun observeDetail(recipeId: Int): Flow<RecipeWithDetails?> = dao.observeDetail(recipeId)

    override suspend fun saveNewRecipe(
        recipe: Recipe,
        ingredients: List<String>,
        steps: List<StepDraft>
    ) {
        database.withTransaction {
            val recipeToInsert = recipe.copy(updatedAt = System.currentTimeMillis())
            val newRecipeId = dao.insertRecipe(recipeToInsert).toInt()
            dao.insertIngredients(
                ingredients.map { Ingredient(recipeId = newRecipeId, ingredient = it) }
            )
            dao.insertSteps(
                steps.mapIndexed { index, draft ->
                    Step(
                        recipeId = newRecipeId,
                        stepNumber = index + 1,
                        stepTitle = draft.title,
                        stepDescription = draft.description
                    )
                }
            )
        }
    }

    override suspend fun updateRecipe(
        recipe: Recipe,
        ingredients: List<String>,
        steps: List<StepDraft>
    ) {
        database.withTransaction {
            dao.updateRecipe(recipe.copy(updatedAt = System.currentTimeMillis()))
            dao.deleteIngredientsOf(recipe.id)
            dao.deleteStepsOf(recipe.id)
            dao.insertIngredients(
                ingredients.map { Ingredient(recipeId = recipe.id, ingredient = it) }
            )
            dao.insertSteps(
                steps.mapIndexed { index, draft ->
                    Step(
                        recipeId = recipe.id,
                        stepNumber = index + 1,
                        stepTitle = draft.title,
                        stepDescription = draft.description
                    )
                }
            )
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        dao.deleteRecipe(recipe)
    }
}