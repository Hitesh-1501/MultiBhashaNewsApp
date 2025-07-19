package com.example.multibhashanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.adapters.CategoryAdapter
import com.example.multibhashanews.databinding.FragmentCategoryBinding
import com.example.multibhashanews.model.CategoryItem
import java.util.Locale

class CategoryFragment : Fragment(R.layout.fragment_category) {
    lateinit var binding: FragmentCategoryBinding
    lateinit var categoryAdapter: CategoryAdapter
    private var mList =  ArrayList<CategoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Populate the list only once when the fragment is first created.
        listOfItems()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoryBinding.bind(view)
        binding.categoryRv.setHasFixedSize(true)
        binding.categoryRv.layoutManager = LinearLayoutManager(requireContext())
        categoryAdapter = CategoryAdapter(mList)
        binding.categoryRv.adapter = categoryAdapter

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        categoryAdapter.setOnItemClickListener { categoryItem->
            val categoryTitle = categoryItem.title?.toLowerCase(Locale.ROOT)?:""
            val action = CategoryFragmentDirections.actionCategoryFragmentToCategoryTypeFragment(categoryTitle)
            findNavController().navigate(action)
        }
    }

    private fun listOfItems(){
        mList.clear()
        mList.add(
            CategoryItem(
                R.drawable.business,
                "Business"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.entertaintment,
                "Entertainment"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.general,
                "General"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.health,
                "Health"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.science,
                "Science"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.sports,
                "Sports"
            )
        )
        mList.add(
            CategoryItem(
                R.drawable.technnology,
                "Technology"
            )
        )
    }
    private fun filterList(query:String?){
        if(!query.isNullOrEmpty()) {
            val filterList = ArrayList<CategoryItem>()
            for (i in mList) {
                if(i.title!!.toLowerCase(Locale.ROOT).contains(query)){
                    filterList.add(i)
                }
            }
            if(filterList.isEmpty()){
                (activity as BaseClass).customToast(requireContext(),"No Data Found")
            }else{
                categoryAdapter.setFilerList(filterList)
            }
        }
    }
}