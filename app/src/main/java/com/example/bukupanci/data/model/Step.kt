package com.example.bukupanci.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class Step(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val stepNumber: Int,
    val stepDescription: String
)