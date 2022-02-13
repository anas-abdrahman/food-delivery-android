package com.kukus.library.model

import com.kukus.library.Constant.Companion.STATUS
import java.util.*
import kotlin.collections.ArrayList


class Ship(
        var id: String = "",
        var user_id: String = "",
        var ship_id: String = "",
        var menu: ArrayList<Order> = arrayListOf(),
        var address: Address = Address(),
        var date: String = "",
        var timestamp: Long = Date().time,
        var status: STATUS = STATUS.WAITING,
        var deliveryId: String = "",
        var delivery: Float = 0f,
        var note: String = "",
        var promo: String = "",
        var promoAmount: Int = 0,
        var wallet: Float = 0f,
        var price: Float = 0f
)