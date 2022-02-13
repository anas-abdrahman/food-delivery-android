package com.kukus.library.model

import java.util.*

class Chat(var userId: String = "", var messageText: String = "", var messageUser: String = "", var messageTime: Long = Date().time, var role :String = "")