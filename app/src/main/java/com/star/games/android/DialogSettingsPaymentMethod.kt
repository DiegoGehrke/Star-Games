package com.star.games.android

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast

class DialogSettingsPaymentMethod (
    private var dialogContext: Context,
    private val dbReference: DocumentReference
    )

{
    private lateinit var dialogBuilder: Dialog

    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var dialogSaveBtnText: TextView

    private lateinit var editText: EditText

    private lateinit var closeButton: ImageView
    private lateinit var saveButton: ImageView

    private lateinit var colorFilter : ColorMatrixColorFilter

     @SuppressLint("ClickableViewAccessibility")
     fun showSetPaymentDialog(
         dialogTitle: String,
         dialogMessage: String,
         editTextHint: String,
         isPix: Boolean
     ) {
        dialogBuilder = Dialog(dialogContext)
         dialogBuilder.setContentView(R.layout.settings_payment_method_dialog_layout)
         dialogBuilder.setCancelable(false)
         dialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

         saveButton = dialogBuilder.findViewById(R.id.dialog_save_button)
         saveButton.isEnabled = false
         dialogSaveBtnText = dialogBuilder.findViewById(R.id.dialog_save_btn_text)
         val matrix = floatArrayOf(
             0.33f, 0.33f, 0.33f, 0f, 0f,
             0.33f, 0.33f, 0.33f, 0f, 0f,
             0.33f, 0.33f, 0.33f, 0f, 0f,
             0f, 0f, 0f, 1f, 0f
         )
         val colorMatrix = ColorMatrix(matrix)
         colorFilter = ColorMatrixColorFilter(colorMatrix)
         //BLACK AND WHITE FILTER!
         saveButton.colorFilter = colorFilter

         title = dialogBuilder.findViewById(R.id.dialog_title)
         title.text = dialogTitle

         message = dialogBuilder.findViewById(R.id.dialog_message)
         message.text = dialogMessage
         editText = dialogBuilder.findViewById(R.id.dialog_edit_text)

         editText.hint = editTextHint
         editText.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 if (isPix) {
                     if (count >= 1) {
                         saveButton.colorFilter = null
                         saveButton.isEnabled = true
                         val textEnabledColor = Color.parseColor("#272D42")
                         dialogSaveBtnText.setTextColor(textEnabledColor)
                     } else {
                         saveButton.colorFilter = colorFilter
                         saveButton.isEnabled = false
                         val textDisabledColor = Color.parseColor("#656565")
                         dialogSaveBtnText.setTextColor(textDisabledColor)
                     }
                 } else {
                     val checkEmail = s.toString()
                     if (checkEmail.contains(".") && checkEmail.substring(checkEmail.indexOf(".") + 1).isNotEmpty() &&
                         checkEmail.contains("@") && checkEmail.substring(checkEmail.indexOf("@") + 1).isNotEmpty()) {

                         saveButton.colorFilter = null
                         saveButton.isEnabled = true
                         val textEnabledColor = Color.parseColor("#272D42")
                         dialogSaveBtnText.setTextColor(textEnabledColor)

                     } else {
                         saveButton.colorFilter = colorFilter
                         saveButton.isEnabled = false
                         val textDisabledColor = Color.parseColor("#656565")
                         dialogSaveBtnText.setTextColor(textDisabledColor)

                     }
                 }
             }

             override fun afterTextChanged(s: Editable?) {}

         })

         closeButton = dialogBuilder.findViewById(R.id.dialog_close_btn)

         closeButton.setOnClickListener {
             dialogBuilder.dismiss()
         }

         saveButton.setOnClickListener {
             val pixKey = editText.text.toString()
             val email = editText.text.toString()
             saveButton.colorFilter = colorFilter
             saveButton.isEnabled = false
             val textDisabledColor = Color.parseColor("#656565")
             dialogSaveBtnText.setTextColor(textDisabledColor)

             val data = if (isPix) {
                 hashMapOf("pixKey" to pixKey)
             } else {
                 hashMapOf("email" to email)
             }

             dbReference.get()
                 .addOnSuccessListener { documentSnapshot ->
                     val updateOrSet = if (documentSnapshot.exists()) {
                         dbReference.update(data as Map<String, Any>)
                     } else {
                         dbReference.set(data)
                             .addOnSuccessListener {

                             }
                     }

                     updateOrSet
                         .addOnSuccessListener {
                             val setUpdateMessage = if (isPix) {
                                 dialogContext.getString(R.string.key_pix_updated_successfully)
                             } else {
                                 dialogContext.getString(R.string.email_successfully_updated)
                             }
                             val setSetMessage = if (isPix) {
                                 dialogContext.getString(R.string.key_pix_defined_successfully)
                             } else {
                                 dialogContext.getString(R.string.email_defined_successfully)
                             }
                             showCustomSuccessToast(
                                 dialogContext,
                                 if (documentSnapshot.exists())
                                     setUpdateMessage
                                 else
                                     setSetMessage,
                                 Toast.LENGTH_LONG,
                                 dialogBuilder.window!!
                             )
                             dialogBuilder.dismiss()
                         }
                         .addOnFailureListener { exception ->
                             showCustomErrorToast(
                                 dialogContext,
                                 exception.message.toString(),
                                 Toast.LENGTH_LONG,
                                 dialogBuilder.window!!
                             )
                             dialogBuilder.dismiss()
                         }
                 }
                 .addOnFailureListener { exception ->
                     showCustomErrorToast(
                         dialogContext,
                         exception.message.toString(),
                         Toast.LENGTH_LONG,
                         dialogBuilder.window!!
                     )
                     dialogBuilder.dismiss()
                 }
         }
         dialogBuilder.show()
    }
}