package com.example.bukupanci.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bukupanci.BukuPanciApp
import com.example.bukupanci.data.model.Recipe
import com.example.bukupanci.data.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

enum class RecipeSortOrder { TERBARU, TERLAMA, JUDUL_A_Z, JUDUL_Z_A }

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: RecipeSortOrder = RecipeSortOrder.TERBARU
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(RecipeSortOrder.TERBARU)

    private val baseRecipes = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.recipes else repository.search(query)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        baseRecipes,
        _searchQuery,
        _sortOrder
    ) { recipes, query, sortOrder ->
        HomeUiState(
            recipes = recipes.sortedByOrder(sortOrder),
            searchQuery = query,
            sortOrder = sortOrder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortOrderChange(order: RecipeSortOrder) {
        _sortOrder.value = order
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BukuPanciApp
                HomeViewModel(app.recipeRepository)
            }
        }
    }
}

private fun List<Recipe>.sortedByOrder(order: RecipeSortOrder): List<Recipe> = when (order) {
    RecipeSortOrder.TERBARU -> sortedByDescending { it.updatedAt }
    RecipeSortOrder.TERLAMA -> sortedBy { it.updatedAt }
    RecipeSortOrder.JUDUL_A_Z -> sortedBy { it.title.lowercase() }
    RecipeSortOrder.JUDUL_Z_A -> sortedByDescending { it.title.lowercase() }
}