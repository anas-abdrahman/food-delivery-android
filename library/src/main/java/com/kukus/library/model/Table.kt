package com.kukus.library.model

import com.kukus.library.Constant.Companion.TABLE
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import java.util.*
import kotlin.collections.ArrayList


class Table(

        val date : Long = getTimestamp,
        var table_1: TABLE = TABLE.EMPTY,
        var table_2: TABLE = TABLE.EMPTY,
        var table_3: TABLE = TABLE.EMPTY,
        var table_4: TABLE = TABLE.EMPTY,
        var table_5: TABLE = TABLE.EMPTY,
        var table_6: TABLE = TABLE.EMPTY,
        var table_7: TABLE = TABLE.EMPTY,
        var table_8: TABLE = TABLE.EMPTY,
        var table_9: TABLE = TABLE.EMPTY,
        var table_10: TABLE = TABLE.EMPTY,
        var order_1: String = "",
        var order_2: String = "",
        var order_3: String = "",
        var order_4: String = "",
        var order_5: String = "",
        var order_6: String = "",
        var order_7: String = "",
        var order_8: String = "",
        var order_9: String = "",
        var order_10: String = ""
)