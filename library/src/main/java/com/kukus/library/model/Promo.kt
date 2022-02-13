package com.kukus.library.model

class Promo {

    var id: String = ""
    var code: String = ""
    var user: ArrayList<String> = arrayListOf()
    var createAt: Long = 0
    var amount: Int = 0
    var expireAt: Long = 0
    var active: Boolean = false
    var limit: Int = 0
    var currentLimit: Int = 0

    constructor() {}

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

    fun reset() {

        this.id = ""
        this.code = ""
        this.user = arrayListOf()
        this.createAt = 0
        this.amount = 0
        this.expireAt = 0
        this.active = false
        this.limit = 0
        this.currentLimit = 0
    }


}