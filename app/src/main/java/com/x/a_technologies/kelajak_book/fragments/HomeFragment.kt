package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.*
import com.x.a_technologies.kelajak_book.databinding.FragmentHomeBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.models.Book
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), CategoriesCallBack, NewBooksCallBack {

    lateinit var binding: FragmentHomeBinding
    lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var booksByCategoriesAdapter: BooksByCategoriesAdapter
    lateinit var newBooksAdapter: NewBooksAdapter

    var isNew = true

    companion object {
        var dataIsEmpty = true
        var booksList: ArrayList<Book> = ArrayList()
        var newBooksList: ArrayList<Book> = ArrayList()
        var categoriesList: ArrayList<String> = ArrayList()

        var categoryPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryPosition = categoriesAdapter.selectedItemPosition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.swipeRefresh.setOnRefreshListener {
            loadNewBooks()
            loadCategories()
            loadAllBooks()
        }

    }

    private fun check() {
        if (isNew) {
            isNew = false

            categoriesAdapter = CategoriesAdapter(requireActivity(), categoriesList, this)
            categoriesAdapter.selectedItemPosition = categoryPosition
            booksByCategoriesAdapter = BooksByCategoriesAdapter(booksList)
            newBooksAdapter = NewBooksAdapter(newBooksList, this)
            initRecycler()

            if (dataIsEmpty) {
                dataIsEmpty = false
                binding.swipeRefresh.isRefreshing = true
                loading()

                loadNewBooks()
                loadCategories()
                loadAllBooks()
            }
        } else {
            initRecycler()
        }
    }

    private fun loadNewBooks() {
        val newBooksQuery = DatabaseRef.booksRef.orderByChild("addedTimeMillis").limitToLast(20)

        newBooksQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newBooksList.clear()

                for (item in snapshot.children) {
                    newBooksList.add(item.getValue(Book::class.java)!!)
                }
                newBooksList.reverse()

                newBooksAdapter.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
                binding.shimmerNewBooks.visibility = View.GONE
                binding.newBooksRv.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadCategories() {
        DatabaseRef.categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()

                for (item in snapshot.children) {
                    categoriesList.add(item.value.toString())
                }
                categoriesList.add(0, "Barchasi")

                categoriesAdapter.selectedItemPosition = 0
                categoriesAdapter.notifyDataSetChanged()
                binding.categoriesRv.scrollToPosition(0)
                binding.swipeRefresh.isRefreshing = false
                binding.shimmerCategories.visibility = View.GONE
                binding.categoriesRv.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadAllBooks() {
        DatabaseRef.booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksList.clear()

                for (item in snapshot.children) {
                    booksList.add(item.getValue(Book::class.java)!!)
                }

                Thread {
                    SearchFragment.allBooksList.apply {
                        clear()
                        addAll(booksList)
                    }
                }.start()

                booksByCategoriesAdapter.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
                binding.shimmerBooksByCategories.visibility = View.GONE
                binding.booksByCategoriesRv.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadBooksByCategories(category: String) {
        val categoryQuery = DatabaseRef.booksRef.orderByChild("category").equalTo(category)

        categoryQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksList.clear()

                for (item in snapshot.children) {
                    booksList.add(item.getValue(Book::class.java)!!)
                }

                booksByCategoriesAdapter.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initRecycler() {
        binding.categoriesRv.adapter = categoriesAdapter
        binding.categoriesRv.scrollToPosition(categoriesAdapter.selectedItemPosition)
        binding.booksByCategoriesRv.adapter = booksByCategoriesAdapter
        binding.newBooksRv.adapter = newBooksAdapter
    }

    override fun categoriesItemClickListener(position: Int) {
        binding.swipeRefresh.isRefreshing = true

        if (position == 0) {
            loadAllBooks()
        } else {
            loadBooksByCategories(categoriesList[position])
        }
    }

    override fun newBooksItemClickListener(position: Int) {
        findNavController().navigate(
            R.id.action_homeFragment_to_bookDetailsFragment, bundleOf(
                "currentBook" to newBooksList[position]
            )
        )
    }

    fun loading() {
        binding.newBooksRv.visibility = View.GONE
        binding.categoriesRv.visibility = View.GONE
        binding.booksByCategoriesRv.visibility = View.GONE

        binding.shimmerNewBooks.visibility = View.VISIBLE
        binding.shimmerCategories.visibility = View.VISIBLE
        binding.shimmerBooksByCategories.visibility = View.VISIBLE
    }

}