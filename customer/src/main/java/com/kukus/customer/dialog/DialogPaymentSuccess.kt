package com.kukus.customer.dialog


import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kukus.customer.R


class DialogPaymentSuccess : DialogFragment() {

    private var root_view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root_view = inflater.inflate(R.layout.dialog_payment_success, container, false)

        (root_view!!.findViewById(R.id.fab) as FloatingActionButton).setOnClickListener { dismiss() }

        return root_view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}