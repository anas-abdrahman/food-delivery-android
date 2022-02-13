package com.kukus.administrator.staff.tab

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.androidhuman.rxfirebase2.database.*
import com.cipolat.superstateview.SuperStateView
import com.kukus.administrator.R
import com.kukus.administrator.staff.ActivityAddStaff
import com.kukus.administrator.staff.ActivityStaff
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.FirebaseUtils.Companion.getStaffRef
import com.kukus.library.model.Staff
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find

class StaffList : Fragment() {

    var statusStaff : Constant.Companion.USER = Constant.Companion.USER.ADMIN

    companion object {

        fun newInstance(status: Constant.Companion.USER): StaffList {

            val bundle = Bundle()
            val fragment = StaffList()

            bundle.putSerializable(EXTRA_STATUS, status)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusStaff = arguments?.getSerializable(EXTRA_STATUS) as Constant.Companion.USER

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val view = inflater.inflate(R.layout.fragment_staff, container, false)
        val recyclerView = view.find<RecyclerView>(R.id.recyclerView)
        val progress = view.find<LinearLayout>(R.id.progress)
        val empty = view.find<SuperStateView>(R.id.empty)
        val staffAdapter = StaffAdapter()
        val addStaff = view.find<FloatingActionButton>(R.id.add_user)

        recyclerView.adapter = staffAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(30)

        addStaff.setOnClickListener {

            val intent = Intent(context, ActivityAddStaff::class.java)
            intent.putExtra("role", statusStaff)
            startActivity(intent)

        }


        doAsync {

            val ref = getStaffRef

            ref.dataChanges().subscribe {

                if (it.exists()) {

                    val listStaff = arrayListOf<Staff>()

                    it.children.forEach { data ->

                        val staff = data.getValue(Staff::class.java)

                        if (staff != null) {

                            if (staff.role == statusStaff) {

                                listStaff.add(staff)
                            }
                        }
                    }

                    if (listStaff.size > 0) {

                        staffAdapter.swapAdapter(listStaff)
                        staffAdapter.notifyDataSetChanged()

                        recyclerView.visibility = View.VISIBLE
                        empty.visibility = View.GONE


                    } else {

                        recyclerView.visibility = View.GONE
                        empty.visibility = View.VISIBLE

                    }

                } else {

                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }


                Handler().postDelayed({
                    progress.visibility = View.GONE
                }, 1000)
            }

        }

        return view
    }
}
