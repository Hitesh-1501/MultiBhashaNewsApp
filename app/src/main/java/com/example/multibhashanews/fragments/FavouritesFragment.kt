package com.example.multibhashanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.MainActivity
import com.example.multibhashanews.activities.SwipeToDeleteCallback
import com.example.multibhashanews.adapters.NewsAdapter
import com.example.multibhashanews.databinding.FragmentFavouritesBinding
import com.example.multibhashanews.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {
    lateinit var binding: FragmentFavouritesBinding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouritesBinding.bind(view)
        newsViewModel = (activity as MainActivity).newsViewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {article->
            val action = FavouritesFragmentDirections.actionFavouritesFragmentToArticleFragment(article)
            findNavController().navigate(action)
        }

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // Prevent crashes if the swipe happens during a list update
                if (position == RecyclerView.NO_POSITION) return
                val article = newsAdapter.differ.currentList[position]
                viewLifecycleOwner.lifecycleScope.launch {
                    // DELETION PROCESS
                    showProgressBar()
                    newsViewModel.deleteFavouriteArticle(article)
                    hideProgressBar()
                }
                Snackbar.make(view,"Remove from favourites",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewLifecycleOwner.lifecycleScope.launch {
                            //Undo Process
                            showProgressBar()
                            newsViewModel.addToFavourites(article)
                            hideProgressBar()
                        }
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(swipeHandler).apply {
            attachToRecyclerView(binding.rvFavouriteArticles)
        }
        newsViewModel.getFavouriteItems().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })
    }

    private fun hideProgressBar(){
     binding.progressBarFavourite.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.progressBarFavourite.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvFavouriteArticles.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}