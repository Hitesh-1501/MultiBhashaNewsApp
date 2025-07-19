package com.example.multibhashanews.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.MainActivity
import com.example.multibhashanews.databinding.FragmentArticleBinding
import com.example.multibhashanews.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException


class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var binding: FragmentArticleBinding
    private lateinit var newsViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState:  Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = (activity as MainActivity).newsViewModel

        val article = args.article

        Log.d("ArticleDebug", "Article: $article")
        Log.d("ArticleDebug", "URL: ${article.url}")
        Log.d("ArticleDebug", "Source: ${article.source}")
        Log.d("ArticleDebug", "Source Name: ${article.source?.name}")
        Log.d("ArticleDebug", "Image: ${article.urlToImage}")
        Log.d("ArticleDebug", "Title: ${article.title}")

        setupWebView()
        article.url?.let {
            binding.webView.loadUrl(it)
        }



        binding.fab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                newsViewModel.addToFavourites(article)

                // Use the standard SnackBar for testing
                Snackbar.make(requireView(), "Added To Favourites", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(){
        val settings = binding.webView.settings

        // This stores parts of the website on the device.
        // The next time the user opens an article from the same site, it will load much faster.
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        // Enable JavaScript, as many modern news sites require it
        binding.webView.settings.javaScriptEnabled = true

        //Load text first, then load images after the page layout is ready.
        // The user sees the content much faster.
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false


        // Set a WebViewClient to manage page loading events
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }
}