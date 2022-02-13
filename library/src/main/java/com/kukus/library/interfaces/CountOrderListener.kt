package com.kukus.library.interfaces

import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.model.Order


interface CountOrderListener{

    fun countAllOrder(count : Int = 0, type : TYPE_ORDER)
    fun countAllPrice(price : Float = 0f, type : TYPE_ORDER)
    fun listAllOrder(list : ArrayList<Order>, type : TYPE_ORDER)

}