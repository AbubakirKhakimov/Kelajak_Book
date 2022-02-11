package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.SearchResultsAdapter
import com.x.a_technologies.kelajak_book.adapters.SearchResultsCallBack
import com.x.a_technologies.kelajak_book.adapters.TopBookSearchAdapter
import com.x.a_technologies.kelajak_book.adapters.TopBookSearchCallBack
import com.x.a_technologies.kelajak_book.databinding.FragmentSearchBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.models.Book

class SearchFragment : Fragment(), TopBookSearchCallBack, SearchResultsCallBack {

    lateinit var binding: FragmentSearchBinding
    lateinit var searchResultAdapter: SearchResultsAdapter
    lateinit var topBookSearchAdapter: TopBookSearchAdapter
    var isNew = true

    companion object{
        var dataIsEmpty = true
        var allBooksList = ArrayList<Book>()
        var topBooksList:ArrayList<Book> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.searchEdt.addTextChangedListener{
            if (it.toString().isEmpty()){
                binding.toBookSearchRv.visibility = View.VISIBLE
                binding.title.visibility = View.VISIBLE
                binding.searchResultsRv.visibility = View.GONE
            }else {
                binding.toBookSearchRv.visibility = View.GONE
                binding.title.visibility = View.GONE
                binding.searchResultsRv.visibility = View.VISIBLE
            }

            searchResultAdapter.filter.filter(it.toString())
        }

    }

    private fun check(){
        if (isNew){
            isNew = false

            searchResultAdapter = SearchResultsAdapter(allBooksList, this)
            topBookSearchAdapter = TopBookSearchAdapter(topBooksList, this)
            initRecycler()

            if(dataIsEmpty) {
                dataIsEmpty = false
                loadTopBookSearch()
            }
        }else{
            initRecycler()
        }
    }

    private fun initRecycler(){
        binding.searchResultsRv.adapter = searchResultAdapter
        binding.toBookSearchRv.adapter = topBookSearchAdapter
    }

    private fun loadTopBookSearch(){
        isLoading(true)

        val topSearchQuery = DatabaseRef.booksRef.orderByChild("searchedCount").limitToLast(16)
        topSearchQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                topBooksList.clear()

                for (item in snapshot.children){
                    topBooksList.add(item.getValue(Book::class.java)!!)
                }
                topBooksList.reverse()

                topBookSearchAdapter.notifyDataSetChanged()
                isLoading(false)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun isLoading(bool:Boolean){
        if (bool){
            binding.shimmerSearch.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.toBookSearchRv.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.shimmerSearch.stopShimmer()
            binding.shimmerSearch.visibility = View.GONE
            binding.toBookSearchRv.visibility = View.VISIBLE
        }
    }

    override fun topBookSearchItemClickListener(position: Int) {
        findNavController().navigate(R.id.action_searchFragment_to_bookDetailsFragment, bundleOf(
            "currentBook" to topBooksList[position]
        ))
    }

    override fun searchResultsItemClickListener(item:Book) {
        findNavController().navigate(R.id.action_searchFragment_to_bookDetailsFragment, bundleOf(
            "currentBook" to item,
            "forSearchResults" to true
        ))
    }

}