package com.kukus.library.model

data class CountOrder(var countOrder: Int = 0, var countAllOrder: Int = 0, var countAllPrice: Float = 0f){

    fun resetCount(){
        countOrder      = 0
        countAllOrder   = 0
        countAllPrice   = 0f
    }

}