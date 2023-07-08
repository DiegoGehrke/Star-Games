package com.star.games.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RulesDialog(private val context: Context) {

    private lateinit var rulesDialogBuilder: Dialog
    private lateinit var adapter: ItemAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var listView: RecyclerView
    private lateinit var closeDialogBtn: ImageView

    fun showEventRulesDialog(itemList: List<String>) {
        rulesDialogBuilder = Dialog(context)
        rulesDialogBuilder.setCancelable(true)
        rulesDialogBuilder.setContentView(R.layout.rules_dialog_layout)
        rulesDialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        layoutManager = LinearLayoutManager(context)
        adapter = ItemAdapter(itemList)

        listView = rulesDialogBuilder.findViewById(R.id.rules_list)
        listView.layoutManager = layoutManager
        val spacingInPixels = context.resources.getDimensionPixelSize(R.dimen.item_spacing)
        val itemDecoration = ItemSpacingDecoration(spacingInPixels)
        listView.adapter = adapter
        listView.addItemDecoration(itemDecoration)

        closeDialogBtn = rulesDialogBuilder.findViewById(R.id.dialog_close_btn)
        closeDialogBtn.setOnClickListener {
            rulesDialogBuilder.dismiss()
        }

        rulesDialogBuilder.show()
    }
}
