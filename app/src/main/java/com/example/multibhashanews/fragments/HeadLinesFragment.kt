package com.example.multibhashanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.MainActivity
import com.example.multibhashanews.adapters.NewsAdapter
import com.example.multibhashanews.databinding.FragmentHeadLinesBinding
import com.example.multibhashanews.utils.Constants
import com.example.multibhashanews.utils.Resource
import com.example.multibhashanews.viewModel.NewsViewModel
import kotlin.math.ceil

class HeadLinesFragment : Fragment(R.layout.fragment_head_lines){
    private lateinit var binding:FragmentHeadLinesBinding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadLinesBinding.bind(view)

        newsViewModel = (activity as MainActivity).newsViewModel
        setUpRecyclerView()
        setupViewModelObserver()
        setUpSwipeToRefresh()
        newsAdapter.setOnItemClickListener {article->
            val action = HeadLinesFragmentDirections.actionHeadLinesFragmentToArticleFragment(article)
            findNavController().navigate(action)
        }
        binding.itemHeadlinesError.btnRetry.setOnClickListener {
            newsViewModel.getHeadLines("us")
        }
    }
    /**
     * Sets up the listener for the SwipeRefreshLayout.
     * This is for "pull-to-refresh" functionality.
     */
    private fun setUpSwipeToRefresh(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            //Call the function that resets everything
            newsViewModel.refreshHeadlines("us")
            // The refreshing animation will be stopped inside the ViewModel observer
            // once new data arrives.
        }
    }

    /**
     * Observes the LiveData from the ViewModel and updates the UI accordingly.
     */
     private fun setupViewModelObserver(){
         newsViewModel.headlines.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success<*> -> {
                    hideProgressBar()
                    hideErrorMsg()
                    // Stop the swipe-to-refresh animation
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { newsresponse->
                        newsAdapter.differ.submitList(newsresponse.articles)
                        val totalResults = newsresponse.totalResults.toDouble()
                        val pageSize = Constants.QUERY_PAGE_SIZE.toDouble()
                        val totalPages = ceil(totalResults / pageSize).toInt()
                        isLastPage = newsViewModel.headLinesPage >= totalPages
                        if(isLastPage){
                            binding.rvHeadLines.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error<*> ->{
                   hideProgressBar()
                   // Stop the swipe-to-refresh animation even if there's an error
                   binding.swipeRefreshLayout.isRefreshing = false
                   response.message?.let { message ->
                       (activity as BaseClass).customToast(activity as BaseClass,"Error Occur: $message")
                       showErrorMsg(message)
                   }
               }
                is Resource.Loading<*> -> {
                    // Only show the bottom progress bar if we are not swipe-refreshing
                    if (!binding.swipeRefreshLayout.isRefreshing) {
                        showingProgressBar()
                    }
                }
            }
         })
     }

    var isError = false
    var isLoading = false
    var isLastPage =false
    var isScrolling =false

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showingProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }
    private fun hideErrorMsg(){
        binding.itemHeadlinesError.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMsg(message:String){
        binding.itemHeadlinesError.root.visibility = View.VISIBLE
        binding.itemHeadlinesError.tvNoInternet.text = message
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
                newsViewModel.getHeadLines("us")
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

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvHeadLines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // Add the pagination listener to the RecyclerView
            addOnScrollListener(this@HeadLinesFragment.scrollListener)
        }
    }
}