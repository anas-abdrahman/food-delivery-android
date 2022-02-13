package com.kukus.administrator.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getDateConvert
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getShipRef
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWaiterRef
import com.kukus.library.model.Ship
import com.kukus.library.model.StatisticNew
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.activity_statistic.*
import java.util.*
import kotlin.math.min


class ActivityStatisticNew : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        getDataFirebase()

    }

    @SuppressLint("CheckResult")
    fun getDataFirebase() {

        var delivery = 0f
        var makanan = 0f
        var minuman = 0f

        /* TODAY */
        getShip(getDate).data().subscribe { shipRef ->

            if (shipRef.exists()) {

                shipRef.children.forEach { data ->

                    val ship = data.getValue(Ship::class.java)

                    if (ship != null) {

                        if (ship.status == Constant.Companion.STATUS.COMPLETE) {

                            ship.menu.forEach {

                                if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                                    minuman += (it.price1 + it.price2) * it.quantity
                                } else if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name || it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                                    makanan += (it.price1) * it.quantity
                                }

                            }

                            delivery += ship.delivery

                        }
                    }
                }

            }
        }

        getWaiter(getDate).dataChanges().subscribe { waiterRef ->

            if (waiterRef.exists()) {

                waiterRef.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.status == Constant.Companion.STATUS.COMPLETE) {

                            waiter.menu.forEach {

                                if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                                    minuman += it.price1 + it.price2
                                } else if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name || it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                                    makanan += it.price1
                                }

                            }
                        }
                    }
                }

                txt_makanan.text = makanan.toString()
                txt_minuman.text = minuman.toString()
                txt_delivery.text = delivery.toString()
                txt_total.text = (makanan + minuman + delivery).toString()
            }
        }

        /* HISTORY*/

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val orderAdapter = StatisticAdapter()
        recyclerView.adapter = orderAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(30)

        val listOrders = mutableListOf<Ship>()
        val hashMapShip = HashMap<String, MutableList<Ship>>()

        getShipRef.data().subscribe { shipRef ->

            if (shipRef.exists()) {

                shipRef.children.forEach { data ->

                    val date = data.key

                    if (date != null) {

                        data.children.forEach { child ->

                            val ship = child.getValue(Ship::class.java)

                            if (ship != null) {

                                listOrders.add(ship)

                            }
                        }

                        hashMapShip[date] = listOrders

                    }
                }

            }
        }


        val listWaiter = mutableListOf<Waiter>()
        val listStatistic = arrayListOf<StatisticNew>()
        val hashMapWaiter = HashMap<String, MutableList<Waiter>>()

        getWaiterRef.dataChanges().subscribe { waiterRef ->

            if (waiterRef.exists()) {


                waiterRef.children.forEach { data ->

                    val date = data.key

                    if (date != null) {

                        data.children.forEach { child ->

                            val waiter = child.getValue(Waiter::class.java)

                            if (waiter != null) {

                                listWaiter.add(waiter)

                            }
                        }

                        hashMapWaiter[date] = listWaiter

                    }
                }

                //progress.visibility = View.GONE

            }
        }

        Handler().postDelayed({


            /** static calculate **/





            for ((key2, value2) in hashMapWaiter) {

                var deliveryWaiter = 0f
                var makananWaiter = 0f
                var minumanWaiter = 0f

                value2.forEach { waiter ->

                    if (key2 == waiter.date && waiter.status == Constant.Companion.STATUS.COMPLETE) {

                        waiter.menu.forEach {

                            if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                                minumanWaiter += it.price1 + it.price2
                            } else if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name || it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                                makananWaiter += it.price1
                            }

                        }
                    }

                }

                for ((key1, value1) in hashMapShip) {

                    value1.forEach { ship ->

                        if (key1 == key2 && ship.status == Constant.Companion.STATUS.COMPLETE) {

                            deliveryWaiter += ship.delivery

                            ship.menu.forEach {

                                if (it.type == Constant.Companion.TYPE_ORDER.DRINK.name) {
                                    minumanWaiter += (it.price1 + it.price2) * it.quantity
                                } else if (it.type == Constant.Companion.TYPE_ORDER.FOOD.name || it.type == Constant.Companion.TYPE_ORDER.EXTRA.name) {
                                    makananWaiter += (it.price1) * it.quantity
                                }

                            }
                        }
                    }

                }

                listStatistic.add(StatisticNew(getDateConvert(key2), deliveryWaiter, makananWaiter, minumanWaiter, (deliveryWaiter + makananWaiter + minumanWaiter)))


            }



            orderAdapter.swapAdapter(listStatistic)

            progress.visibility = View.GONE

        }, 2000)

    }

    /*private fun setupChart(data : ArrayList<Statistic>) {

        val entry = arrayListOf<Entry>()

        for (value in data.takeLast(7)){
            entry.add(Entry(value.count.toFloat(), value.total.toFloat()))
        }

        val lds = LineDataSet(entry, "count")
        lds.setCircleColor(Color.RED)
        lds.circleRadius = 8f
        lds.circleHoleRadius = 5f
        lds.setCircleColorHole(Color.WHITE)
        lds.color = Color.RED
        lds.lineWidth = 3f
        val ld = LineData(lds)
        ld.setDrawValues(false)
        chart.data = ld

        val textSize = 12f
        val xa = chart.xAxis
        xa.granularity = 1f
        xa.isGranularityEnabled = true
        xa.valueFormatter = StickyDateAxisValueFormatter(chart, label_1)
        xa.position = XAxis.XAxisPosition.BOTTOM
        xa.textSize = textSize
        xa.setDrawGridLines(true)

        chart.axisLeft.setDrawAxisLine(false)
        chart.axisLeft.gridColor = Color.parseColor("#dddddd")
        chart.axisLeft.textColor = Color.parseColor("#bbbbbb")
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(false)
        chart.xAxis.axisLineColor = Color.parseColor("#aaaaaa")
        chart.xAxis.textColor = Color.parseColor("#bbbbbb")

        val max = chart.data.yMax.toInt()
        chart.axisLeft.labelCount = max

        chart.axisLeft.setValueFormatter { value, axis ->

            return@setValueFormatter Math.floor(value.toDouble()).toInt().toString()
        }


        chart.setVisibleXRangeMaximum(7f)
        chart.axisLeft.textSize = textSize
        //chart.extraBottomOffset = 10f

        chart.axisRight.isEnabled = false
        val desc = Description()
        desc.setText("")
        chart.description = desc
        chart.legend.isEnabled = false
        chart.axisLeft.setDrawGridLines(true)


    }
*/

    /*inner class StickyDateAxisValueFormatter internal constructor(private val chart: LineChart, private val sticky: TextView) : IAxisValueFormatter {

        private val c: Calendar
        private var lastFormattedValue = 1e9f
        private var lastMonth = 0
        private var lastYear = 0
        private var stickyMonth = -1
        private var stickyYear = -1
        private val monthFormatter = SimpleDateFormat("MMM", Locale.getDefault())


        init {
            c = GregorianCalendar()
        }

        override fun getFormattedValue(value: Float, axis: AxisBase): String {

            // Sometimes this gets called on values much lower than the visible range
            // Catch that here to prevent messing up the sticky text logic
            if (value < chart.lowestVisibleX) {
                return ""
            }

            // NOTE: I assume for this example that all count is plotted in days
            // since Jan 1, 2018. Update for your scheme accordingly.

            val days = value.toInt()

            val isFirstValue = value < lastFormattedValue

            if (isFirstValue) {
                // starting over formatting sequence
                lastMonth = 50
                lastYear = 5000

                c.set(2019, 4, 12)
                c.add(Calendar.DATE, chart.lowestVisibleX.toInt())

                stickyMonth = c.get(Calendar.MONTH)
                stickyYear = c.get(Calendar.YEAR)

                val stickyText = monthFormatter.format(c.getTime()) + " " + stickyYear
                sticky.setText(stickyText)
            }

            c.set(2019, 4, 12)
            c.add(Calendar.DATE, days)
            val d = c.getTime()

            val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
            val month = c.get(Calendar.MONTH)
            val year = c.get(Calendar.YEAR)

            val monthStr = monthFormatter.format(d)

            if ((month > stickyMonth || year > stickyYear) && isFirstValue) {
                stickyMonth = month
                stickyYear = year
                val stickyText = monthStr + " " + year
                sticky.setText(stickyText)
            }

            val ret: String

            if ((month > lastMonth || year > lastYear) && !isFirstValue) {
                ret = monthStr
            } else {
                ret = Integer.toString(dayOfMonth)
            }

            lastMonth = month
            lastYear = year
            lastFormattedValue = value

            return ret
        }
    }*/


}
