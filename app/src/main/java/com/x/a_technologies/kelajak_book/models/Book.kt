package com.x.a_technologies.kelajak_book.models

import android.os.Parcel
import android.os.Parcelable
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
):Serializable, Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookId)
        parcel.writeString(imageUrl)
        parcel.writeInt(count)
        parcel.writeString(name)
        parcel.writeString(author)
        parcel.writeString(moreInformation)
        parcel.writeLong(addedTimeMillis)
        parcel.writeString(language)
        parcel.writeString(alphabetType)
        parcel.writeInt(pagesCount)
        parcel.writeString(coatingType)
        parcel.writeString(manufacturingCompany)
        parcel.writeString(category)
        parcel.writeInt(searchedCount)
        parcel.writeString(rentPrice)
        parcel.writeString(sellingPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!
            )
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
