package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat

class SupportActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var screenTitle: TextView
    private lateinit var parentLayout : ConstraintLayout
    private lateinit var parentLayout1 : ConstraintLayout
    private lateinit var parentLayout2 : ConstraintLayout
    private lateinit var parentLayout3 : ConstraintLayout
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        sharedPreferences = getSharedPreferences("getHelpRequest", MODE_PRIVATE)

        linearLayout = findViewById(R.id.linearLayout)
        screenTitle = findViewById(R.id.screen_title)
        val backBtn : ImageView = findViewById(R.id.back_btn)

        if (sharedPreferences.getString("helpType", "") == "support") {
           createSupportUI()
        }
        else if (sharedPreferences.getString("helpType", "") == "events") {
            createEventsSectionUI()
        }
        else if (sharedPreferences.getString("helpType", "") == "account") {
            createAccountSectionUI()
        }
        else if (sharedPreferences.getString("helpType", "") == "commonQuests") {
            createCommonQuestionsUI()
        }
        else {
            createSupportUI()
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, HelpSupportActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun createSupportUI() {
        val context : Context = this@SupportActivity
        screenTitle.text = "Support"
        val supportFirstTxt = TextView(context)
        val supportSecondTxt = TextView(context)
        val supportThirdTxt = TextView(context)
        val supportFourthTxt = TextView(context)
        val supportFifthTxt = TextView(context)
        val supportButton = ImageView(context)
        val supportTxt = TextView(context)

        supportFirstTxt.id = View.generateViewId()
        supportSecondTxt.id = View.generateViewId()
        supportThirdTxt.id = View.generateViewId()
        supportFourthTxt.id = View.generateViewId()
        supportFifthTxt.id = View.generateViewId()
        supportButton.id = View.generateViewId()
        supportTxt.id = View.generateViewId()

        supportButton.setImageResource(R.drawable.help_rectangle)
        supportFirstTxt.setTextColor(Color.WHITE)
        supportFirstTxt.textSize = 16f
        supportFirstTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        supportSecondTxt.setTextColor(Color.WHITE)
        supportSecondTxt.textSize = 16f
        supportSecondTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        supportThirdTxt.setTextColor(Color.WHITE)
        supportThirdTxt.textSize = 16f
        supportThirdTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        supportFourthTxt.setTextColor(Color.WHITE)
        supportFourthTxt.textSize = 16f
        supportFourthTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        supportFifthTxt.setTextColor(Color.WHITE)
        supportFifthTxt.textSize = 16f
        supportFifthTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        supportTxt.textSize = 20f
        supportTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        supportTxt.setTextColor(Color.WHITE)

        supportFirstTxt.text = "Esta é a seção de \"Suporte\" do nosso aplicativo, que tem como objetivo ajudá-lo em caso de quaisquer dúvidas ou problemas."
        supportSecondTxt.text = "Para entrar em contato conosco, basta clicar no botão \"Suporte\" logo abaixo. Você poderá nos enviar um e-mail para que nossa equipe possa ajudá-lo da melhor forma possível."
        supportThirdTxt.text = "Nossa equipe de suporte está disponível para ajudá-lo a qualquer hora do dia ou da noite, e faremos o possível para responder rapidamente e com soluções efetivas para seus problemas."
        supportFourthTxt.text = "Além disso, em nossa seção de \"Perguntas frequentes\", você encontrará respostas para as perguntas mais comuns que recebemos dos nossos usuários. Recomendamos que você dê uma olhada nesta seção antes de entrar em contato conosco, pois sua dúvida pode já ter sido respondida lá."
        supportFifthTxt.text = "Agradecemos a confiança que você deposita em nosso aplicativo e estamos sempre trabalhando para melhorar sua experiência. Se precisar de ajuda, não hesite em nos contatar através da seção de \"Suporte\"."
        supportTxt.text = "SUPPORT"
        supportButton.setOnClickListener {
            val intent = Intent(this, SendEmailToSupportActivity::class.java)
            startActivity(intent)
        }

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)
        val parentLayout1 = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(supportFirstTxt)
        parentLayout.addView(supportSecondTxt)
        parentLayout.addView(supportThirdTxt)
        parentLayout.addView(supportFourthTxt)
        parentLayout.addView(supportFifthTxt)
        parentLayout1.addView(supportButton)
        parentLayout.addView(supportTxt)

        val setBackground = ConstraintSet()
        setBackground.constrainWidth(supportButton.id, ConstraintSet.MATCH_CONSTRAINT)
        setBackground.constrainHeight(supportButton.id, 148)
        setBackground.connect(supportButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        setBackground.connect(supportButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        setBackground.connect(supportButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 48)
        setBackground.applyTo(parentLayout1)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(supportFirstTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportFirstTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportFirstTxt.id, ConstraintSet.TOP, linearLayout.id, ConstraintSet.BOTTOM, 24)
        constraintSet.connect(supportFirstTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(supportFirstTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(supportSecondTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportSecondTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportSecondTxt.id, ConstraintSet.TOP, supportFirstTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(supportSecondTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(supportSecondTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(supportThirdTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportThirdTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportThirdTxt.id, ConstraintSet.TOP, supportSecondTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(supportThirdTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(supportThirdTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(supportFourthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportFourthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportFourthTxt.id, ConstraintSet.TOP, supportThirdTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(supportFourthTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(supportFourthTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(supportFifthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportFifthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportFifthTxt.id, ConstraintSet.TOP, supportFourthTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(supportFifthTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(supportFifthTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(supportTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(supportTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(supportTxt.id, ConstraintSet.START, supportButton.id, ConstraintSet.START)
        constraintSet.connect(supportTxt.id, ConstraintSet.END, supportButton.id, ConstraintSet.END)
        constraintSet.connect(supportTxt.id, ConstraintSet.BOTTOM, supportButton.id, ConstraintSet.BOTTOM)
        constraintSet.connect(supportTxt.id, ConstraintSet.TOP, supportButton.id, ConstraintSet.TOP)

        constraintSet.applyTo(parentLayout)
    }

    private fun createEventsSectionUI() {
        val contextEvent : Context = this@SupportActivity
        screenTitle.text = "Eventos"
        val eventsFirstTxt = TextView(contextEvent)
        val eventsSecondTxt = TextView(contextEvent)
        val eventsThirdTxt = TextView(contextEvent)
        val eventsFourthTxt = TextView(contextEvent)
        val eventsFifthTxt = TextView(contextEvent)

        eventsFirstTxt.id = View.generateViewId()
        eventsSecondTxt.id = View.generateViewId()
        eventsThirdTxt.id = View.generateViewId()
        eventsFourthTxt.id = View.generateViewId()
        eventsFifthTxt.id = View.generateViewId()

        eventsFirstTxt.setTextColor(Color.WHITE)
        eventsFirstTxt.textSize = 16f
        eventsFirstTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        eventsSecondTxt.setTextColor(Color.WHITE)
        eventsSecondTxt.textSize = 16f
        eventsSecondTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        eventsThirdTxt.setTextColor(Color.WHITE)
        eventsThirdTxt.textSize = 16f
        eventsThirdTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        eventsFourthTxt.setTextColor(Color.WHITE)
        eventsFourthTxt.textSize = 16f
        eventsFourthTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        eventsFifthTxt.setTextColor(Color.WHITE)
        eventsFifthTxt.textSize = 16f
        eventsFifthTxt.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)

        eventsFirstTxt.text = "Essa é a seção de \"Eventos\" na nossa Central de Ajuda! Aqui, você encontrará informações sobre os eventos que ocorrem no nosso aplicativo."
        eventsSecondTxt.text = "Nossos eventos são divertidos e fáceis de participar. Eles variam desde jogos simples até desafios mais complexos, mas todos eles oferecem a chance de ganhar prêmios incríveis! Você pode receber Estrelas, pontos VIP e outros itens de graça."
        eventsThirdTxt.text = "Para participar, basta escolher um evento na aba vertical que fica no lado direito da tela principal do aplicativo. Certifique-se de verificar as regras e os requisitos de cada evento para garantir sua participação e premiação. Não se preocupe, tudo é explicado de forma clara e fácil de entender."
        eventsFourthTxt.text = "É importante destacar que os eventos são redefinidos todos os dias às 21:00 BRT. Isso significa que você terá uma nova oportunidade de participar e ganhar prêmios a cada dia! Então, não perca a chance de participar e aumentar suas chances de ganhar."
        eventsFifthTxt.text = "Agradecemos por fazer parte da nossa comunidade e estamos animados para ver você participando dos nossos eventos diários. Boa sorte e divirta-se!"

        parentLayout = findViewById(R.id.background)

        parentLayout.addView(eventsFirstTxt)
        parentLayout.addView(eventsSecondTxt)
        parentLayout.addView(eventsThirdTxt)
        parentLayout.addView(eventsFourthTxt)
        parentLayout.addView(eventsFifthTxt)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(eventsFirstTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(eventsFirstTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(eventsFirstTxt.id, ConstraintSet.TOP, linearLayout.id, ConstraintSet.BOTTOM, 24)
        constraintSet.connect(eventsFirstTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(eventsFirstTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(eventsSecondTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(eventsSecondTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(eventsSecondTxt.id, ConstraintSet.TOP, eventsFirstTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(eventsSecondTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(eventsSecondTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(eventsThirdTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(eventsThirdTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(eventsThirdTxt.id, ConstraintSet.TOP, eventsSecondTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(eventsThirdTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(eventsThirdTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(eventsFourthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(eventsFourthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(eventsFourthTxt.id, ConstraintSet.TOP, eventsThirdTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(eventsFourthTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(eventsFourthTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.constrainWidth(eventsFifthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(eventsFifthTxt.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(eventsFifthTxt.id, ConstraintSet.TOP, eventsFourthTxt.id, ConstraintSet.BOTTOM, 8)
        constraintSet.connect(eventsFifthTxt.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)
        constraintSet.connect(eventsFifthTxt.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)

        constraintSet.applyTo(parentLayout)
    }

    private fun createAccountSectionUI() {
        val accountHelpContext : Context = this@SupportActivity
        screenTitle.text = "Conta"
        val changePassOptionText = TextView(accountHelpContext)
        val backgroundImage = ImageView(accountHelpContext)
        val btnGoToHelpChangePass = ImageView(accountHelpContext)

        changePassOptionText.id = View.generateViewId()
        backgroundImage.id = View.generateViewId()
        btnGoToHelpChangePass.id = View.generateViewId()

        changePassOptionText.setTextColor(Color.WHITE)
        changePassOptionText.textSize = 16f
        changePassOptionText.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
        backgroundImage.setImageResource(R.drawable.help_rectangle)
        btnGoToHelpChangePass.setImageResource(R.drawable.back_btn)
        btnGoToHelpChangePass.setOnClickListener {
            createHowToChangePassUI()
        }

        changePassOptionText.text = "Como mudar minha senha"

        parentLayout = findViewById(R.id.background)
        parentLayout1 = findViewById(R.id.background)

        parentLayout.addView(backgroundImage)
        parentLayout1.addView(changePassOptionText)
        parentLayout1.addView(btnGoToHelpChangePass)

        val setBackG = ConstraintSet()
        setBackG.constrainWidth(backgroundImage.id, ConstraintSet.MATCH_CONSTRAINT)
        setBackG.constrainHeight(backgroundImage.id, 128)
        setBackG.connect(backgroundImage.id, ConstraintSet.TOP, linearLayout.id, ConstraintSet.BOTTOM, 24)
        setBackG.connect(backgroundImage.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        setBackG.connect(backgroundImage.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        setBackG.applyTo(parentLayout)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(btnGoToHelpChangePass.id, 48)
        constraintSet.constrainHeight(btnGoToHelpChangePass.id, 48)
        constraintSet.connect(btnGoToHelpChangePass.id, ConstraintSet.TOP, backgroundImage.id, ConstraintSet.TOP)
        constraintSet.connect(btnGoToHelpChangePass.id, ConstraintSet.END, backgroundImage.id, ConstraintSet.END, 24)
        constraintSet.connect(btnGoToHelpChangePass.id, ConstraintSet.BOTTOM, backgroundImage.id, ConstraintSet.BOTTOM)

        constraintSet.constrainWidth(changePassOptionText.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(changePassOptionText.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(changePassOptionText.id, ConstraintSet.TOP, backgroundImage.id, ConstraintSet.TOP)
        constraintSet.connect(changePassOptionText.id, ConstraintSet.START, backgroundImage.id, ConstraintSet.START, 24)
        constraintSet.connect(changePassOptionText.id, ConstraintSet.BOTTOM, backgroundImage.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(parentLayout1)
    }

    private fun createHowToChangePassUI() {
        TODO("Not yet implemented")
    }

    private fun createCommonQuestionsUI() {
            val commonQuestionContext : Context = this@SupportActivity
            screenTitle.text = "Common Questions"
            val firstCQFrameTitle = TextView(commonQuestionContext)
            val firstCQFrame = ImageView(commonQuestionContext)
            val firstCQButton = ImageView(commonQuestionContext)
            val secondCQFrameTitle = TextView(commonQuestionContext)
            val secondCQFrame = ImageView(commonQuestionContext)
            val secondCQButton = ImageView(commonQuestionContext)
            val thirdCQFrameTitle = TextView(commonQuestionContext)
            val thirdCQFrame = ImageView(commonQuestionContext)
            val thirdCQButton = ImageView(commonQuestionContext)

            firstCQFrameTitle.id = View.generateViewId()
            firstCQFrame.id = View.generateViewId()
            firstCQButton.id = View.generateViewId()
            secondCQFrameTitle.id = View.generateViewId()
            secondCQFrame.id = View.generateViewId()
            secondCQButton.id = View.generateViewId()
            thirdCQFrameTitle.id = View.generateViewId()
            thirdCQFrame.id = View.generateViewId()
            thirdCQButton.id = View.generateViewId()

            firstCQFrameTitle.setTextColor(Color.WHITE)
            secondCQFrameTitle.setTextColor(Color.WHITE)
            thirdCQFrameTitle.setTextColor(Color.WHITE)
            firstCQFrameTitle.textSize = 16f
            secondCQFrameTitle.textSize = 16f
            thirdCQFrameTitle.textSize = 16f
            firstCQFrameTitle.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
            secondCQFrameTitle.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
            thirdCQFrameTitle.typeface = ResourcesCompat.getFont(this, R.font.inter_medium)
            firstCQFrame.setImageResource(R.drawable.help_rectangle)
            secondCQFrame.setImageResource(R.drawable.help_rectangle)
            thirdCQFrame.setImageResource(R.drawable.help_rectangle)
            firstCQButton.setImageResource(R.drawable.back_btn)
            secondCQButton.setImageResource(R.drawable.back_btn)
            thirdCQButton.setImageResource(R.drawable.back_btn)
            firstCQButton.setOnClickListener {
                createHowToChangePassUI()
            }
            secondCQButton.setOnClickListener {

            }
            thirdCQButton.setOnClickListener {

            }

            firstCQFrameTitle.text = "Como solicitar meu dinheiro"
            secondCQFrameTitle.text = "Como convidar um amigo"
            thirdCQFrameTitle.text = "Como mudar minha senha"

            parentLayout = findViewById(R.id.background)
            parentLayout1 = findViewById(R.id.background)
            parentLayout2 = findViewById(R.id.background)
            parentLayout3 = findViewById(R.id.background)

            parentLayout.addView(firstCQFrame)
            parentLayout.addView(secondCQFrame)
            parentLayout.addView(thirdCQFrame)
            parentLayout1.addView(firstCQFrameTitle)
            parentLayout1.addView(secondCQFrameTitle)
            parentLayout1.addView(thirdCQFrameTitle)
            parentLayout1.addView(firstCQButton)
            parentLayout1.addView(secondCQButton)
            parentLayout1.addView(thirdCQButton)

            val setBackG = ConstraintSet()
            setBackG.constrainWidth(firstCQFrame.id, ConstraintSet.MATCH_CONSTRAINT)
            setBackG.constrainHeight(firstCQFrame.id, 128)
            setBackG.connect(firstCQFrame.id, ConstraintSet.TOP, linearLayout.id, ConstraintSet.BOTTOM, 24)
            setBackG.connect(firstCQFrame.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            setBackG.connect(firstCQFrame.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            setBackG.constrainWidth(secondCQFrame.id, ConstraintSet.MATCH_CONSTRAINT)
            setBackG.constrainHeight(secondCQFrame.id, 128)
            setBackG.connect(secondCQFrame.id, ConstraintSet.TOP, firstCQFrame.id, ConstraintSet.BOTTOM, 24)
            setBackG.connect(secondCQFrame.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            setBackG.connect(secondCQFrame.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            setBackG.constrainWidth(thirdCQFrame.id, ConstraintSet.MATCH_CONSTRAINT)
            setBackG.constrainHeight(thirdCQFrame.id, 128)
            setBackG.connect(thirdCQFrame.id, ConstraintSet.TOP, secondCQFrame.id, ConstraintSet.BOTTOM, 24)
            setBackG.connect(thirdCQFrame.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            setBackG.connect(thirdCQFrame.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            setBackG.applyTo(parentLayout)

            val constraintSet = ConstraintSet()
            constraintSet.constrainWidth(firstCQButton.id, 48)
            constraintSet.constrainHeight(firstCQButton.id, 48)
            constraintSet.connect(firstCQButton.id, ConstraintSet.TOP, firstCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(firstCQButton.id, ConstraintSet.END, firstCQFrame.id, ConstraintSet.END, 24)
            constraintSet.connect(firstCQButton.id, ConstraintSet.BOTTOM, firstCQFrame.id, ConstraintSet.BOTTOM)

            constraintSet.constrainWidth(firstCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.constrainHeight(firstCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(firstCQFrameTitle.id, ConstraintSet.TOP, firstCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(firstCQFrameTitle.id, ConstraintSet.START, firstCQFrame.id, ConstraintSet.START, 24)
            constraintSet.connect(firstCQFrameTitle.id, ConstraintSet.BOTTOM, firstCQFrame.id, ConstraintSet.BOTTOM)

            constraintSet.constrainWidth(secondCQButton.id, 48)
            constraintSet.constrainHeight(secondCQButton.id, 48)
            constraintSet.connect(secondCQButton.id, ConstraintSet.TOP, secondCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(secondCQButton.id, ConstraintSet.END, secondCQFrame.id, ConstraintSet.END, 24)
            constraintSet.connect(secondCQButton.id, ConstraintSet.BOTTOM, secondCQFrame.id, ConstraintSet.BOTTOM)

            constraintSet.constrainWidth(secondCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.constrainHeight(secondCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(secondCQFrameTitle.id, ConstraintSet.TOP, secondCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(secondCQFrameTitle.id, ConstraintSet.START, secondCQFrame.id, ConstraintSet.START, 24)
            constraintSet.connect(secondCQFrameTitle.id, ConstraintSet.BOTTOM, secondCQFrame.id, ConstraintSet.BOTTOM)

            constraintSet.constrainWidth(thirdCQButton.id, 48)
            constraintSet.constrainHeight(thirdCQButton.id, 48)
            constraintSet.connect(thirdCQButton.id, ConstraintSet.TOP, thirdCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(thirdCQButton.id, ConstraintSet.END, thirdCQFrame.id, ConstraintSet.END, 24)
            constraintSet.connect(thirdCQButton.id, ConstraintSet.BOTTOM, thirdCQFrame.id, ConstraintSet.BOTTOM)

            constraintSet.constrainWidth(thirdCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.constrainHeight(thirdCQFrameTitle.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(thirdCQFrameTitle.id, ConstraintSet.TOP, thirdCQFrame.id, ConstraintSet.TOP)
            constraintSet.connect(thirdCQFrameTitle.id, ConstraintSet.START, thirdCQFrame.id, ConstraintSet.START, 24)
            constraintSet.connect(thirdCQFrameTitle.id, ConstraintSet.BOTTOM, thirdCQFrame.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(parentLayout1)
        }
}