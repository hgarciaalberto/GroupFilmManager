package com.ahgitdevelopment.groupfilmmanager.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

        view.findViewById<TextView>(R.id.title).apply {
            text = arguments?.getString(TITLE, "") ?: ""
        }

        val text = view.findViewById<EditText>(R.id.text)

        view.findViewById<Button>(R.id.btnJoin).setOnClickListener {
            if (text.text.isNotBlank()) {
                dismiss()
                when (listener) {
                    is OnJoinClickListener ->
                        (listener as OnJoinClickListener).onJoinClickListener(text.text.toString())

                    is OnAddNameClickListener ->
                        (listener as OnAddNameClickListener).onAddNameClickListener(text.text.toString())
                }

            } else {
                Toast.makeText(requireActivity(), R.string.error_message_empty_value, Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    interface MyListener

    interface OnJoinClickListener : MyListener {
        fun onJoinClickListener(databaseId: String)
    }

    interface OnAddNameClickListener : MyListener {
        fun onAddNameClickListener(name: String)
    }

    companion object {
        private const val TITLE = "title"
        private lateinit var listener: MyListener

        fun newInstance(listener: MyListener, title: String): JoinDialogFragment? {
            Companion.listener = listener
            return JoinDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                }
            }
        }
    }
}