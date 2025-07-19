package com.example.multibhashanews.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.multibhashanews.databinding.ItemCategoryBinding
import com.example.multibhashanews.model.CategoryItem

class CategoryAdapter(private var list: ArrayList<CategoryItem>): RecyclerView.Adapter<CategoryAdapter.viewHolder>() {
    inner class viewHolder(binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageView = binding.imageView
        var textView = binding.tv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

     private var onItemClick: ((CategoryItem) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setFilerList(list:ArrayList<CategoryItem>){
        this.list = list
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = list[position]
        item.image?.let { holder.imageView.setImageResource(it) }
        holder.textView.text = item.title

        holder.itemView.setOnClickListener {
            onItemClick?.let {
                it(item)
            }
        }
    }

    fun setOnItemClickListener(listener: ((CategoryItem) -> Unit)){
        onItemClick = listener
    }
}