package com.sgg.healthykaly.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sgg.healthykaly.R
import com.sgg.healthykaly.ui.adapter.FindListAdapter
import com.sgg.healthykaly.ui.adapter.RecipeLoadStateAdapter
import com.sgg.healthykaly.databinding.FragmentFindBinding
import com.sgg.healthykaly.model.RecipeEntity
import com.sgg.healthykaly.ui.animation.FadeInFadeOutAnimation
import com.sgg.healthykaly.ui.viewmodel.RecipeViewModel
import com.sgg.healthykaly.ui.widget.CustomErrorWidget.RefreshListener
import com.sgg.healthykaly.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FindFragment : Fragment() {

    private lateinit var binding: FragmentFindBinding

    @Inject
    lateinit var viewModel: RecipeViewModel

    lateinit var fabAnimation: FadeInFadeOutAnimation

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val recipeListAdapter = FindListAdapter()

        binding = FragmentFindBinding.inflate(inflater)
        binding.bindView(viewModel.recipes, recipeListAdapter)
        fabAnimation = FadeInFadeOutAnimation(binding.fabScrollTop)
        return binding.root
    }

    /**
     * FragmentFindBinding extension function used to bind and set-up all view in the fragment
     */
    private fun FragmentFindBinding.bindView(recipeModelFlow: Flow<PagingData<RecipeEntity>>,
                                             recipeListAdapter: FindListAdapter) {
        lifecycleOwner = this@FindFragment

        receipList.apply {
            adapter = recipeListAdapter.withLoadStateHeaderAndFooter(header = RecipeLoadStateAdapter { recipeListAdapter.retry() },
                                                                     footer = RecipeLoadStateAdapter { recipeListAdapter.retry() })
            layoutManager = LinearLayoutManager(context,
                                                LinearLayoutManager.VERTICAL,
                                                false)
            addOnScrollListener(recipesScrollListener())
        }

        // Observe the adapter's load state flow from PagingDataAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            recipeListAdapter.loadStateFlow.collect { loadState ->
                progressBar.isVisible = loadState.isLoading()
                errorWidget.isVisible = loadState.isError() && recipeListAdapter.isEmpty()
                if (loadState.isError() && recipeListAdapter.isNotEmpty()) {
                    showSnackBar(getString(R.string.error_loading_data)) {
                        recipeListAdapter.retry()
                    }
                }
            }
        }

        fabListener = View.OnClickListener {
            receipList.smoothScrollToPosition(0)
        }

        // Set the refresh listener for the retry button on custom widget
        refreshListener = RefreshListener { recipeListAdapter.retry() }

        // Observe the recipe flow either remote or database source
        viewLifecycleOwner.lifecycleScope.launch {
            recipeModelFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        recipeListAdapter.submitData(it)
                    }
        }
    }

    private fun showSnackBar(message: String,
                             action: () -> Unit) {
        Snackbar.make(binding.root,
                      message,
                      Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.txt_retry_button)) {
                    action
                }
                .show()
    }

    /**
     * Recycler viw scroll listener that update the fab visibility depending on the scroll position
     */
    private fun recipesScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView,
                                dx: Int,
                                dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            if (layoutManager.isAtTop()) {
                fabAnimation.fadeOut()
            } else {
                if (binding.fabScrollTop.isGone) fabAnimation.fadeIn()
            }
        }
    }
}

