package com.kukus.library.model

class Point {


    var user_id: String = ""
    var point: Int = 0
    var note: String = ""
    var date: Long = 0
    var inOut: String = ""

    constructor(){}

    constructor(
            user_id: String,
            point: Int,
            note: String,
            date: Long,
            inOut: String
    ) {
        this.user_id = user_id
        this.point = point
        this.note = note
        this.date = date
        this.inOut = inOut
    }
}