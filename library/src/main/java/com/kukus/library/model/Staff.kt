package com.kukus.library.model

import com.kukus.library.Constant.Companion.USER


class Staff(
        var id          :String = "",
        var name        :String = "",
        var mobile      :String = "",
        var email       :String = "",
        var pass        :String = "",
        var role        :USER = USER.ADMIN,
        var lat         :String = "",
        var lng         :String = "",
        var walletId    :String = "",
        var wallet      :Float = 0f,
        var point       :Int = 0,
        var block       :Boolean = false,
        var online      :Boolean = false,
        var login_at    :Long    = 0
        )