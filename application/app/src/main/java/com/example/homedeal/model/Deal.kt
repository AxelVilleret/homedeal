package com.example.homedeal.model

import android.os.Parcel
import android.os.Parcelable

data class Deal(var name:String, var expiration:String, var price: Double, var link: String, var description:String, var image:String, var creator:String, var reference: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(expiration)
        parcel.writeDouble(price)
        parcel.writeString(link)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(creator)
        parcel.writeString(reference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Deal> {
        override fun createFromParcel(parcel: Parcel): Deal {
            return Deal(parcel)
        }

        override fun newArray(size: Int): Array<Deal?> {
            return arrayOfNulls(size)
        }
    }
}