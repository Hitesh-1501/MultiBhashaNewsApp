package com.example.multibhashanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.MainActivity
import com.example.multibhashanews.adapters.CategoriesNewsAdapter
import com.example.multibhashanews.databinding.FragmentCategoryTypeBinding
import com.example.multibhashanews.utils.Constants
import com.example.multibhashanews.utils.Resource
import com.example.multibhashanews.viewModel.NewsViewModel

class CategoryTypeFragment : Fragment(R.layout.fragment_category_type) {
    lateinit var binding:FragmentCategoryTypeBinding
    lateinit var newsViewModel: NewsViewModel
    lateinit var categoryNewsAdapter: CategoriesNewsAdapter
    private val args: CategoryTypeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding =  FragmentCategoryTypeBinding.bind(view)
        newsViewModel = (activity as  MainActivity).newsViewModel

        val category = args.categoryTitle

        setUpRecycler()
        setUpSwipeToRefresh()
        setupViewModelObserver()

        // Check if the list is empty to prevent reloading on configuration changes.
        if (categoryNewsAdapter.differ.currentList.isEmpty()) {
            newsViewModel.getCategory(category)
        }

        categoryNewsAdapter.setOnItemClickListener { article->
            val action = CategoryTypeFragmentDirections.actionCategoryTypeFragmentToArticleFragment(article)
            findNavController().navigate(action)
        }

        binding.itemCategoryError.btnRetry.setOnClickListener {
            newsViewModel.getCategory(category)
        }
    }
    /**
     * Sets up the listener for the SwipeRefreshLayout.
     * This is for "pull-to-refresh" functionality.
     */
    private fun setUpSwipeToRefresh(){
        binding.swipeRefresh.setOnRefreshListener {
            // Call the new refresh function
            newsViewModel.refreshCategory(args.categoryTitle)
            // The refreshing animation will be stopped inside the ViewModel observer
            // once new data arrives.
        }
    }

    /**
     * Observes the LiveData from the ViewModel and updates the UI accordingly.
     */
    private fun setupViewModelObserver(){
        newsViewModel.categoryHeadLines.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success<*> -> {
                    hidingProgressBar()
                    hideErrorMsg()
                    // Stop the swipe-to-refresh animation
                    binding.swipeRefresh.isRefreshing = false
                    response.data?.let { newsresponse->
                        categoryNewsAdapter.differ.submitList(newsresponse.articles)
                        val totalResults = newsresponse.totalResults.toDouble()
                        val pageSize = Constants.QUERY_PAGE_SIZE.toDouble()
                        val totalPages = kotlin.math.ceil(totalResults / pageSize).toInt()
                        isLastPage = newsViewModel.categoryPage >= totalPages
                        if (isLastPage) {
                            binding.rvCategoryHeadLines.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error<*> ->{
                    hidingProgressBar()
                    // Stop the swipe-to-refresh animation even if there's an error
                    binding.swipeRefresh.isRefreshing = false
                    response.message?.let { message ->
                        (activity as BaseClass).customToast(activity as BaseClass,"Error Occur: $message")
                        showErrMsg(message)
                    }
                }
                is Resource.Loading<*> -> {
                    // Only show the bottom progress bar if we are not swipe-refreshing
                    if (!binding.swipeRefresh.isRefreshing) {
                        showProgressBar()
                    }
                }
            }
        })
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hidingProgressBar(){
        binding.categoryProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        binding.categoryProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
    private fun hideErrorMsg(){
        binding.itemCategoryError.root.visibility = View.INVISIBLE
        isError = false
    }
    private fun showErrMsg(message:String){
        binding.itemCategoryError.root.visibility = View.VISIBLE
        binding.itemCategoryError.tvNoInternet.text = message
        isError = true
    }
    /*
        It provides "infinite scrolling."
         It automatically loads older articles as the user scrolls towards the bottom of the current list.
         It's for when the user thinks, "I want to keep reading more from this list."
    */
    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoError = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNoError && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                newsViewModel.getCategory(args.categoryTitle)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
    private fun setUpRecycler(){
        categoryNewsAdapter = CategoriesNewsAdapter()
        binding.rvCategoryHeadLines.apply {
            adapter = categoryNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            // Add the pagination listener to the RecyclerView
            addOnScrollListener(this@CategoryTypeFragment.scrollListener)
        }
    }
}