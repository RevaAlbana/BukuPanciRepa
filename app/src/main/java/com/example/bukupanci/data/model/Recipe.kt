package com.example.bukupanci.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imageSource: String? = null,
    val cookingTimeEstimation: Int? = null,
    val description: String? = null,
    val updatedAt: Long = 0L,
    val servings: Int? = null,
    val isFavorite: Boolean = false
)