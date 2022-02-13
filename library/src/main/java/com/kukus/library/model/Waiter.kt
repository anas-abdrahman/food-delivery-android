package com.kukus.library.model

import com.kukus.library.Constant.Companion.STATUS
import java.util.*
import kotlin.collections.ArrayList


class Waiter(
        var id: String = "",
        var ship_id: String = "",
        var menu: ArrayList<Order> = arrayListOf(),
        var date: String = "",
        var timestamp: Long = Date().time,
        var status: STATUS = STATUS.WAITING,
        var note: String = "",
        var price: Float = 0f,
        var table: Int = 0,
        var discount: Int = 0
)