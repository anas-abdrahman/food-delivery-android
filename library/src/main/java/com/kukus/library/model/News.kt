package com.kukus.library.model

import java.util.*


class News {


    var id: String = ""
    var image: String = ""
    var date : Long = Date().time
    var reads: ArrayList<String> = arrayListOf()

    constructor(){}

    constructor(
            id: String,
            image: String,
            date: Long,
            reads: ArrayList<String>

    ) {
        this.id = id
        this.image = image
        this.date = date
        this.reads = reads
    }
}