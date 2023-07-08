package com.star.games.android

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
import com.google.firebase.auth.FirebaseAuth
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast

class ResetPasswordDialog(private var context : Context) {
    private lateinit var resetPassDialog: Dialog
    private lateinit var sendLinkToResetUserPassBtn : ImageView
    private lateinit var emailEditText : EditText
    private lateinit var closeDialogBtn : ImageView
    private lateinit var dialogSaveBtnText : TextView

    private lateinit var colorFilter : ColorMatrixColorFilter

    val auth = FirebaseAuth.getInstance()

    fun showResetPassDialog() {
        resetPassDialog = Dialog(context)
        resetPassDialog.setContentView(R.layout.reset_password_dialog_layout)
        resetPassDialog.setCancelable(false)
        resetPassDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        emailEditText = resetPassDialog.findViewById(R.id.editText2)
        sendLinkToResetUserPassBtn = resetPassDialog.findViewById(R.id.dialog_save_button)
        dialogSaveBtnText = resetPassDialog.findViewById(R.id.dialog_save_btn_text)
        sendLinkToResetUserPassBtn.isEnabled = false
        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val colorMatrix = ColorMatrix(matrix)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
        //BLACK AND WHITE FILTER!
        sendLinkToResetUserPassBtn.colorFilter = colorFilter
        sendLinkToResetUserPassBtn.setOnClickListener {
            auth.sendPasswordResetEmail(emailEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showCustomSuccessToast(
                            context,
                            "Email de redefinição de " +
                                    "senha enviado com sucesso!",
                            Toast.LENGTH_LONG,
                            resetPassDialog.window!!
                        )
                        resetPassDialog.dismiss()
                    } else {
                        showCustomErrorToast(
                            context,
                            "Falha ao enviar o email " +
                                    "de redefinição de senha.",
                            Toast.LENGTH_LONG,
                            resetPassDialog.window!!
                        )
                        resetPassDialog.dismiss()
                    }
                }
        }

        closeDialogBtn = resetPassDialog.findViewById(R.id.close_btn)
        closeDialogBtn.setOnClickListener {
            resetPassDialog.dismiss()
        }

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val checkEmail = s.toString()
                if (checkEmail.contains(".") && checkEmail.substring(checkEmail.indexOf(".") + 1).isNotEmpty() &&
                    checkEmail.contains("@") && checkEmail.substring(checkEmail.indexOf("@") + 1).isNotEmpty()) {

                    sendLinkToResetUserPassBtn.colorFilter = null
                    sendLinkToResetUserPassBtn.isEnabled = true
                    val textEnabledColor = Color.parseColor("#272D42")
                    dialogSaveBtnText.setTextColor(textEnabledColor)

                } else {
                    sendLinkToResetUserPassBtn.colorFilter = colorFilter
                    sendLinkToResetUserPassBtn.isEnabled = false
                    val textEnabledColor = Color.parseColor("#272D42")
                    dialogSaveBtnText.setTextColor(textEnabledColor)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        resetPassDialog.show()
    }
}