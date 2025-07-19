package com.example.multibhashanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.MainActivity
import com.example.multibhashanews.adapters.NewsAdapter
import com.example.multibhashanews.databinding.FragmentSearchBinding
import com.example.multibhashanews.utils.Constants
import com.example.multibhashanews.utils.Resource
import com.example.multibhashanews.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var binding:FragmentSearchBinding
    lateinit var newsAdapter: NewsAdapter
    lateinit var newsViewModel: NewsViewModel
    private var isSearchActive = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        newsViewModel = (activity as MainActivity).newsViewModel
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener {article->
            val action = SearchFragmentDirections.actionSearchFragmentToArticleFragment(article)
            findNavController().navigate(action)
        }

        // Declare a Job variable to manage coroutine lifecycle
        var job: Job? = null
        // Add a text change listener to the EditText (searchEdit)
        binding.etSearch.addTextChangedListener(){editable->
            // Cancel the previous coroutine job (if still running) to prevent multiple API calls
            job?.cancel()
            //This scope is automatically cancelled when the fragment's view is destroyed
            job = viewLifecycleOwner.lifecycleScope.launch {
                // Introduce a delay to wait for user to finish typing
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                // Check if the text is not null and not empty
                editable?.let {
                    val query = editable.toString()
                    if(query.isNotEmpty()){
                        isSearchActive = true
                        // Call the ViewModel function to perform search API request
                        //Reset page & response
                        newsViewModel.searchNewsPage = 1
                        newsViewModel.searchNewsResponse = null
                        newsViewModel.searchNews(editable.toString())
                    }else{
                        isSearchActive = false // <-- SET FLAG TO FALSE
                        // Optional: Clear the list when the search box is empty
                        newsAdapter.differ.submitList(emptyList())

                    }
                }
            }
        }
        setUpViewModelObserver()

        binding.noInternetLayout.btnRetry.setOnClickListener {
            if(binding.etSearch.text.toString().isNotEmpty()){
                newsViewModel.searchNews(binding.etSearch.text.toString())
            }else{
                hideProgressBar()
            }
        }
    }

    private fun setUpViewModelObserver(){
        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMsg()
                    response.data?.let {newsResponse->
                        val validArticles = newsResponse.articles.filter {
                            !it.url.isNullOrEmpty() && it.url.startsWith("http")
                        }
                        newsAdapter.differ.submitList(validArticles)
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            binding.rvSearchResults.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let {message->
                        (activity as BaseClass).customToast(requireContext(),"Sorry Error $message ")
                        showErrorMsg(message)
                    }
                }
                is Resource.Loading<*> ->{
                    showingProgressBar()
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
        binding.noInternetLayout.root.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMsg(message:String){
        binding.noInternetLayout.root.visibility = View.VISIBLE
        binding.noInternetLayout.tvNoInternet.text = message
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
                // Now, decide WHAT to paginate based on the search state
                if(isSearchActive){
                    // User is searching, so paginate the search results
                    newsViewModel.searchNews(binding.etSearch.text.toString())
                }else{
                    //User is just Browse, so paginate the normal news feed
                    newsViewModel.getHeadLines("us")
                }
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
        binding.rvSearchResults.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // Add the pagination listener to the RecyclerView
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
}