package com.x.a_technologies.kelajak_book.models

import android.os.Parcel
import android.os.Parcelable

data class Review(
   var reviewId:String = "",
   var senderUser: User = User(),
   var reviewText:String = "",
   var reviewSendTimeMillis:Long = 0L,
   var adminMessage:String? = null,
   var adminMessageSendTimeMillis:Long? = null
):Parcelable {

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(reviewId)
      parcel.writeParcelable(senderUser, flags)
      parcel.writeString(reviewText)
      parcel.writeLong(reviewSendTimeMillis)
      parcel.writeString(adminMessage)
      parcel.writeValue(adminMessageSendTimeMillis)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<Review> {
      override fun createFromParcel(parcel: Parcel): Review {
         return Review(
            parcel.readString()!!,
            parcel.readParcelable(User::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long
         )
      }

      override fun newArray(size: Int): Array<Review?> {
         return arrayOfNulls(size)
      }
   }
}
