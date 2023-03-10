package com.sgg.healthykaly.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sgg.healthykaly.data.RecipePagingSource
import com.sgg.healthykaly.model.Recipe
import com.sgg.healthykaly.service.RecipeService
import com.sgg.healthykaly.utils.QueryConstants
import kotlinx.coroutines.flow.Flow
class RemoteDataProvider(private val recipeService: RecipeService) : RecipeDataSourceProvider {
    override fun getFlowOfRecipes(queries: Map<String, Int>): Flow<PagingData<Recipe>> {
        return Pager(config = PagingConfig(pageSize = QueryConstants.DEFAULT_LOAD_SIZE,
                                           enablePlaceholders = false),
                     pagingSourceFactory = { RecipePagingSource(recipeService, queries) }).flow
    }
}