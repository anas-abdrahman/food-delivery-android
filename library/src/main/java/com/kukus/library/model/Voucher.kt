package com.kukus.library.model

class Voucher{
    var id: String = ""
    var code: String = ""
    var user: ArrayList<String> = arrayListOf()
    var createAt: Long = 0
    var amount: Int = 0
    var expireAt: Long = 0
    var active: Boolean = true
    var limit: Int = 0
    var currentLimit: Int = 0

    constructor(){}

    constructor(
            id: String,
            code: String,
            user: ArrayList<String>,
            amount: Int,
            createAt: Long,
            expireAt: Long,
            active: Boolean,
            limit: Int,
            currentLimit: Int
    ) {
        this.id = id
        this.code = code
        this.user = user
        this.amount = amount
        this.createAt = createAt
        this.expireAt = expireAt
        this.active = active
        this.limit = limit
        this.currentLimit = currentLimit
    }
}
