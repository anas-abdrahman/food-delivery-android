package com.kukus.customer.dialog


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.kukus.customer.R
import org.jetbrains.anko.find


class DialogAddress : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_address, container, false)
        val building = viewRoot.find<EditText>(R.id.building)
        val floor = viewRoot.find<EditText>(R.id.floor)
        val apartment = viewRoot.find<EditText>(R.id.apartment)
        val submit = viewRoot.find<Button>(R.id.submit)


        submit.setOnClickListener {

            if(listener != null) {
                listener?.onDataSend(building.text.toString(), floor.text.toString(), apartment.text.toString())
            }

        }

        return viewRoot
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        try {
            listener = activity as AddressDialogListener
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    var listener : AddressDialogListener? = null

    interface AddressDialogListener{
        fun onDataSend(builder: String, floor: String, apertment: String)
    }

}