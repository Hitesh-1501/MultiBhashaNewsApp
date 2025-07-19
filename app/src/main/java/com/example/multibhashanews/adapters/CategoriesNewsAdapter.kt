package com.example.multibhashanews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.multibhashanews.R
import com.example.multibhashanews.databinding.ItemNewsBinding
import com.example.multibhashanews.model.Article
import java.security.interfaces.RSAPublicKey

class CategoriesNewsAdapter: RecyclerView.Adapter<CategoriesNewsAdapter.viewHolder>() {
    inner class viewHolder(binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root){
        var articleImage = binding.articleImage
        var articleSource = binding.articleSource
        var articleTitle = binding.articleTitle
        var articleDescription = binding.articleDescription
        var articleDateTime = binding.articleDateTime
    }

    /*
       To efficiently update RecyclerView items when the dataset changes
       — without reloading the entire list — using DiffUtil and AsyncListDiffer.
    */
    private val differCallback = object: DiffUtil.ItemCallback<Article>(){
        /*
           Checks whether two items represent the same object.

           Typically compares a unique identifier (like url or id).

           Used for checking identity.
        */
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        /*

       Checks whether the contents of the items are the same.

       You usually compare all fields here.

       Kotlin's data class makes it easy with == (which checks all properties).

        */

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
    //AsyncListDiffer handles the calculation of list differences on a background thread.
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage ?: R.drawable.placeholder).into(holder.articleImage)
            holder.articleDescription.text  = article.description
            holder.articleTitle.text = article.title
            holder.articleSource.text = article?.source?.name
            holder.articleDateTime.text = article.publishedAt

            setOnClickListener {
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
    }
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener:(Article) -> Unit){
        onItemClickListener = listener
    }
}