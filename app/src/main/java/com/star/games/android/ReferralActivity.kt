package com.star.games.android

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ReferralActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser
    private var userData = db.collection("USERS").document(currentUser.uid)
    private var friendCodeDb = db.collection("USERS").document("codigoDoAmigo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser!!
        val userCodeTxt : TextView = findViewById(R.id.user_code)


        val backBtn : ImageView = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val codeEditText : EditText = findViewById(R.id.editText)
        val addReff : LinearLayout = findViewById(R.id.add_referral_btn)
        val useCoins : LinearLayout = findViewById(R.id.claim_btn)
        useCoins.setOnClickListener {
            val spendedCoins = 50
            addCoins(spendedCoins)
        }
       /* addReff.setOnClickListener {
            // Obter o código de referência inserido pelo usuário
            val referralCode = codeEditText.text.toString()

            // Verificar se o código é diferente do código do usuário atual
            if (referralCode != userReferralCode) {
                // Adicionar o ponto no documento do Firestore do usuário indicado
                addPointToReferral(referralCode)
            } else {
                // Mostrar mensagem de erro ao usuário
                Toast.makeText(this, "Você não pode usar o seu próprio código de referência", Toast.LENGTH_SHORT).show()
            }
        }*/

    }

    private fun addCoins(coins: Int) {
            userData.update("coins", coins).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Envia 20% dos coins gastos para a conta do amigo que convidou o usuário
                    val friendCode = intent.getStringExtra("codigo_do_amigo")
                    if (friendCode != null) {
                        friendCodeDb.get().addOnSuccessListener { document ->
                            val coinsDoAmigo = document.getLong("coins")?.toInt() ?: 0
                            val coinsParaAmigo = (coins * 0.2).toInt()
                            friendCodeDb.update("coins", (coinsDoAmigo + coinsParaAmigo).toLong())
                        }
                    }
                }
            }
    }

    // Obter o código de referência do usuário a partir do seu UID
    private fun getUserReferralCode() {

    }

    // Adicionar um ponto no documento do Firestore do usuário indicado
    private fun addPointToReferral(referralCode: String) {
        // Obter a referência do documento do usuário indicado
        val referralDocRef = db.collection("USERS").whereEqualTo("referral", referralCode).get()

        referralDocRef.addOnSuccessListener { documents ->
            if (documents.documents.isNotEmpty()) {
                // Obter o documento do usuário indicado
                val referralDoc = documents.documents[0]

                // Obter o número de pontos do usuário indicado
                var points = referralDoc.getLong("points") ?: 0

                // Adicionar um ponto
                points++

                // Atualizar o número de pontos no documento do usuário indicado
                referralDoc.reference.update("points", points)
                    .addOnSuccessListener {
                        // Mostrar mensagem de sucesso ao usuário
                        Toast.makeText(this, "1 ponto adicionado com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Mostrar mensagem de erro ao usuário
                        Toast.makeText(this, "Erro ao adicionar ponto: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Mostrar mensagem de erro ao usuário
                Toast.makeText(this, "Código de referência inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}