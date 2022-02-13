package com.x.a_technologies.kelajak_book.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    var phoneNumber:String = "",
    var firstName:String = "",
    var lastName:String = "",
    var imageUrl:String? = null
): Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(phoneNumber)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()
            )
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
