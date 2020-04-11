package com.romellbolton.putmeon.controller

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import com.romellbolton.putmeon.R

class AlertDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val context: Context = activity
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_button_ok_text, null)
        return builder.create()
    }
}