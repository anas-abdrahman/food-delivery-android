package com.kukus.customer.tracking


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import org.jetbrains.anko.find

class DialogMessage : DialogFragment() {


    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val viewRoot = inflater.inflate(R.layout.dialog_message, container, false)
        val fab = viewRoot.find<FloatingActionButton>(R.id.fab)
        val input = viewRoot.find<EditText>(R.id.input)

        val id = arguments?.getString("id") ?: ""

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val name = sharedPreferences.getString(Constant.EXTRA_USER_NAME,"")
        val email = sharedPreferences.getString(Constant.EXTRA_USER_EMAIL, "")

        RxView.clicks(fab)
                .filter {
                    input.text.isNotBlank() && input.text.isNotEmpty()
                }
                .subscribe {

                    val key = getPushKey

                    getShip(getDate).child(id).child("message").child(key).setValue(
                            Message(key,
                                    getUserId,
                                    "delivery",
                                    email,
                                    name,
                                    input.text.toString(),
                                    false
                            )
                    )
                    input.setText("")

                }


        displayChatMessage(viewRoot, id)

        return viewRoot
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        //dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }


    private fun displayChatMessage(view: View, id : String){

        val query = getShip(getDate).child(id).child("message")

        val options = FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_chat)
                .setQuery(query, Message::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<Message>(options) {

            override fun populateView(v: View, model: Message, position: Int) {

                val messageText = v.findViewById(R.id.message_text) as TextView
                val messageUser = v.findViewById(R.id.message_user) as TextView
                val messageTime = v.findViewById(R.id.message_time) as TextView
                val imageView8 = v.findViewById(R.id.imageView8) as ImageView

                imageView8.visibility = View.GONE

                if(model.user_id != getUserId)
                {

                    messageText.setBackgroundResource(R.drawable.shape_radius_blue_5)
                    messageText.setTextColor(Color.WHITE)

                    messageUser.setTextColor(Color.parseColor("#2E87E6"))
                    messageTime.setTextColor(Color.parseColor("#2E87E6"))

                }else{

                    messageText.setBackgroundResource(R.drawable.shape_radius_grey_5)
                    messageText.setTextColor(Color.BLACK)

                    messageUser.setTextColor(Color.GRAY)
                    messageTime.setTextColor(Color.GRAY)

                }

                // Set their text
                messageText.text = model.text
                messageUser.text = model.title

                // Format the date before showing it
                messageTime.text = DateFormat.format("dd/MM/yyyy - HH:mm:ss",  model.time)

            }
        }

        view.find<ListView>(R.id.list_of_messages).adapter = mAdapter
        //view.find<ListView>(R.id.list_of_messages).setSelection(mAdapter.count)


    }
}