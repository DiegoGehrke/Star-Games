package com.star.games.android

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast

@SuppressLint("NotifyDataSetChanged")
class WithdrawHistoryListAdapter(private val context: Context, query: Query, private val window: Window) :
    RecyclerView.Adapter<WithdrawHistoryListAdapter.ViewHolder>() {
    private var withdrawRequests: List<DocumentSnapshot>

    init {
        withdrawRequests = ArrayList()
        query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (queryDocumentSnapshots != null) {
                withdrawRequests = queryDocumentSnapshots.documents
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.withdraw_history_item_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = withdrawRequests[position]
        var method = ""
        val amount = document.getDouble("amount")!!
        val date = document.getString("date")
        if (document.contains("pixKey")) {
             method = "PIX"
        }
        val id = document.getString("id") ?: "0"
        val status = document.getString("status")

        holder.amountTextView.text = context.getString(R.string.amount, amount.toString())
        holder.dateTextView.text = context.getString(R.string.date, date)
        holder.methodTextView.text = context.getString(R.string.method, method)
        holder.idTextView.text = context.getString(R.string.id, id.substring(0, 12) + "...")
        holder.statusTextView.text = context.getString(R.string.status, status)
        when (status) {
            context.getString(R.string.pending) -> {
                val setTextColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getColor(R.color.analysisColor)
                } else {
                    ContextCompat.getColor(context, R.color.analysisColor)
                }
                holder.statusTextView.setTextColor(setTextColor)
            }
            context.getString(R.string.rejected) -> {
                val setTextColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getColor(R.color.rejectedColor)
                } else {
                    ContextCompat.getColor(context, R.color.rejectedColor)
                }
                holder.statusTextView.setTextColor(setTextColor)
            }
            else -> {
                val setTextColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getColor(R.color.approvedColor)
                } else {
                    ContextCompat.getColor(context, R.color.approvedColor)
                }
                holder.statusTextView.setTextColor(setTextColor)
            }
        }
        holder.copyButton.setOnClickListener {
            showCustomSuccessToast(
                context,
                context.getString(R.string.order_id_successfully_copied_to_your_clipboard),
                Toast.LENGTH_LONG,
                window
            )
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("ID", id)
            clipboardManager.setPrimaryClip(clipData)
        }
    }

    override fun getItemCount(): Int {
        return withdrawRequests.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var amountTextView: TextView
        var dateTextView: TextView
        var methodTextView: TextView
        var idTextView: TextView
        var statusTextView: TextView
        val copyButton: ImageView = itemView.findViewById(R.id.copyIdButton)

        init {
            amountTextView = itemView.findViewById(R.id.amountTextView)
            dateTextView = itemView.findViewById(R.id.dateTextView)
            methodTextView = itemView.findViewById(R.id.methodTextView)
            idTextView = itemView.findViewById(R.id.idTextView)
            statusTextView = itemView.findViewById(R.id.statusTextView)
        }
    }
}