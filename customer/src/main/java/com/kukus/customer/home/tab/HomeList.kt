package com.kukus.customer.home.tab

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.data
import com.cipolat.superstateview.SuperStateView
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDateLong
import com.kukus.library.FirebaseUtils.Companion.getMenuRef
import com.kukus.library.FirebaseUtils.Companion.getSetting
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.model.Menu
import com.kukus.library.model.Time
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_time_work.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find

class HomeList : Fragment() {

    private lateinit var recycleView: RecyclerView
    private lateinit var progressbar: ProgressBar
    private lateinit var empty: SuperStateView
    private lateinit var textTitle: TextView
    private lateinit var menu: TextView
    private lateinit var time: TextView
    private lateinit var foodMenuAdapter: FoodMenuAdapter
    private lateinit var drinkMenuAdapter: DrinkMenuAdapter
    private lateinit var extraMenuAdapter: FoodMenuAdapter
    private var timeWork = Time()

    lateinit var statusMenu: TYPE_ORDER

    companion object {
        fun newInstance(type: TYPE_ORDER): HomeList {

            val bundle = Bundle()
            val fragment = HomeList()

            bundle.putSerializable(EXTRA_STATUS, type)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recycleView = view.find(R.id.recycleView)
        progressbar = view.find(R.id.progressbar)
        textTitle = view.find(R.id.text_title)
        empty = view.find(R.id.empty)
        menu = view.find(R.id.menu)
        time = view.find(R.id.time)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        if (context != null && arguments != null) {

            statusMenu = arguments?.get(EXTRA_STATUS) as TYPE_ORDER

            setRecyclerView()
            setFirebaseView()


        } else {

            empty.visibility = View.GONE

        }

    }

    private fun setViewVisible(isVisible: Boolean) {

        Handler().postDelayed({

            progressbar.visibility = View.GONE

            if (!isVisible) {

                recycleView.visibility = View.GONE
                empty.visibility = View.VISIBLE

            } else {

                recycleView.visibility = View.VISIBLE
                empty.visibility = View.GONE

            }

        }, 1000)

    }

    @SuppressLint("CheckResult")
    private fun setFirebaseView() {

        doAsync {

            getSetting("time").data().subscribe { data ->

                if (data.exists()) {

                    val dataTime = data.getValue(Time::class.java)

                    if (dataTime != null) {

                        timeWork = dataTime
                        time.visibility = View.VISIBLE

                        if(timeWork.open) {

                            val timeOpen  = getDateLong(timeWork.time_1, timeWork.time_type_1)
                            val timeClose = getDateLong(timeWork.time_2, timeWork.time_type_2)

                            timeWork.open = getTimestamp in timeOpen until timeClose

                        }

                        if(!timeWork.open)
                        {

                            time.text = "CLOSE"
                            time.setTextColor(Color.parseColor("#F44336"))
                            time.setBackgroundResource(R.drawable.shape_radius_red)

                        }
                    }

                }else{

                    timeWork = Time(true,"PM","12:00", "PM","06:00", "")

                    getSetting("time").setValue(timeWork)

                    time.visibility = View.VISIBLE

                    if(timeWork.open) {

                        val timeOpen  = getDateLong(timeWork.time_1, timeWork.time_type_1)
                        val timeClose = getDateLong(timeWork.time_2, timeWork.time_type_2)

                        timeWork.open = getTimestamp in timeOpen until timeClose

                    }

                    if(!timeWork.open)
                    {

                        time.text = "CLOSE"
                        time.setTextColor(Color.parseColor("#F44336"))
                        time.setBackgroundResource(R.drawable.shape_radius_red)

                    }

                }

                getAdapter()

            }

        }
    }

    @SuppressLint("CheckResult")
    private fun getAdapter() {

        getMenuRef.data().subscribe({

            if (it.exists()) {

                val listOrders = arrayListOf<Menu>()

                it.children.forEach { data ->

                    val menu = data.getValue(Menu::class.java)

                    if (menu != null) {

                        if (menu.type == statusMenu && menu.show) {

                            listOrders.add(menu)

                        }
                    }
                }

                if (listOrders.size > 0) {

                    when (statusMenu) {
                        TYPE_ORDER.FOOD -> {
                            foodMenuAdapter.isOpen(timeWork.open)
                            foodMenuAdapter.swapAdapter(listOrders)
                        }
                        TYPE_ORDER.DRINK -> {
                            drinkMenuAdapter.isOpen(timeWork.open)
                            drinkMenuAdapter.swapAdapter(listOrders)
                        }
                        TYPE_ORDER.EXTRA -> {
                            extraMenuAdapter.isOpen(timeWork.open)
                            extraMenuAdapter.swapAdapter(listOrders)
                        }
                        else -> {
                        }


                    }

                    setViewVisible(true)

                }else{

                    setViewVisible(false)

                }

            }else{

                setViewVisible(false)

            }


        }) {

            setViewVisible(false)

        }
    }

    private fun setRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        linearLayoutManager.stackFromEnd = true
        recycleView.layoutManager = linearLayoutManager
        recycleView.isNestedScrollingEnabled = false
        recycleView.setItemViewCacheSize(10)

        textTitle.text = statusMenu.name

        when (statusMenu) {
            TYPE_ORDER.FOOD -> {
                foodMenuAdapter = FoodMenuAdapter(context!!)
                recycleView.adapter = foodMenuAdapter
            }
            TYPE_ORDER.DRINK -> {
                drinkMenuAdapter = DrinkMenuAdapter(context!!)
                recycleView.adapter = drinkMenuAdapter
            }
            TYPE_ORDER.EXTRA -> {
                extraMenuAdapter = FoodMenuAdapter(context!!)
                recycleView.adapter = extraMenuAdapter
            }
        }

        menu.setOnClickListener {
            (activity as ActivityHome).drawer_layout.openDrawer(Gravity.START)
        }
        time.setOnClickListener {
            getTimeWork()
        }



    }


    @SuppressLint("CheckResult")
    private fun getTimeWork() {

        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_time_work, null)

        dialogView.progress.visibility = View.GONE

        dialogView.date.text = FirebaseUtils.getDateConvert(FirebaseUtils.getDate, "/")
        dialogView.time.text = ("${timeWork.time_1} ${timeWork.time_type_1} - ${timeWork.time_2} ${timeWork.time_type_2}")

        if (!timeWork.open) {

            dialogView.time.paint.flags = (Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
            dialogView.time.setTextColor(Color.parseColor("#F44336"))

            dialogView.header.setBackgroundResource(R.color.colorRed)
            dialogView.header.text = ("CLOSE")

        } else {

            dialogView.time.setTextColor(Color.parseColor("#32c360"))

            dialogView.header.setBackgroundResource(R.color.colorGreen)
            dialogView.header.text = ("OPEN")

        }

        if (timeWork.note.isNotEmpty()) {

            dialogView.layout_note.visibility = View.VISIBLE
            dialogView.note.text = timeWork.note
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.create().show()

    }

}

