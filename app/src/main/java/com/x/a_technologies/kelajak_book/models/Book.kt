package com.x.a_technologies.kelajak_book.models

import java.io.Serializable

data class Book(
    var bookId:String = "",
    var imageUrl:String = "",
    var count:Int = 0,
    var name:String = "",
    var author:String = "",
    var moreInformation:String = "",
    var addedTimeMillis:Long = 0L,
    var language:String = "",
    var alphabetType:String = "",
    var pagesCount:Int = 0,
    var coatingType:String = "",
    var manufacturingCompany:String = "",
    var category:String = "",
    var searchedCount:Int = 0,
    var rentPrice:String = "",
    var sellingPrice:String = ""
):Serializable
