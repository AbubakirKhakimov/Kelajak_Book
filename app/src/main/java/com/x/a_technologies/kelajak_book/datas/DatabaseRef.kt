package com.x.a_technologies.kelajak_book.datas

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.x.a_technologies.kelajak_book.models.Keys

object DatabaseRef {
    val usersRef = Firebase.database.getReference(Keys.USERS_KEY)
    val booksRef = Firebase.database.getReference(Keys.BOOKS_KEY)
    val reviewsRef = Firebase.database.getReference(Keys.REVIEWS_KEY)
    val usersReviewsIdsRef = Firebase.database.getReference(Keys.USERS_REVIEWS_IDS)
    val categoriesRef = Firebase.database.getReference(Keys.CATEGORIES_KEY)
}