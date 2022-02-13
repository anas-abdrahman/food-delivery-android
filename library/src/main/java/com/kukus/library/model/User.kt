package com.kukus.library.model

class User(
        var id: String = "",
        var name: String = "",
        var mobile: String = "",
        var email: String = "",
        var password: String = "",
        var level: Int = 0,
        var walletId: String = "",
        var wallet: Float = 0.0f,
        var refId: String = "",
        var referral: String = "",
        var promoId: String = "",
        var point: Int = 0,
        var created_at : Long = 0,
        var updated_at : Long = 0,
        var login_at : Long = 0,
        var block: Boolean = false,
        var online:Boolean = false
)