package com.star.games.android

import android.content.Context

class QuizQuestions(val context: Context) {
        fun getQuestions(): List<Question> {
            val questions: MutableList<Question> = mutableListOf()

            val question1 = Question(
                context.getString(R.string.what_is_the_capital_of_france),
                listOf(
                    context.getString(R.string.paris),
                    context.getString(R.string.london),
                    context.getString(R.string.rome),
                    context.getString(R.string.madrid)
                ).shuffled(),
                context.getString(R.string.paris)
            )
            questions.add(question1)

            val question2 = Question(
                context.getString(R.string.who_painted_the_mona_lisa),
                listOf(
                    context.getString(R.string.vincent_van_gogh), 
                    context.getString(R.string.leonardo_da_vinci), 
                    context.getString(R.string.pablo_picasso), 
                    context.getString(R.string.michelangelo)
                ).shuffled(),
                context.getString(R.string.leonardo_da_vinci)
            )
            questions.add(question2)

            val question3 = Question(
                context.getString(R.string.what_is_the_biggest_planet_in_the_solar_system),
                listOf(
                    context.getString(R.string.venus), 
                    context.getString(R.string.mars), 
                    context.getString(R.string.jupiter), 
                    context.getString(R.string.saturn)
                ).shuffled(),
                context.getString(R.string.jupiter)
            )
            questions.add(question3)

            val question4 = Question(
                context.getString(R.string.what_is_the_primary_color),
                listOf(
                    context.getString(R.string.white),
                    context.getString(R.string.green),
                    context.getString(R.string.black),
                    context.getString(R.string.red)
                ).shuffled(),
                context.getString(R.string.red)
            )
            questions.add(question4)

            val question5 = Question(
                context.getString(R.string.what_is_the_biggest_ocean_in_the_world),
                listOf(
                    context.getString(R.string.pacific),
                    context.getString(R.string.arctic),
                    context.getString(R.string.atlantic),
                    context.getString(R.string.indian)
                ).shuffled(),
                context.getString(R.string.pacific)
            )
            questions.add(question5)

            val question6 = Question(
                context.getString(R.string.who_wrote_the_harry_potter_series_of_books),
                listOf(
                    context.getString(R.string.roald_dahl),
                    context.getString(R.string.j_k_rowling),
                    context.getString(R.string.rick_riordan),
                    context.getString(R.string.suzanne_collins)
                ).shuffled(),
                context.getString(R.string.j_k_rowling)
            )
            questions.add(question6)

            val question7 = Question(
                context.getString(R.string.what_is_the_country_with_the_largest_population_in_the_world),
                listOf(
                    context.getString(R.string.india),
                    context.getString(R.string.brazil),
                    context.getString(R.string.china),
                    context.getString(R.string.united_states)
                ).shuffled(),
                context.getString(R.string.india)
            )
            questions.add(question7)

            val question8 = Question(
                context.getString(R.string.what_is_the_fastest_land_animal),
                listOf(
                    context.getString(R.string.leopard),
                    context.getString(R.string.tiger),
                    context.getString(R.string.horse),
                    context.getString(R.string.cheetah)
                ).shuffled(),
                context.getString(R.string.cheetah)
            )
            questions.add(question8)

            val question9 = Question(
                context.getString(R.string.what_is_the_chemical_formula_of_water),
                listOf(
                    context.getString(R.string.h2o),
                    context.getString(R.string.co2),
                    context.getString(R.string.nacl),
                    context.getString(R.string.o2)
                ).shuffled(),
                context.getString(R.string.h2o)
            )
            questions.add(question9)

            val question10 = Question(
                context.getString(R.string.who_was_the_first_man_to_step_on_the_moon),
                listOf(
                    context.getString(R.string.buzz_aldrin),
                    context.getString(R.string.neil_armstrong),
                    context.getString(R.string.alan_shepard),
                    context.getString(R.string.yuri_gagarin)
                ).shuffled(),
                context.getString(R.string.neil_armstrong)
            )
            questions.add(question10)

            val question11 = Question(
                context.getString(R.string.what_is_the_capital_of_australia),
                listOf(
                    context.getString(R.string.melbourne),
                    context.getString(R.string.sydney),
                    context.getString(R.string.canberra),
                    context.getString(R.string.perth)
                ).shuffled(),
                context.getString(R.string.canberra)
            )
            questions.add(question11)

            val question12 = Question(
                context.getString(R.string.which_is_the_largest_desert_in_the_world),
                listOf(
                    context.getString(R.string.arabian_desert),
                    context.getString(R.string.gobi_desert),
                    context.getString(R.string.antarctica),
                    context.getString(R.string.sahara_desert)
                ).shuffled(),
                context.getString(R.string.sahara_desert)
            )
            questions.add(question12)

            val question13 = Question(
                context.getString(R.string.who_painted_the_famous_artwork_the_starry_night),
                listOf(
                    context.getString(R.string.vincent_van_gogh),
                    context.getString(R.string.leonardo_da_vinci),
                    context.getString(R.string.claude_monet),
                    context.getString(R.string.tarsila_do_amaral)
                ).shuffled(),
                context.getString(R.string.vincent_van_gogh)
            )
            questions.add(question13)

            val question14 = Question(
                context.getString(R.string.what_is_the_tallest_mountain_in_the_world),
                listOf(
                    context.getString(R.string.k2),
                    context.getString(R.string.mount_everest),
                    context.getString(R.string.mount_fuji),
                    context.getString(R.string.mount_kilimanjaro)
                ).shuffled(),
                context.getString(R.string.mount_everest)
            )
            questions.add(question14)

            val question15 = Question(
                context.getString(R.string.who_invented_the_telephone),
                listOf(
                    context.getString(R.string.isaac_newton),
                    context.getString(R.string.nikola_tesla),
                    context.getString(R.string.alexander_graham_bell),
                    context.getString(R.string.thomas_edison)
                ).shuffled(),
                context.getString(R.string.alexander_graham_bell)
            )
            questions.add(question15)

            val question16 = Question(
                context.getString(R.string.which_planet_is_known_as_the_red_planet),
                listOf(
                    context.getString(R.string.venus),
                    context.getString(R.string.jupiter),
                    context.getString(R.string.earth),
                    context.getString(R.string.mars)
                ).shuffled(),
                context.getString(R.string.mars)
            )
            questions.add(question16)

            val question17 = Question(
                context.getString(R.string.how_many_units_are_in_a_dozen),
                listOf(
                    context.getString(R.string._6_units),
                    context.getString(R.string._12_units),
                    context.getString(R.string._24_units),
                    context.getString(R.string._15_units)
                ).shuffled(),
                context.getString(R.string._12_units)
            )
            questions.add(question17)

            val question18 = Question(
                context.getString(R.string.who_wrote_the_play_romeo_and_juliet),
                listOf(
                    context.getString(R.string.william_shakespeare),
                    context.getString(R.string.tennessee_williams),
                    context.getString(R.string.arthur_miller),
                    context.getString(R.string.oscar_wilde)
                ).shuffled(),
                context.getString(R.string.william_shakespeare)
            )
            questions.add(question18)

            val question19 = Question(
                context.getString(R.string.what_is_the_collective_of_dogs),
                listOf(
                    context.getString(R.string.pack),
                    context.getString(R.string.flock),
                    context.getString(R.string.gang),
                    context.getString(R.string.herd)
                ).shuffled(),
                context.getString(R.string.pack)
            )
            questions.add(question19)

            val question20 = Question(
                context.getString(R.string.the_adjective_venous_is_related_to),
                listOf(
                    context.getString(R.string.vern),
                    context.getString(R.string.vein),
                    context.getString(R.string.venon),
                    context.getString(R.string.vegan)
                ).shuffled(),
                context.getString(R.string.vein)
            )
            questions.add(question20)

            val question21 = Question(
                context.getString(R.string.compensation_for_loss_is_called),
                listOf(
                    context.getString(R.string.indexing),
                    context.getString(R.string.deficit),
                    context.getString(R.string.indemnity),
                    context.getString(R.string.indebted)
                ).shuffled(),
                context.getString(R.string.indemnity)
            )
            questions.add(question21)

            val question22 = Question(
                context.getString(R.string.what_are_the_vowels_in_the_american_alphabet),
                listOf(
                    context.getString(R.string.a_e_i_z_and_u),
                    context.getString(R.string.a_e_i_o_and_y),
                    context.getString(R.string.u_o_i_e_and_b),
                    context.getString(R.string.a_e_i_o_and_u)
                ).shuffled(),
                context.getString(R.string.a_e_i_o_and_u)
            )
            questions.add(question22)

            val question23 = Question(
                context.getString(R.string.the_object_drawn_in_the_center_of_the_flag_of_argentina_is_a),
                listOf(
                    context.getString(R.string.sun),
                    context.getString(R.string.moon),
                    context.getString(R.string.star),
                    context.getString(R.string.sickle_and_hammer)
                ).shuffled(),
                context.getString(R.string.sun)
            )
            questions.add(question23)

            val question24 = Question(
                context.getString(R.string.who_was_the_god_of_thunder_in_greek_mythology),
                listOf(
                    context.getString(R.string.poseidon),
                    context.getString(R.string.zeus),
                    context.getString(R.string.hades),
                    context.getString(R.string.apollo)
                ).shuffled(),
                context.getString(R.string.zeus)
            )
            questions.add(question24)

            val question25 = Question(
                context.getString(R.string.which_goddess_was_known_as_the_goddess_of_love_and_beauty),
                listOf(
                    context.getString(R.string.hera),
                    context.getString(R.string.athena),
                    context.getString(R.string.aphrodite),
                    context.getString(R.string.artemis)
                ).shuffled(),
                context.getString(R.string.aphrodite)
            )
            questions.add(question25)

            val question26 = Question(
                context.getString(R.string.which_god_was_considered_the_god_of_the_seas_and_earthquakes),
                listOf(
                    context.getString(R.string.hephaestus),
                    context.getString(R.string.athena),
                    context.getString(R.string.dionysus),
                    context.getString(R.string.poseidon)
                ).shuffled(),
                context.getString(R.string.poseidon)
            )
            questions.add(question26)

            val question27 = Question(
                context.getString(R.string.which_god_was_lord_of_the_dead_and_ruled_the_underworld),
                listOf(
                    context.getString(R.string.hades),
                    context.getString(R.string.hermes),
                    context.getString(R.string.apollo),
                    context.getString(R.string.zeus)
                ).shuffled(),
                context.getString(R.string.hades)
            )
            questions.add(question27)

            val question28 = Question(
                context.getString(R.string.which_god_was_the_god_of_the_sun_music_and_poetry),
                listOf(
                    context.getString(R.string.hermes),
                    context.getString(R.string.apollo),
                    context.getString(R.string.dionysus),
                    context.getString(R.string.ares)
                ).shuffled(),
                context.getString(R.string.apollo)
            )
            questions.add(question28)

            val question29 = Question(
                context.getString(R.string.what_is_the_process_by_which_plants_make_their_own_food),
                listOf(
                    context.getString(R.string.digestion),
                    context.getString(R.string.perspiration),
                    context.getString(R.string.photosynthesis),
                    context.getString(R.string.breathing)
                ).shuffled(),
                context.getString(R.string.photosynthesis)
            )
            questions.add(question29)

            val question30 = Question(
                context.getString(R.string.what_is_the_strongest_natural_substance_on_earth),
                listOf(
                    context.getString(R.string.wood),
                    context.getString(R.string.glass),
                    context.getString(R.string.iron),
                    context.getString(R.string.diamond)
                ).shuffled(),
                context.getString(R.string.diamond)
            )
            questions.add(question30)

            val question31 = Question(
                context.getString(R.string.if_enzo_was_born_in_2023_how_old_will_he_be_in_2032),
                listOf(
                    context.getString(R.string._10_years_old),
                    context.getString(R.string._13_years_old),
                    context.getString(R.string._03_years_old),
                    context.getString(R.string._32_years_old)
                ).shuffled(),
                context.getString(R.string._10_years_old)
            )
            questions.add(question31)

            val question32 = Question(
                context.getString(R.string.julia_had_5_apples_and_shared_them_equally_with_her_2_friends_is_it_correct_to_say_that_julia_would_have),
                listOf(
                    context.getString(R.string._4_apples),
                    context.getString(R.string._1_apple),
                    context.getString(R.string._2_apple),
                    context.getString(R.string._1_2_apple)
                ).shuffled(),
                context.getString(R.string._1_apple)
            )
            questions.add(question32)

            val question33 = Question(
                context.getString(R.string.it_is_correct_to_say_that_3_3_is_equal_to),
                listOf(
                    context.getString(R.string._8),
                    context.getString(R.string._12),
                    context.getString(R.string._6),
                    context.getString(R.string._24)
                ).shuffled(),
                context.getString(R.string._6)
            )
            questions.add(question33)

            val question34 = Question(
                context.getString(R.string.who_founded_the_microsoft_company),
                listOf(
                    context.getString(R.string.larry_ellison),
                    context.getString(R.string.elon_musk),
                    context.getString(R.string.jeff_bezos),
                    context.getString(R.string.bill_gates)
                ).shuffled(),
                context.getString(R.string.bill_gates)
            )
            questions.add(question34)

            val question35 = Question(
                context.getString(R.string.dark_colored_fizzy_drink_from_a_company_founded_in_1892),
                listOf(
                    context.getString(R.string.coca_cola),
                    context.getString(R.string.sprite),
                    context.getString(R.string.fanta),
                    context.getString(R.string.pepsi)
                ).shuffled(),
                context.getString(R.string.coca_cola)
            )
            questions.add(question35)

            val question36 = Question(
                context.getString(R.string.which_animal_has_black_and_white_stripes_and_is_known_for_being_fast_and_agile),
                listOf(
                    context.getString(R.string.tiger),
                    context.getString(R.string.zebra),
                    context.getString(R.string.leopard),
                    context.getString(R.string.elephant)
                ).shuffled(),
                context.getString(R.string.zebra)
            )
            questions.add(question36)

            val question37 = Question(
                context.getString(R.string.which_animal_flies_and_has_a_diet_based_on_flower_nectar),
                listOf(
                    context.getString(R.string.butterfly),
                    context.getString(R.string.bird),
                    context.getString(R.string.bee),
                    context.getString(R.string.bat)
                ).shuffled(),
                context.getString(R.string.bee)
            )
            questions.add(question37)

            val question38 = Question(
                context.getString(R.string.which_animal_lives_in_water_has_gills_and_fins),
                listOf(
                    context.getString(R.string.turtle),
                    context.getString(R.string.dolphin),
                    context.getString(R.string.penguin),
                    context.getString(R.string.shark)
                ).shuffled(),
                context.getString(R.string.shark)
            )
            questions.add(question38)

            val question39 = Question(
                context.getString(R.string.who_was_the_leader_of_the_independence_movement_in_brazil),
                listOf(
                    context.getString(R.string.dom_pedro_i),
                    context.getString(R.string.pedro_lvares_cabral),
                    context.getString(R.string.dom_pedro_ii),
                    context.getString(R.string.tiradentes)
                ).shuffled(),
                context.getString(R.string.dom_pedro_i)
            )
            questions.add(question39)

            val question40 = Question(
                context.getString(R.string.who_was_the_famous_pacifist_leader_who_fought_for_the_civil_rights_of_african_americans_in_the_united_states),
                listOf(
                    context.getString(R.string.abraham_lincoln),
                    context.getString(R.string.martin_luther_king_jr),
                    context.getString(R.string.george_washington),
                    context.getString(R.string.thomas_jefferson)
                ).shuffled(),
                context.getString(R.string.martin_luther_king_jr)
            )
            questions.add(question40)

            val question41 = Question(
                context.getString(R.string.in_what_year_did_world_war_ii_take_place),
                listOf(
                    context.getString(R.string._1945),
                    context.getString(R.string._1960),
                    context.getString(R.string._1939),
                    context.getString(R.string._1950)
                ).shuffled(),
                context.getString(R.string._1939)
            )
            questions.add(question41)

            val question42 = Question(
                context.getString(R.string.who_was_the_south_african_leader_who_fought_apartheid_and_became_the_country_s_first_black_president),
                listOf(
                    context.getString(R.string.thabo_mbeki),
                    context.getString(R.string.desmond_tutu),
                    context.getString(R.string.jacob_zuma),
                    context.getString(R.string.nelson_mandela)
                ).shuffled(),
                context.getString(R.string.nelson_mandela)
            )
            questions.add(question42)

            val question43 = Question(
                context.getString(R.string.what_is_the_subject_of_the_sentence_the_dog_barks_in_the_backyard),
                listOf(
                    context.getString(R.string.the_dog),
                    context.getString(R.string.barks),
                    context.getString(R.string.in_the_backyard),
                    context.getString(R.string.the)
                ).shuffled(),
                context.getString(R.string.the_dog)
            )
            questions.add(question43)

            val question44 = Question(
                context.getString(R.string.what_is_the_antonym_of_the_word_cheerful),
                listOf(
                    context.getString(R.string.happy),
                    context.getString(R.string.sad),
                    context.getString(R.string.content),
                    context.getString(R.string.radiant)
                ).shuffled(),
                context.getString(R.string.sad)
            )
            questions.add(question44)

            val question45 = Question(
                context.getString(R.string.what_is_the_comparative_degree_of_superiority_of_the_adjective_beautiful),
                listOf(
                    context.getString(R.string.handsome),
                    context.getString(R.string.pretty),
                    context.getString(R.string.more_beautiful),
                    context.getString(R.string.elegant)
                ).shuffled(),
                context.getString(R.string.more_beautiful)
            )
            questions.add(question45)

            val question46 = Question(
                context.getString(R.string.who_discovered_brazil),
                listOf(
                    context.getString(R.string.christopher_columbus),
                    context.getString(R.string.vasco_da_gama),
                    context.getString(R.string.ferdinand_magellan),
                    context.getString(R.string.pedro_alvares_cabral)
                ).shuffled(),
                context.getString(R.string.pedro_alvares_cabral)
            )
            questions.add(question46)

            val question47 = Question(
                context.getString(R.string.what_is_the_perimeter_of_a_square_with_sides_measuring_6_cm_each),
                listOf(
                    context.getString(R.string._24_centimeters),
                    context.getString(R.string._12_centimeters),
                    context.getString(R.string._18_centimeters),
                    context.getString(R.string._36_centimeters)
                ).shuffled(),
                context.getString(R.string._24_centimeters)
            )
            questions.add(question47)

            val question48 = Question(
                context.getString(R.string.what_is_the_result_of_the_following_operation_8_4_2),
                listOf(
                    context.getString(R.string.the_result_is_12),
                    context.getString(R.string.the_result_is_10),
                    context.getString(R.string.the_result_is_14),
                    context.getString(R.string.the_result_is_8)
                ).shuffled(),
                context.getString(R.string.the_result_is_10)
            )
            questions.add(question48)

            val question49 = Question(
                context.getString(R.string.what_is_the_result_of_the_following_operation_4_2_2),
                listOf(
                    context.getString(R.string.the_result_is_2),
                    context.getString(R.string.the_result_is_4),
                    context.getString(R.string.the_result_is_0),
                    context.getString(R.string.the_result_is_6)
                ).shuffled(),
                context.getString(R.string.the_result_is_0)
            )
            questions.add(question49)

            val question50 = Question(
                context.getString(R.string.what_is_the_result_of_the_following_operation_4_x_5),
                listOf(
                    context.getString(R.string.the_result_is_25),
                    context.getString(R.string.the_result_is_40),
                    context.getString(R.string.the_result_is_50),
                    context.getString(R.string.the_result_is_20)
                ).shuffled(),
                context.getString(R.string.the_result_is_20)
            )
            questions.add(question50)

            val question51 = Question(
                context.getString(R.string.what_is_the_result_of_the_following_operation_40_15),
                listOf(
                    context.getString(R.string.the_result_is_25),
                    context.getString(R.string.the_result_is_35),
                    context.getString(R.string.the_result_is_40),
                    context.getString(R.string.the_result_is_55)
                ).shuffled(),
                context.getString(R.string.the_result_is_25)
            )
            questions.add(question51)

            val question52 = Question(
                context.getString(R.string.what_is_the_result_of_the_following_operation_8_2),
                listOf(
                    context.getString(R.string.the_result_is_2),
                    context.getString(R.string.the_result_is_4),
                    context.getString(R.string.the_result_is_8),
                    context.getString(R.string.the_result_is_6)
                ).shuffled(),
                context.getString(R.string.the_result_is_4)
            )
            questions.add(question52)

            val question53 = Question(
                context.getString(R.string.it_has_wings_and_can_fly_through_the_skies_it_is_known_to_migrate_great_distances_what_animal_is_this),
                listOf(
                    context.getString(R.string.dog),
                    context.getString(R.string.duck),
                    context.getString(R.string.eagle),
                    context.getString(R.string.rabbit)
                ).shuffled(),
                context.getString(R.string.eagle)
            )
            questions.add(question53)

            val question54 = Question(
                context.getString(R.string.farm_animal_that_produces_milk_it_has_four_legs_and_horns_what_animal_is_this),
                listOf(
                    context.getString(R.string.cat),
                    context.getString(R.string.dog),
                    context.getString(R.string.horse),
                    context.getString(R.string.cow)
                ).shuffled(),
                context.getString(R.string.cow)
            )
            questions.add(question54)

            val question55 = Question(
                context.getString(R.string.how_many_months_does_a_year_have),
                listOf(
                    context.getString(R.string._12_months),
                    context.getString(R.string._10_months),
                    context.getString(R.string._11_months),
                    context.getString(R.string._13_months)
                ).shuffled(),
                context.getString(R.string._12_months)
            )
            questions.add(question55)

            val question56 = Question(
                context.getString(R.string.what_is_the_capital_of_china),
                listOf(
                    context.getString(R.string.shanghai),
                    context.getString(R.string.beijing),
                    context.getString(R.string.hong_kong),
                    context.getString(R.string.canton)
                ).shuffled(),
                context.getString(R.string.beijing)
            )
            questions.add(question56)

            val question57 = Question(
                context.getString(R.string.what_civilization_built_the_pyramids_in_egypt),
                listOf(
                    context.getString(R.string.mesopotamia),
                    context.getString(R.string.greek),
                    context.getString(R.string.egyptian),
                    context.getString(R.string.roman)
                ).shuffled(),
                context.getString(R.string.egyptian)
            )
            questions.add(question57)

            val question58 = Question(
                context.getString(R.string.what_is_the_largest_country_by_land_area),
                listOf(
                    context.getString(R.string.canada),
                    context.getString(R.string.united_states),
                    context.getString(R.string.china),
                    context.getString(R.string.russia)
                ).shuffled(),
                context.getString(R.string.russia)
            )
            questions.add(question58)

            val question59 = Question(
                context.getString(R.string.what_is_the_most_populous_city_in_the_world),
                listOf(
                    context.getString(R.string.tokyo),
                    context.getString(R.string.shanghai),
                    context.getString(R.string.bombay),
                    context.getString(R.string.s_o_paulo)
                ).shuffled(),
                context.getString(R.string.tokyo)
            )
            questions.add(question59)

            val question60 = Question(
                context.getString(R.string.what_is_the_closest_planet_to_the_sun),
                listOf(
                    context.getString(R.string.venus),
                    context.getString(R.string.mercury),
                    context.getString(R.string.earth),
                    context.getString(R.string.mars)
                ).shuffled(),
                context.getString(R.string.mercury)
            )
            questions.add(question60)

            val question61 = Question(
                context.getString(R.string.what_is_the_largest_rainforest_in_the_world),
                listOf(
                    context.getString(R.string.atlantic_forest),
                    context.getString(R.string.thick),
                    context.getString(R.string.amazon_rainforest),
                    context.getString(R.string.pantanal)
                ).shuffled(),
                context.getString(R.string.amazon_rainforest)
            )
            questions.add(question61)

            val question62 = Question(
                context.getString(R.string.what_is_the_popular_brazilian_festival_known_for_its_dances_costumes_and_samba_school_parades),
                listOf(
                    context.getString(R.string.june_celebration),
                    context.getString(R.string.saint_john),
                    context.getString(R.string.oktoberfest),
                    context.getString(R.string.carnival)
                ).shuffled(),
                context.getString(R.string.carnival)
            )
            questions.add(question62)

            val question63 = Question(
                context.getString(R.string.what_is_the_name_of_the_official_currency_of_brazil),
                listOf(
                    context.getString(R.string.real),
                    context.getString(R.string.peso),
                    context.getString(R.string.dollar),
                    context.getString(R.string.euro)
                ).shuffled(),
                context.getString(R.string.real)
            )
            questions.add(question63)

            val question64 = Question(
                context.getString(R.string.what_is_the_name_of_the_official_currency_of_united_states),
                listOf(
                    context.getString(R.string.real),
                    context.getString(R.string.dollar),
                    context.getString(R.string.peso),
                    context.getString(R.string.euro)
                ).shuffled(),
                context.getString(R.string.dollar)
            )
            questions.add(question64)

            val question65 = Question(
                context.getString(R.string.who_owns_amazon_company),
                listOf(
                    context.getString(R.string.mark_zuckerberg),
                    context.getString(R.string.elon_musk),
                    context.getString(R.string.jeff_bezos),
                    context.getString(R.string.bill_gates)
                ).shuffled(),
                context.getString(R.string.jeff_bezos)
            )
            questions.add(question65)

            val question66 = Question(
                context.getString(R.string.animal_that_flies_is_known_for_its_ability_to_rotate_its_head_almost_360_degrees_what_animal_is_this),
                listOf(
                    context.getString(R.string.penguin),
                    context.getString(R.string.seagull),
                    context.getString(R.string.duck),
                    context.getString(R.string.owl)
                ).shuffled(),
                context.getString(R.string.owl)
            )
            questions.add(question66)

            val question67 = Question(
                context.getString(R.string.the_titanic_ship_sank_in_the_year),
                listOf(
                    "1912",
                    "1924",
                    "2012",
                    "1939").shuffled(),
                "1912"
            )
            questions.add(question67)

            return questions
        }
}