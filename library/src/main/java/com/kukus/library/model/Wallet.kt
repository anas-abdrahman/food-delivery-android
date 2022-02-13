package com.kukus.library.model

class Wallet {


    var user_id: String = ""
    var amount: Float = 0.0f
    var inOut: String = ""
    var isCash: Boolean = true
    var note: String = ""
    var date: Long = 0

    constructor(){}

    constructor(
            user_id: String,
            amount: Float,
            inOut: String,
            isCash: Boolean,
            note: String,
            date: Long
    ) {
        this.user_id = user_id
        this.amount = amount
        this.inOut = inOut
        this.isCash = isCash
        this.note = note
        this.date = date
    }
}