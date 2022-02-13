package com.kukus.library.model

import com.kukus.library.Constant.Companion.TYPE_ORDER

class Menu(

        var id: String = "",
        var available: Boolean = true,
        var show: Boolean = true,
        var image: String = "",
        var name: String = "",
        var description: String = "",
        var label_price1: String = "",
        var label_price2: String = "",
        var price1: Float = 0f,
        var price2: Float = 0f,
        var type: TYPE_ORDER = TYPE_ORDER.EMPTY,
        var limit: Int = 0,
        var currentLimit: Int = 0,
        var count1: Int = 0,
        var count2: Int = 0

)