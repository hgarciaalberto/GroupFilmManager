package com.ahgitdevelopment.groupfilmmanager.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.ahgitdevelopment.groupfilmmanager.R


class JoinDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, theme)
    }

    override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.input_text, container, false)


        val databaseId = view.findViewById<EditText>(R.id.dialogDatabaseIdText)

        view.findViewById<Button>(R.id.btnJoin).setOnClickListener {
            dismiss()
            listener.onJoinClickListener(databaseId.text.toString())
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        return view
    }

    interface OnJoinClickListener {
        fun onJoinClickListener(databaseId: String)
    }

    companion object {
        private lateinit var listener: OnJoinClickListener

        fun newInstance(listener: OnJoinClickListener): JoinDialogFragment? {
            Companion.listener = listener
            return JoinDialogFragment()
        }
    }
}