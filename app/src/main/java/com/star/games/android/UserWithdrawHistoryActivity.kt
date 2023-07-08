package com.star.games.android

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class UserWithdrawHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var activityName: TextView
    private lateinit var keepPlayingTxt: TextView

    private lateinit var backBtn: ImageView
    private lateinit var imageView: ImageView

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid

    private lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_withdraw_history)

        initializeViews()
        fetchUserWithdrawData()
        setViewsOnClickListeners()
        backPressedListener()
        textUI()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recycler_view)
        activityName = findViewById(R.id.activity_name)
        backBtn = findViewById(R.id.back_btn)
        imageView = findViewById(R.id.imageView11)
        keepPlayingTxt = findViewById(R.id.keep_playing)
        loadingDialog = LoadingDialog(this)
    }

    private fun fetchUserWithdrawData() {
        loadingDialog.show()
        val query: Query = db
            .collection("WITHDRAWALS REQUESTS")
            .whereEqualTo("uid", uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    loadingDialog.hide()
                    imageView.visibility = View.GONE
                    keepPlayingTxt.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val adapter = WithdrawHistoryListAdapter(this, query, this.window)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    val spacingItemDecoration = ItemSpacingDecoration(
                        resources.getDimensionPixelSize(R.dimen.item_spacing)
                    )
                    recyclerView.addItemDecoration(spacingItemDecoration)
                    recyclerView.adapter = adapter
                } else {
                    loadingDialog.hide()
                    imageView.visibility = View.VISIBLE
                    keepPlayingTxt.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                loadingDialog.hide()
                imageView.visibility = View.VISIBLE
                keepPlayingTxt.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                showCustomErrorToast(
                    this,
                    exception.message.toString(),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
    }


    private fun setViewsOnClickListeners() {
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun backPressedListener() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(
                    this@UserWithdrawHistoryActivity,
                    HomeActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        })
    }

    private fun textUI() {
        val textShader1: Shader = LinearGradient(
            0f, 12f, 0f, activityName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        activityName.paint.shader = textShader1
    }
}