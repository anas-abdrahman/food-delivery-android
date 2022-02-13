package com.kukus.administrator.menu.tab

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.*
import com.cipolat.superstateview.SuperStateView
import com.kukus.administrator.R
import com.kukus.administrator.menu.ActivityFormDrink
import com.kukus.administrator.menu.ActivityFormMenu
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.FirebaseUtils.Companion.getMenuRef
import com.kukus.library.model.Menu
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import java.lang.StringBuilder

class MenuList : Fragment() {

    lateinit var statusMenu : TYPE_ORDER

    companion object {

        fun newInstance(status: TYPE_ORDER): MenuList {

            val bundle = Bundle()
            val fragment = MenuList()

            bundle.putSerializable(EXTRA_STATUS, status)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusMenu = arguments?.getSerializable(EXTRA_STATUS) as TYPE_ORDER

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val recyclerView = view.find<RecyclerView>(R.id.recyclerView)
        val progress = view.find<LinearLayout>(R.id.progress)
        val textList = view.find<TextView>(R.id.txt_info)
        val empty = view.find<SuperStateView>(R.id.empty)
        val add_menu = view.find<FloatingActionButton>(R.id.add_menu)
        val menuAdapter = MenuAdapter()

        add_menu.setOnClickListener {

            val intent = if(statusMenu == TYPE_ORDER.DRINK) {

                Intent(context, ActivityFormDrink::class.java)

            }else{

                Intent(context, ActivityFormMenu::class.java)

            }

            intent.putExtra("type", statusMenu)

            startActivity(intent)

        }

        recyclerView.adapter = menuAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(30)

        doAsync {

            getMenuRef.dataChanges().subscribe {

                if (it.exists()) {

                    val listMenu = arrayListOf<Menu>()

                    it.children.forEach { data ->

                        val menu = data.getValue(Menu::class.java)

                        if (menu != null) {

                            if (menu.type == statusMenu) {

                                listMenu.add( Menu(
                                        menu.id,
                                        menu.available,
                                        menu.show,
                                        menu.image,
                                        menu.name,
                                        menu.description,
                                        menu.label_price1,
                                        menu.label_price2,
                                        menu.price1,
                                        menu.price2,
                                        menu.type,
                                        menu.limit,
                                        menu.currentLimit)
                                )
                            }
                        }
                    }

                    if (listMenu.size > 0) {

                        menuAdapter.swapAdapter(listMenu)
                        menuAdapter.notifyDataSetChanged()

                        recyclerView.visibility = View.VISIBLE
                        empty.visibility = View.GONE


                    } else {

                        recyclerView.visibility = View.GONE
                        empty.visibility = View.VISIBLE

                    }

                    textList.text = StringBuilder("Total List (" + listMenu.size.toString() + ")")

                } else {

                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }

                progress.visibility = View.GONE
            }

        }

        return view
    }
}
