package com.kukus.administrator.printer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.text.format.DateFormat
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.getIP
import com.kukus.library.Constant.Companion.getOrderList
import com.kukus.library.Constant.Companion.setIP
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.model.Ship
import com.kukus.library.model.User
import com.kukus.library.model.Waiter
import com.printer.sdk.PrinterConstants
import com.printer.sdk.PrinterInstance
import com.printer.sdk.Table

class Printer(val context: Context) {

    private lateinit var myPrinter: PrinterInstance
    private lateinit var sharedPreferences : SharedPreferences
    var isConnected = false

    init {
        openConnection()
    }

    private var status = "Unknown"

    fun setIP(ip : String)  = setIP(sharedPreferences, ip)
    fun getIP() = getIP(sharedPreferences)

    private fun openConnection() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        myPrinter = PrinterInstance.getPrinterInstance(getIP(), 9100, @SuppressLint("HandlerLeak") object : Handler() {

            override fun handleMessage(msg: Message) {
                when (msg.what) {

                    PrinterConstants.Connect.SUCCESS -> {
                        //context.toast("connected")
                        status = ("connected")
                    }
                    PrinterConstants.Connect.FAILED -> {
                        //context.toast("failed connect")
                        status = ("connect failed")
                    }
                    PrinterConstants.Connect.CLOSED -> {
                        // context.toast("close connect")
                        status = ("connect close")
                    }
                    PrinterConstants.Connect.NODEVICE -> {
                        // context.toast("no device connect")
                        status = ("no device connect")
                    }
                    else -> {
                        //context.toast("Unknown")
                        //status = ("Unknown")
                    }
                }

            }

        })

