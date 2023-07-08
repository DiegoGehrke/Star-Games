package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class SupportActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout

    private lateinit var screenTitle: TextView

    private lateinit var parentLayout : ConstraintLayout

    private lateinit var backBtn: ImageView

    private lateinit var sharedPreferences : SharedPreferences

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        sharedPreferences = getSharedPreferences("getHelpRequest", MODE_PRIVATE)

        linearLayout = findViewById(R.id.linearLayout)
        screenTitle = findViewById(R.id.screen_title)
        backBtn = findViewById(R.id.back_btn)
        recyclerView = findViewById(R.id.recycler_view)

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
        screenTitle.text = getString(R.string.support_mini)
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

        supportFirstTxt.text = getString(R.string.this_is_the_support_section_of_our_application_which_aims_to_help_you_in_case_of_any_questions_or_problems)
        supportSecondTxt.text = getString(R.string.to_contact_us_just_click_on_the_support_button_below_you_can_send_us_an_email_so_that_our_team_can_help_you_in_the_best_way_possible)
        supportThirdTxt.text = getString(R.string.our_support_team_is_available_to_help_you_anytime_day_or_night_and_we_will_do_our_best_to_respond_quickly_and_with_effective_solutions_to_your_issues)
        supportFourthTxt.text = getString(R.string.also_in_our_frequently_asked_questions_section_you_ll_find_answers_to_the_most_common_questions_we_receive_from_our_users_we_recommend_that_you_take_a_look_at_this_section_before_contacting_us_as_your_question_may_already_be_answered_there)
        supportFifthTxt.text = getString(R.string.we_appreciate_the_trust_you_place_in_our_app_and_we_are_always_working_to_improve_your_experience_if_you_need_help_don_t_hesitate_to_contact_us_through_the_support_section)
        supportTxt.text = getString(R.string.support)
        supportButton.setOnClickListener {
            val intent = Intent(this, SendEmailToSupportActivity::class.java)
            startActivity(intent)
        }

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(supportFirstTxt)
        parentLayout.addView(supportSecondTxt)
        parentLayout.addView(supportThirdTxt)
        parentLayout.addView(supportFourthTxt)
        parentLayout.addView(supportFifthTxt)
        parentLayout.addView(supportButton)
        parentLayout.addView(supportTxt)

        val setBackground = ConstraintSet()
        setBackground.constrainWidth(supportButton.id, ConstraintSet.MATCH_CONSTRAINT)
        setBackground.constrainHeight(supportButton.id, 148)
        setBackground.connect(supportButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        setBackground.connect(supportButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        setBackground.connect(supportButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 48)
        setBackground.applyTo(parentLayout)

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
        screenTitle.text = getString(R.string.events)
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

        eventsFirstTxt.text = getString(R.string.this_is_the_events_section_of_our_help_center_here_you_will_find_information_about_the_events_that_take_place_in_our_application)
        eventsSecondTxt.text = getString(R.string.our_events_are_fun_and_easy_to_attend_they_range_from_simple_games_to_more_complex_challenges_but_they_all_offer_the_chance_to_win_amazing_prizes_you_can_receive_stars_vip_points_and_other_items_for_free)
        eventsThirdTxt.text = getString(R.string.to_participate_simply_choose_an_event_in_the_vertical_tab_on_the_right_side_of_the_application_s_main_screen_be_sure_to_check_each_event_s_rules_and_requirements_to_ensure_your_participation_and_prize_pool_don_t_worry_everything_is_explained_clearly_and_easy_to_understand)
        eventsFourthTxt.text = getString(R.string.it_is_important_to_note_that_the_events_are_reset_every_day_at_21_00_et_that_means_you_ll_have_a_new_opportunity_to_participate_and_win_prizes_every_day_so_don_t_miss_the_chance_to_participate_and_increase_your_chances_of_winning)
        eventsFifthTxt.text = getString(R.string.we_appreciate_being_part_of_our_community_and_we_are_excited_to_see_you_participate_in_our_daily_events_good_luck_and_have_fun)

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
        screenTitle.text = getString(R.string.account)
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

        }

        changePassOptionText.text = getString(R.string.how_to_change_your_password)

        parentLayout = findViewById(R.id.background)

        parentLayout.addView(backgroundImage)
        parentLayout.addView(changePassOptionText)
        parentLayout.addView(btnGoToHelpChangePass)

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

        constraintSet.applyTo(parentLayout)
    }

    private fun createCommonQuestionsUI() {
            val commonQuestionContext : Context = this@SupportActivity
            screenTitle.text = getString(R.string.common_questions)
            var boolean: Boolean
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
                boolean = true
                screenTitle.text = getString(R.string.how_to_change_your_password)
                screenTitle.textSize = 16F
                firstCQFrame.visibility = View.GONE
                firstCQFrameTitle.visibility = View.GONE
                firstCQButton.visibility = View.GONE
                secondCQFrame.visibility = View.GONE
                secondCQFrameTitle.visibility = View.GONE
                secondCQButton.visibility = View.GONE
                thirdCQFrame.visibility = View.GONE
                thirdCQFrameTitle.visibility = View.GONE
                thirdCQButton.visibility = View.GONE

                val sectionText = TextView(commonQuestionContext)
                val buttonGoToChangePassScreen = ImageView(commonQuestionContext)
                val buttonGoToChangePassScreenTxt = TextView(commonQuestionContext)

                sectionText.id = View.generateViewId()
                buttonGoToChangePassScreen.id = View.generateViewId()
                buttonGoToChangePassScreenTxt.id = View.generateViewId()

                sectionText.textSize = 16F
                sectionText.typeface = ResourcesCompat.getFont(
                    this,
                    R.font.inter_medium
                )
                sectionText.setTextColor(Color.WHITE)

                buttonGoToChangePassScreenTxt.textSize = 20F
                buttonGoToChangePassScreenTxt.typeface = ResourcesCompat.getFont(
                    this,
                    R.font.inter_bold
                )
                buttonGoToChangePassScreenTxt.setTextColor(Color.WHITE)

                val sectionTextSB = StringBuilder()
                sectionText.append(getString(R.string.to_change_your_password_you_can_follow_these_steps))
                sectionText.append(getString(R.string._1_starting_from_the_main_screen_click_on_the_gear_symbol))
                sectionText.append(getString(R.string._2_click_on_the_manage_account_option))
                sectionText.append(getString(R.string._3_click_on_the_change_password_button))
                sectionText.append(getString(R.string._4_and_then_just_provide_what_the_dialog_asks_for))
                sectionText.append(getString(R.string.we_ll_leave_a_shortcut_below_we_hope_that_s_enough_if_you_need_help_you_can_contact_us_in_the_help_and_support_section))

                val finalText = sectionTextSB.toString()
                sectionText.text = finalText

                buttonGoToChangePassScreen.setImageResource(R.drawable.help_rectangle)
                buttonGoToChangePassScreenTxt.text = getString(R.string.go_to_change_password)

                parentLayout = findViewById(R.id.background)

                parentLayout.addView(sectionText)
                parentLayout.addView(buttonGoToChangePassScreen)
                parentLayout.addView(buttonGoToChangePassScreenTxt)

                val addViewsToLayout = ConstraintSet()
                addViewsToLayout.constrainWidth(sectionText.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.constrainHeight(sectionText.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.connect(
                    sectionText.id,
                    ConstraintSet.TOP,
                    linearLayout.id,
                    ConstraintSet.BOTTOM,
                    24
                )
                addViewsToLayout.connect(
                    sectionText.id,
                    ConstraintSet.START,
                    parentLayout.id,
                    ConstraintSet.START
                )
                addViewsToLayout.connect(
                    sectionText.id,
                    ConstraintSet.END,
                    parentLayout.id,
                    ConstraintSet.END
                )

                addViewsToLayout.constrainWidth(buttonGoToChangePassScreen.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.constrainHeight(buttonGoToChangePassScreen.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.connect(
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.BOTTOM,
                    parentLayout.id,
                    ConstraintSet.BOTTOM,
                    48
                )
                addViewsToLayout.connect(
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.START,
                    parentLayout.id,
                    ConstraintSet.START
                )
                addViewsToLayout.connect(
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.END,
                    parentLayout.id,
                    ConstraintSet.END
                )

                addViewsToLayout.constrainWidth(buttonGoToChangePassScreenTxt.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.constrainHeight(buttonGoToChangePassScreenTxt.id, ConstraintSet.WRAP_CONTENT)
                addViewsToLayout.connect(
                    buttonGoToChangePassScreenTxt.id,
                    ConstraintSet.BOTTOM,
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.BOTTOM
                )
                addViewsToLayout.connect(
                    buttonGoToChangePassScreenTxt.id,
                    ConstraintSet.START,
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.START
                )
                addViewsToLayout.connect(
                    buttonGoToChangePassScreenTxt.id,
                    ConstraintSet.END,
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.END
                )
                addViewsToLayout.connect(
                    buttonGoToChangePassScreenTxt.id,
                    ConstraintSet.TOP,
                    buttonGoToChangePassScreen.id,
                    ConstraintSet.TOP
                )
                addViewsToLayout.applyTo(parentLayout)

                backBtn.setOnClickListener {
                    if (boolean) {
                        boolean = false
                        parentLayout.removeView(sectionText)
                        screenTitle.text = getString(R.string.common_questions)
                        screenTitle.textSize = 20F
                        firstCQFrame.visibility = View.VISIBLE
                        firstCQFrameTitle.visibility = View.VISIBLE
                        firstCQButton.visibility = View.VISIBLE
                        secondCQFrame.visibility = View.VISIBLE
                        secondCQFrameTitle.visibility = View.VISIBLE
                        secondCQButton.visibility = View.VISIBLE
                        thirdCQFrame.visibility = View.VISIBLE
                        thirdCQFrameTitle.visibility = View.VISIBLE
                        thirdCQButton.visibility = View.VISIBLE
                    } else {
                        val intent = Intent(this, HelpSupportActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            secondCQButton.setOnClickListener {

            }
            thirdCQButton.setOnClickListener {

            }

            firstCQFrameTitle.text = getString(R.string.how_to_exchange_stars_to_money)
            secondCQFrameTitle.text = getString(R.string.how_to_invite_an_friend)
            thirdCQFrameTitle.text = getString(R.string.how_long_does_it_take_for_the_money_to_arrive_in_my_bank_account)

            parentLayout = findViewById(R.id.background)

            parentLayout.addView(firstCQFrame)
            parentLayout.addView(secondCQFrame)
            parentLayout.addView(thirdCQFrame)
            parentLayout.addView(firstCQFrameTitle)
            parentLayout.addView(secondCQFrameTitle)
            parentLayout.addView(thirdCQFrameTitle)
            parentLayout.addView(firstCQButton)
            parentLayout.addView(secondCQButton)
            parentLayout.addView(thirdCQButton)

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
            constraintSet.applyTo(parentLayout)
        }
}