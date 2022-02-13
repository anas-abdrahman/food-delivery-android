package com.kukus.administrator.waiter

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.dialog.DialogCheckout
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.TABLE
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getTableRef
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWaiterRef
import com.kukus.library.model.Order
import com.kukus.library.model.Table
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.activity_waiter.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*

class ActivityWaiter : AppCompatActivity() {

    var statusTable1 = TABLE.EMPTY
    var statusTable2 = TABLE.EMPTY
    var statusTable3 = TABLE.EMPTY
    var statusTable4 = TABLE.EMPTY
    var statusTable5 = TABLE.EMPTY
    var statusTable6 = TABLE.EMPTY
    var statusTable7 = TABLE.EMPTY
    var statusTable8 = TABLE.EMPTY
    var statusTable9 = TABLE.EMPTY
    var statusTable10 = TABLE.EMPTY

    private val emptyTable = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiter)

        initTableStatus()
        initClickListener()

    }

    @SuppressLint("CheckResult")
    private fun initTableStatus() {

        getTableRef.dataChanges().subscribe { data ->

            if (data.exists()) {

                val table = data.getValue(Table::class.java)

                if (table != null) {

                    if (getDay(getTimestamp) == getDay(table.date)) {

                        emptyTable.clear()

                        statusTable1 = setTableStatus(table_1, txt_table_1, table.table_1, table.order_1, 1)
                        statusTable2 = setTableStatus(table_2, txt_table_2, table.table_2, table.order_2, 2)
                        statusTable3 = setTableStatus(table_3, txt_table_3, table.table_3, table.order_3, 3)
                        statusTable4 = setTableStatus(table_4, txt_table_4, table.table_4, table.order_4, 4)
                        statusTable5 = setTableStatus(table_5, txt_table_5, table.table_5, table.order_5, 5)
                        statusTable6 = setTableStatus(table_6, txt_table_6, table.table_6, table.order_6, 6)
                        statusTable7 = setTableStatus(table_7, txt_table_7, table.table_7, table.order_7, 7)
                        statusTable8 = setTableStatus(table_8, txt_table_8, table.table_8, table.order_8, 8)
                        statusTable9 = setTableStatus(table_9, txt_table_9, table.table_9, table.order_9, 9)
                        statusTable10 = setTableStatus(table_10, txt_table_10, table.table_10, table.order_10, 10)

                    } else {

                        getTableRef.setValue(Table())

                    }

                }

            } else {

                getTableRef.setValue(Table())

            }
        }
    }

    private fun getDay(timestamp: Long): String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance(); calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH).toString()

    }

    private fun initClickListener() {

        table_1.setOnClickListener {

            if (statusTable1 == TABLE.EMPTY) {
                createOrder(1)
            } else {
                updateOrder(1)
            }

        }
        table_2.setOnClickListener {
            if (statusTable2 == TABLE.EMPTY) {
                createOrder(2)
            } else {
                updateOrder(2)
            }
        }
        table_3.setOnClickListener {
            if (statusTable3 == TABLE.EMPTY) {
                createOrder(3)
            } else {
                updateOrder(3)
            }
        }
        table_4.setOnClickListener {
            if (statusTable4 == TABLE.EMPTY) {
                createOrder(4)
            } else {
                updateOrder(4)
            }
        }
        table_5.setOnClickListener {
            if (statusTable5 == TABLE.EMPTY) {
                createOrder(5)
            } else {
                updateOrder(5)
            }
        }
        table_6.setOnClickListener {
            if (statusTable6 == TABLE.EMPTY) {
                createOrder(6)
            } else {
                updateOrder(6)
            }
        }
        table_7.setOnClickListener {
            if (statusTable7 == TABLE.EMPTY) {
                createOrder(7)
            } else {
                updateOrder(7)
            }
        }
        table_8.setOnClickListener {
            if (statusTable8 == TABLE.EMPTY) {
                createOrder(8)
            } else {
                updateOrder(8)
            }
        }
        table_9.setOnClickListener {
            if (statusTable9 == TABLE.EMPTY) {
                createOrder(9)
            } else {
                updateOrder(9)
            }
        }
        table_10.setOnClickListener {
            if (statusTable10 == TABLE.EMPTY) {
                createOrder(10)
            } else {
                updateOrder(10)
            }
        }
    }

    private fun updateOrder(table: Int) {

        val menus = arrayOf("Edit Order", "Cancel Order", "Move Table", "Combine Table", "Print Order", "Checkout")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Table")
        builder.setItems(menus) { _, which ->
            when (which) {
                0 -> editOrder(table)
                1 -> cancelOrder(table)
                2 -> moveTable(table)
                3 -> combineTable(table)
                4 -> printResiept(table)
                5 -> checkoutOrder(table)
            }
        }

        builder.show()
    }

    private fun printResiept(table: Int) {

        //val print = Print(context, context as ActivityOrders)

        //print.runPrintReceiptSequence(ship, user)

        Handler().postDelayed({

            toast("print!")

        }, 500)

    }

    private fun setTableStatus(cardView: CardView, textView: TextView, status: TABLE, order: String, table: Int = 0): TABLE {

        when (status) {
            TABLE.WAITING -> {
                cardView.setCardBackgroundColor(Color.parseColor("#d2cdd1"))
                textView.text = order
            }
            TABLE.COMBINE -> {
                cardView.setCardBackgroundColor(Color.parseColor("#c0c9e3"))
                textView.text = order
            }
            TABLE.PLACED -> {
                cardView.setCardBackgroundColor(Color.parseColor("#32c360"))
                textView.text = order
            }
            else -> {
                emptyTable.add(table.toString())
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                textView.text = ("Empty")
            }
        }

        return status
    }

    private fun createOrder(table: Int) {

        val intent = Intent(this, ActivityAddOrder::class.java)
        intent.putExtra("table", table)
        startActivity(intent)

    }

    private fun getShipId(table: Int): String {

        val id = resources.getIdentifier("txt_table_$table", "id", packageName)

        return find<TextView>(id).text.toString()
    }

    private fun editOrder(table: Int) {

        val shipId = getShipId(table)
        val intent = Intent(this, ActivityAddOrder::class.java)

        intent.putExtra("table", table)
        intent.putExtra("shipId", shipId)
        intent.putExtra("edit_table", true)
        startActivity(intent)

    }

    private fun combineTable(table: Int) {

        val shipId = getShipId(table)
        val emptyTable = emptyTable.toTypedArray()
        val dialogEmptyTable = AlertDialog.Builder(this)

        dialogEmptyTable.setTitle("Select Table :")
        dialogEmptyTable.setItems(emptyTable) { _, position ->

            val selectedTable = emptyTable[position].toInt()

            getWaiter(getDate).data().subscribe { value ->

                if (value.exists()) {

                    value.children.forEach { data ->

                        val waiter = data.getValue(Waiter::class.java)

                        if (waiter != null) {

                            if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                                getTableRef.child("table_$selectedTable").setValue(TABLE.COMBINE)
                                getTableRef.child("order_$selectedTable").setValue(waiter.ship_id)

                            }
                        }
                    }
                }
            }
        }

        dialogEmptyTable.show()
    }

    private fun moveTable(table: Int) {

        val shipId = getShipId(table)
        val emptyTable = emptyTable.toTypedArray()
        val dialogEmptyTable = AlertDialog.Builder(this)

        dialogEmptyTable.setTitle("Select Table :")
        dialogEmptyTable.setItems(emptyTable) { _, position ->

            val selectedTable = emptyTable[position].toInt()

            getWaiter(getDate).data().subscribe { value ->

                if (value.exists()) {

                    value.children.forEach { data ->

                        val waiter = data.getValue(Waiter::class.java)

                        if (waiter != null) {

                            if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) { // KENA CHECK SINI BILA DAH ORDER TAK MASUK SINI

                                if (waiter.table == table) {

                                    getWaiterRef.child(getDate).child(waiter.id).child("table").setValue(selectedTable)
                                    getTableRef.child("table_$selectedTable").setValue(getTableStatus(table))

                                } else {

                                    getTableRef.child("table_$selectedTable").setValue(TABLE.COMBINE)

                                }

                                getTableRef.child("order_$selectedTable").setValue(waiter.ship_id)
                                getTableRef.child("table_$table").setValue(TABLE.EMPTY)
                                getTableRef.child("order_$table").setValue("")
                            }
                        }
                    }
                }
            }
        }

        dialogEmptyTable.show()
    }

    private fun getTableStatus(table: Int): TABLE {

        return when (table) {
            1 -> statusTable1
            2 -> statusTable2
            3 -> statusTable3
            4 -> statusTable4
            5 -> statusTable5
            6 -> statusTable6
            7 -> statusTable7
            8 -> statusTable8
            9 -> statusTable9
            10 -> statusTable10
            else -> TABLE.EMPTY
        }

    }

    @SuppressLint("CheckResult")
    private fun cancelOrder(table: Int) {

        val shipId = getShipId(table)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                            if (waiter.table == table) {

                                getWaiterRef
                                        .child(getDate)
                                        .child(waiter.id)
                                        .child("status")
                                        .setValue(STATUS.CANCEL)

                                removeTable(shipId)

                            } else {

                                getTableRef.child("table_$table").setValue(TABLE.EMPTY)
                                getTableRef.child("order_$table").setValue("")

                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun removeTable(shipId: String) {

        getTableRef.data().subscribe { checkTable ->

            if (checkTable.exists()) {

                val myTable = checkTable.getValue(Table::class.java)

                if (myTable != null) {

                    if (myTable.order_1 == shipId) {
                        getTableRef.child("table_1").setValue(TABLE.EMPTY)
                        getTableRef.child("order_1").setValue("")
                    }
                    if (myTable.order_2 == shipId) {
                        getTableRef.child("table_2").setValue(TABLE.EMPTY)
                        getTableRef.child("order_2").setValue("")
                    }
                    if (myTable.order_3 == shipId) {
                        getTableRef.child("table_3").setValue(TABLE.EMPTY)
                        getTableRef.child("order_3").setValue("")
                    }
                    if (myTable.order_4 == shipId) {
                        getTableRef.child("table_4").setValue(TABLE.EMPTY)
                        getTableRef.child("order_4").setValue("")
                    }
                    if (myTable.order_5 == shipId) {
                        getTableRef.child("table_5").setValue(TABLE.EMPTY)
                        getTableRef.child("order_5").setValue("")
                    }
                    if (myTable.order_6 == shipId) {
                        getTableRef.child("table_6").setValue(TABLE.EMPTY)
                        getTableRef.child("order_6").setValue("")
                    }
                    if (myTable.order_7 == shipId) {
                        getTableRef.child("table_7").setValue(TABLE.EMPTY)
                        getTableRef.child("order_7").setValue("")
                    }
                    if (myTable.order_8 == shipId) {
                        getTableRef.child("table_8").setValue(TABLE.EMPTY)
                        getTableRef.child("order_8").setValue("")
                    }
                    if (myTable.order_9 == shipId) {
                        getTableRef.child("table_9").setValue(TABLE.EMPTY)
                        getTableRef.child("order_9").setValue("")
                    }
                    if (myTable.order_10 == shipId) {
                        getTableRef.child("table_10").setValue(TABLE.EMPTY)
                        getTableRef.child("order_10").setValue("")
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun checkoutOrder(table: Int) {

        val shipId = getShipId(table)
        //val dialogBuilder = AlertDialog.Builder(this)
        //val dialogView = layoutInflater.inflate(R.layout.dialog_menu_checkout, null)

        //val layout = dialogView.findViewById(R.id.layout_price) as LinearLayout
        //val price = dialogView.findViewById(R.id.price) as TextView
        //val progressBar = dialogView.findViewById(R.id.progressBar) as ProgressBar

        //dialogBuilder.setView(dialogView)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

                //progressBar.visibility = View.GONE
                //layout.visibility = View.VISIBLE

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                            //price.text = waiter.price.toString()

                            // dialogBuilder.setPositiveButton("Pay") { _, _ ->

                            val fragmentManager = supportFragmentManager
                            val newFragment = DialogCheckout()
                            val transaction = fragmentManager?.beginTransaction()

                            if (transaction != null) {
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

                            }

                        }

                        //dialogBuilder.setNegativeButton("Cancel") { _, _ ->

                        // }

                        // dialogBuilder.create().show()
                    }
                }
            }
        }
    }
}
