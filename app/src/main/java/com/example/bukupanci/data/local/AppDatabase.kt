package com.example.bukupanci.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bukupanci.data.model.Ingredient
import com.example.bukupanci.data.model.Recipe
import com.example.bukupanci.data.model.Step

@Database(
    entities = [Recipe::class, Ingredient::class, Step::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}