package com.sgg.healthykaly.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.sgg.healthykaly.model.Recipe
import com.sgg.healthykaly.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeRepository: RecipeRepository,
                      private val savedStateHandle: SavedStateHandle? = null) : ViewModel() {

    private lateinit var _recipes: Flow<PagingData<Recipe>>
    val recipes: Flow<PagingData<Recipe>>
        get() = _recipes

    init {
        loadInitState()
    }

    private fun loadInitState() {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            // Load recipes from the repository and update the LiveData object
            _recipes = recipeRepository.getRecipes()
        }
    }
}