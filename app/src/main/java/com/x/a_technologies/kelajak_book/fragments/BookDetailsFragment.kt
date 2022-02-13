package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.ReviewsAdapter
import com.x.a_technologies.kelajak_book.databinding.FragmentBookDetailsBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.Book
import com.x.a_technologies.kelajak_book.models.Keys
import com.x.a_technologies.kelajak_book.models.Review
import com.x.a_technologies.kelajak_book.models.UserReviewIds
import java.util.*
import kotlin.collections.ArrayList

fun Fragment.findTopNavController():NavController{
    val topLevelHost = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    return topLevelHost.navController
}

class BookDetailsFragment : Fragment() {

    lateinit var binding: FragmentBookDetailsBinding
    lateinit var reviewsAdapter: ReviewsAdapter
    lateinit var currentBook:Book
    var reviewsList = ArrayList<Review>()
    var bookmarkList = ArrayList<String>()

    var isNew = true
    var isSaved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.reviewEdt.addTextChangedListener {
            if (it!!.isEmpty()){
                binding.sendReview.visibility = View.GONE
            }else{
                binding.sendReview.visibility = View.VISIBLE
            }
        }
        
        binding.sendReview.setOnClickListener { 
            if (UserInfo.currentUser == null){
                Toast.makeText(requireActivity(), getString(R.string.you_must_register_to_leave_a_review), Toast.LENGTH_SHORT).show()
                AuthorizationNumberFragment.fromInfoFragment = false
                findTopNavController().navigate(R.id.authorizationNumberFragment)
            }else{
                sendReview()
            }
        }

        binding.reviewsCount.setOnClickListener {
            findNavController().navigate(R.id.action_bookDetailsFragment_to_allReviewsFragment, bundleOf(
                "bookName" to currentBook.name,
                "reviewsList" to reviewsList
            ))
        }

        binding.saveToBookmark.setOnClickListener {
            isSaved = !isSaved
            if (isSaved) {
                binding.saveToBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                bookmarkList.add(0, currentBook.bookId)
            }
            else {
                binding.saveToBookmark.setImageResource(R.drawable.ic_bookmark)
                bookmarkList.remove(currentBook.bookId)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        write()
    }

    private fun check(){
        if (isNew){
            isNew = false
            currentBook = arguments?.getSerializable("currentBook") as Book
            if (arguments?.getBoolean("forSearchResults", false)!!){
                addSearchedCount()
            }

            reviewsAdapter = ReviewsAdapter(reviewsList)
            init()
            read()
            loadCurrentBookReviews()
        }else{
            init()
            if (isSaved){
                binding.saveToBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
            }
        }
    }

    private fun init(){
        binding.apply {
            reviewsRv.adapter = reviewsAdapter
            Glide.with(requireActivity()).load(currentBook.imageUrl).into(bookImage)
            bookName.text = currentBook.name
            bookAuthorName.text = currentBook.author
            bookCount.text = currentBook.count.toString()
            bookPagesCount.text = currentBook.pagesCount.toString()
            bookLanguage.text = currentBook.language
            bookAlphabet.text = currentBook.alphabetType
            bookCoatingType.text = currentBook.coatingType
            bookManufacturingCompany.text = currentBook.manufacturingCompany
            bookRentPrice.text = "${currentBook.rentPrice} ${getString(R.string.sum)}"
            bookSellingPrice.text = "${currentBook.sellingPrice} ${getString(R.string.sum)}"
            bookMoreInfo.text = currentBook.moreInformation
            reviewsCountManager()

            if(UserInfo.currentUser != null){
                if (UserInfo.currentUser!!.imageUrl != null){
                    Glide.with(requireActivity()).load(UserInfo.currentUser!!.imageUrl).into(currentUserImage)
                }
            }
        }
    }
    
    private fun sendReview(){
        isLoading(true)
        val reviewRefKey = DatabaseRef.reviewsRef.child(currentBook.bookId).push().key!!

        val review = Review(
            reviewRefKey,
            UserInfo.currentUser!!,
            binding.reviewEdt.text.toString(),
            Date().time,
            null,
            null)

        val userReviewIds = UserReviewIds(
            currentBook.bookId,
            reviewRefKey)

        val queryData = hashMapOf<String, Any>(
            "${Keys.REVIEWS_KEY}/${currentBook.bookId}/${reviewRefKey}" to review,
            "${Keys.USERS_REVIEWS_IDS}/${UserInfo.currentUser!!.phoneNumber}/${reviewRefKey}" to userReviewIds
        )

        DatabaseRef.rootRef.updateChildren(queryData).addOnCompleteListener {
            if (it.isSuccessful){
                binding.reviewEdt.text.clear()
                reviewsList.add(0, review)

                reviewsCountManager()
                reviewsAdapter.notifyDataSetChanged()
                binding.noReviewsTitle.visibility = View.GONE
            }else{
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
            isLoading(false)
        }
    }

    private fun loadCurrentBookReviews(){
        binding.reviewsProgressBar.visibility = View.VISIBLE

        val reviewsQuery = DatabaseRef.reviewsRef.child(currentBook.bookId)
            .orderByChild("reviewSendTimeMillis")

        reviewsQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null){
                    binding.noReviewsTitle.visibility = View.VISIBLE
                }else{
                    for (item in snapshot.children){
                        reviewsList.add(item.getValue(Review::class.java)!!)
                    }

                    if (reviewsList.isEmpty()){
                        binding.noReviewsTitle.visibility = View.VISIBLE
                    }else{
                        reviewsList.reverse()
                    }
                }

                reviewsCountManager()
                reviewsAdapter.notifyDataSetChanged()
                binding.reviewsProgressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                binding.reviewsProgressBar.visibility = View.GONE
                binding.noReviewsTitle.visibility = View.VISIBLE
            }
        })
    }

    fun reviewsCountManager(){
        if (reviewsList.size <= 3){
            binding.reviewsCount.visibility = View.GONE
        }else{
            binding.reviewsCount.visibility = View.VISIBLE
            binding.reviewsCount.text = "${getString(R.string.all)} ${reviewsList.size} ${getString(R.string.all_reviews)}"
        }
    }
    
    private fun isLoading(bool:Boolean){
        if (bool){
            binding.sendReview.visibility = View.GONE
            binding.sendingProgressBar.visibility = View.VISIBLE
        }else{
            binding.sendReview.visibility = View.VISIBLE
            binding.sendingProgressBar.visibility = View.GONE
        }
    }

    private fun addSearchedCount(){
        DatabaseRef.booksRef.child(currentBook.bookId).child("searchedCount")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = snapshot.value.toString().toInt()
                    count++
                    DatabaseRef.booksRef.child(currentBook.bookId).child("searchedCount").setValue(count)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun read(){
        bookmarkList = Hawk.get("bookmarkList", ArrayList())
        checkSavedBookmark()
    }

    private fun write(){
        Hawk.put("bookmarkList", bookmarkList)
    }

    private fun checkSavedBookmark(){
        Thread{
            for (id in bookmarkList){
                if (currentBook.bookId == id){
                    requireActivity().runOnUiThread {
                        binding.saveToBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        isSaved = true
                    }
                    break
                }
            }
        }.start()
    }

}