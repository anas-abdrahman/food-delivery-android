package com.kukus.library.model

import java.util.*

class Message (
        var id: String = "",
        var user_id: String = "",
        var user_to: String = "",
        var email: String = "",
        var title: String = "",
        var text: String = "",
        var read: Boolean = false,
        var time: Long = Date().time
)
