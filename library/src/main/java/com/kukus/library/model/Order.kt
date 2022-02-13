package com.kukus.library.model

import android.os.Parcel
import android.os.Parcelable

class Order() : Parcelable{

        var menu_id: String = ""
        var id: String = ""
        var title: String = ""
        var price1: Float = 0f
        var price2: Int = 0
        var quantity: Int = 0
        var type: String = ""
        var note: String = ""

    constructor(
           menu_id: String = "",
           id: String = "",
           title: String = "",
           price1: Float = 0f,
           price2: Int = 0,
           quantity: Int = 0,
           type: String = "",
           note: String = ""


    ) : this() {
        this.menu_id = menu_id
        this.id = id
        this.title = title
        this.price1 = price1
        this.price2 = price2
        this.quantity = quantity
        this.type = type
        this.note = note
    }


    constructor(parcel: Parcel) : this() {
        menu_id = parcel.readString()
        id = parcel.readString()
        title = parcel.readString()
        price1 = parcel.readFloat()
        price2 = parcel.readInt()
        quantity = parcel.readInt()
        type = parcel.readString()
        note = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(menu_id)
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeFloat(price1)
        parcel.writeInt(price2)
        parcel.writeInt(quantity)
        parcel.writeString(type)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