        ConnectThread().start()

    }

    fun refresh(){
        openConnection()
    }

    fun getStatus(): String {
        return status
    }


    private inner class ConnectThread : Thread() {
        override fun run() {
            isConnected = myPrinter.openConnection()
        }
    }

    fun printReceiptDelivery(ship: Ship, user: User) {

        if (!isConnected) return

        var hasMakan = false
        var hasMinum = false
        var hasExtra = false

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_reset_2)
        val date = DateFormat.format("dd/MM/yyyy - HH:mm:ss", getTimestamp)

        val tableForMakan = Table("", ";", intArrayOf(5, 33, 12))
        val tableForMinum = Table("", ";", intArrayOf(5, 33, 12))
        val tableForExtra = Table("", ";", intArrayOf(5, 33, 12))

        val tableForOrder = Table("", ";", intArrayOf(5, 33, 12))

        //val tableForKitchen = Table("", ";", intArrayOf(5, 33, 12))
        val tableForPrice = Table("", ";", intArrayOf(0, 38, 12))

        // Logo
        myPrinter.printImage(bitmap, PrinterConstants.PAlign.CENTER, 0, false)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText(date.toString())
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Receipt
        myPrinter.setFont(0, 1, 1, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Receipt No : ${ship.ship_id}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3)

        // Name
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("Name        : ${user.name}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Tel
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("Tel         : ${user.mobile}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)


        // building
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("No building : ${ship.address.building}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)


        // Tel
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("Floor       : ${ship.address.floor}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Apartments
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("Apartments  : ${ship.address.apartment}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)


        // Address
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("Address     : ${ship.address.street}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Line
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("------------------------------------------------")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Order
        ship.menu.forEach {

            if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name) {
                tableForMakan.addRow("${it.quantity}x;${it.title};")
                hasMakan = true
            }

            if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                tableForMinum.addRow("${it.quantity}x;${it.title};")
                hasMinum = true
            }

            if (it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                tableForExtra.addRow("${it.quantity}x;${it.title};")
                hasExtra = true
            }

            tableForOrder.addRow("${it.quantity}x;${it.title};LE ${it.price1 * it.quantity}")
            //tableForKitchen.addRow("${it.quantity}x;${it.title};")

        }

        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printTable(tableForOrder)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Line
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("------------------------------------------------")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Delivery
        if (ship.delivery > 0f) {

            tableForPrice.addRow(";- DELIVERY   ;LE ${ship.delivery}")

        }

        // Total
        if(ship.promo != "") {
            val promo = FirebaseUtils.getPromotion(ship.price, ship.promoAmount)
            tableForPrice.addRow(";- PROMO CODE (${ship.promo}) - ${ship.promoAmount}%;- LE $promo")
        }

        if(ship.wallet > 0) {
            tableForPrice.addRow(";- WALLET;- LE ${ship.wallet} -")
        }

        tableForPrice.addRow(";- TOTAL PRICE;LE ${ship.price}")

        myPrinter.setFont(0, 0, 0, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        //myPrinter.printText("TOTAL PRICE : LE ${ship.price1}")
        myPrinter.printTable(tableForPrice)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 4)

        // Text
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Terima Kasih Atas Pesanan anda,")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Selamat Menjamu Selera Ye!!")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

        // Cutter
        myPrinter.cutPaper(48, 7)

        //////////////////////////////////

        // kitchen
        myPrinter.setFont(0, 1, 1, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("*** KITCHEN ***")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3)

        // date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText(date.toString())
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // receipt
        myPrinter.setFont(0, 0, 10, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Receipt No : ${ship.ship_id}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        if (hasMakan) {

            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("-------------------[ MAKANAN ]------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // MAKANAN
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForMakan)

            if (!hasExtra) {

                myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

                // cutter
                myPrinter.cutPaper(48, 7)

            }

        }

        if (hasExtra) {

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("--------------------[ EXTRA ]-------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // EXTRA
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForExtra)

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

            // cutter
            myPrinter.cutPaper(48, 7)

        }

        if (hasMinum) {

            // date
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText(date.toString())
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

            // receipt
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("Receipt No : ${ship.ship_id}")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)


            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("-------------------[ MINUMAN ]------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // MINUM
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForMinum)
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

            // cutter
            myPrinter.cutPaper(48, 7)

        }

    }

    fun printReceiptCustomer(waiter: Waiter) {

        if (!isConnected) return

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_reset_2)
        val date = DateFormat.format("dd/MM/yyyy - HH:mm:ss", getTimestamp)
        val tableForOrder = Table("", ";", intArrayOf(5, 33, 12))
        val tableForPrice = Table("", ";", intArrayOf(0, 38, 12))

        // Logo
        myPrinter.printImage(bitmap, PrinterConstants.PAlign.CENTER, 0, false)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("117 Toha Dinari, hayyu sabeik, Nasr City.")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("contact : +201100355679")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText(date.toString())
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Receipt
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Receipt No : ${waiter.ship_id}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Table
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Table No : ${waiter.table}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Line
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("------------------------------------------------")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Order

        waiter.menu = getOrderList(waiter.menu)

        waiter.menu.forEach {

            tableForOrder.addRow("${it.quantity}x;${it.title};LE ${it.price1 * it.quantity}")

        }

        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printTable(tableForOrder)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Line
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printText("------------------------------------------------")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Total
        if(waiter.discount > 0) {
            tableForPrice.addRow(";- DISCOUNT;- LE ${waiter.discount}")
            tableForPrice.addRow(";- TOTAL PRICE;LE ${waiter.price - waiter.discount}")
        }else{
            tableForPrice.addRow(";- TOTAL PRICE;LE ${waiter.price}")
        }



        myPrinter.setFont(0, 0, 0, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
        myPrinter.printTable(tableForPrice)
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 4)

        // Text
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Terima kasih Atas Kunjungan Anda ")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Sila Datang Lagi!")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

        // Cutter
        myPrinter.cutPaper(48, 7)

    }


    fun printReceiptKitchen(waiter: Waiter) {

        if (!isConnected) return

        var hasMakan = false
        var hasMinum = false
        var hasExtra = false

        val date = DateFormat.format("dd/MM/yyyy - HH:mm:ss", getTimestamp)
        val tableForMakan = Table("", ";", intArrayOf(5, 35, 10))
        val tableForMinum = Table("", ";", intArrayOf(5, 35, 10))
        val tableForExtra = Table("", ";", intArrayOf(5, 35, 10))

        // kitchen
        myPrinter.setFont(0, 1, 1, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("*** KITCHEN ***")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3)

        // date
        myPrinter.setFont(0, 0, 0, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText(date.toString())
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // receipt
        myPrinter.setFont(0, 0, 0, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Receipt No : ${waiter.ship_id}")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

        // Table
        myPrinter.setFont(0, 0, 0, 1, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("Table No : ${waiter.table} ")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

        // Order
        waiter.menu = getOrderList(waiter.menu)

        waiter.menu.forEach {

            if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name) {
                tableForMakan.addRow("${it.quantity}x;${it.title};")
                hasMakan = true
            }

            if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                tableForMinum.addRow("${it.quantity}x;${it.title};")
                hasMinum = true
            }

            if (it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                tableForExtra.addRow("${it.quantity}x;${it.title};")
                hasExtra = true
            }

        }

        if (hasMakan) {

            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("-------------------[ MAKANAN ]------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // MAKANAN
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForMakan)

            if (!hasExtra) {

                myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

                // cutter
                myPrinter.cutPaper(48, 7)

            }

        }

        if (hasExtra) {

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("--------------------[ EXTRA ]-------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // EXTRA
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForExtra)

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

            // cutter
            myPrinter.cutPaper(48, 7)

        }

        if (hasMinum) {

            // date
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText(date.toString())
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

            // receipt
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("Receipt No : ${waiter.ship_id}")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // Table
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("Table No : ${waiter.table} ")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2)

            // line
            myPrinter.setFont(0, 0, 0, 0, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
            myPrinter.printText("-------------------[ MINUMAN ]------------------")
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)

            // MINUM
            myPrinter.setFont(0, 0, 0, 1, 0)
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
            myPrinter.printTable(tableForMinum)
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)

            // cutter
            myPrinter.cutPaper(48, 7)

        }
    }

    fun testPrint() {

        if (!isConnected) return

        myPrinter.setFont(0, 1, 1, 0, 0)
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
        myPrinter.printText("*** TEST PRINT ***")
        myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 7)
        myPrinter.cutPaper(48, 7)

    }
}