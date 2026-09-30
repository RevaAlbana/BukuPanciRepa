package com.example.bukupanci

import android.app.Application
import androidx.room.Room
import com.example.bukupanci.data.local.AppDatabase
import com.example.bukupanci.data.repository.RecipeRepository
import com.example.bukupanci.data.repository.RoomRecipeRepository

class BukuPanciApp : Application() {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bukupanci.db"
        ).build()
    }

    val recipeRepository: RecipeRepository by lazy {
        RoomRecipeRepository(database.recipeDao(), database)
    }
}