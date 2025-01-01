package dev.aaa1115910.bv.util

import android.content.Context
import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.bv.R

fun UgcType.getDisplayName(context: Context) = when (this) {
    UgcType.Douga -> R.string.ugc_type_douga
    UgcType.DougaMad -> R.string.ugc_type_douga_mad
    UgcType.DougaMmd -> R.string.ugc_type_douga_mmd
    UgcType.DougaHandDrawn -> R.string.ugc_type_douga_hand_drawn
    UgcType.DougaVoice -> R.string.ugc_type_douga_voice
    UgcType.DougaGarageKit -> R.string.ugc_type_douga_garage_kit
    UgcType.DougaTokusatsu -> R.string.ugc_type_douga_tokusatsu
    UgcType.DougaAcgnTalks -> R.string.ugc_type_douga_acgn_talks
    UgcType.DougaOther -> R.string.ugc_type_douga_other

    UgcType.Game -> R.string.ugc_type_game
    UgcType.GameStandAlone -> R.string.ugc_type_game_stand_alone
    UgcType.GameESports -> R.string.ugc_type_game_e_sports
    UgcType.GameMobile -> R.string.ugc_type_game_mobile
    UgcType.GameOnline -> R.string.ugc_type_game_online
    UgcType.GameBoard -> R.string.ugc_type_game_board
    UgcType.GameGmv -> R.string.ugc_type_game_gmv
    UgcType.GameMusic -> R.string.ugc_type_game_music
    UgcType.GameMugen -> R.string.ugc_type_game_mugen

    UgcType.Kichiku -> R.string.ugc_type_kichiku
    UgcType.KichikuGuide -> R.string.ugc_type_kichiku_guild
    UgcType.KichikuMad -> R.string.ugc_type_kichiku_mad
    UgcType.KichikuManualVocaloid -> R.string.ugc_type_kichiku_manual_vocaloid
    UgcType.KichikuTheatre -> R.string.ugc_type_kichiku_theatre
    UgcType.KichikuCourse -> R.string.ugc_type_kichiku_course

    UgcType.Music -> R.string.ugc_type_music
    UgcType.MusicOriginal -> R.string.ugc_type_music_original
    UgcType.MusicLive -> R.string.ugc_type_music_live
    UgcType.MusicCover -> R.string.ugc_type_music_cover
    UgcType.MusicPerform -> R.string.ugc_type_music_perform
    UgcType.MusicCommentary -> R.string.ugc_type_music_commentary
    UgcType.MusicVocaloidUtau -> R.string.ugc_type_music_vocaloid_utau
    UgcType.MusicMv -> R.string.ugc_type_music_mv
    UgcType.MusicFanVideos -> R.string.ugc_type_music_fan_videos
    UgcType.MusicAiMusic -> R.string.ugc_type_music_ai_music
    UgcType.MusicRadio -> R.string.ugc_type_music_radio
    UgcType.MusicTutorial -> R.string.ugc_type_music_tutorial
    UgcType.MusicOther -> R.string.ugc_type_music_other

    UgcType.Dance -> R.string.ugc_type_dance
    UgcType.DanceOtaku -> R.string.ugc_type_dance_otaku
    UgcType.DanceHiphop -> R.string.ugc_type_dance_hiphop
    UgcType.DanceStar -> R.string.ugc_type_dance_star
    UgcType.DanceChina -> R.string.ugc_type_dance_china
    UgcType.DanceGestures -> R.string.ugc_type_dance_gestures
    UgcType.DanceThreeD -> R.string.ugc_type_dance_three_d
    UgcType.DanceDemo -> R.string.ugc_type_dance_demo

    UgcType.Cinephile -> R.string.ugc_type_cinephile
    UgcType.CinephileCinecism -> R.string.ugc_type_cinephile_cinecism
    UgcType.CinephileNibtage -> R.string.ugc_type_cinephile_nibtage
    UgcType.CinephileMashup -> R.string.ugc_type_cinephile_mashup
    UgcType.CinephileAiImagine -> R.string.ugc_type_cinephile_ai_imagine
    UgcType.CinephileTrailerInfo -> R.string.ugc_type_cinephile_trailer_info
    UgcType.CinephileShortPlay -> R.string.ugc_type_cinephile_short_play
    UgcType.CinephileShortFilm -> R.string.ugc_type_cinephile_short_film
    UgcType.CinephileComperhensive -> R.string.ugc_type_cinephile_comperhensive

    UgcType.Ent -> R.string.ugc_type_ent
    UgcType.EntTalker -> R.string.ugc_type_ent_talker
    UgcType.EntCpRecommendation -> R.string.ugc_type_ent_cp_recommendation
    UgcType.EntBeauty -> R.string.ugc_type_ent_beauty
    UgcType.EntFans -> R.string.ugc_type_ent_fans
    UgcType.EntEntertainmentNews -> R.string.ugc_type_ent_entertainment_news
    UgcType.EntCelebrity -> R.string.ugc_type_ent_celebrity
    UgcType.EntVariety -> R.string.ugc_type_ent_variety

    UgcType.Knowledge -> R.string.ugc_type_knowledge
    UgcType.KnowledgeScience -> R.string.ugc_type_knowledge_science
    UgcType.KnowledgeSocialScience -> R.string.ugc_type_knowledge_social_science
    UgcType.KnowledgeHumanity -> R.string.ugc_type_knowledge_humanity
    UgcType.KnowledgeBusiness -> R.string.ugc_type_knowledge_business
    UgcType.KnowledgeCampus -> R.string.ugc_type_knowledge_campus
    UgcType.KnowledgeCareer -> R.string.ugc_type_knowledge_career
    UgcType.KnowledgeDesign -> R.string.ugc_type_knowledge_design
    UgcType.KnowledgeSkill -> R.string.ugc_type_knowledge_skill

    UgcType.Tech -> R.string.ugc_type_tech
    UgcType.TechDigital -> R.string.ugc_type_tech_digital
    UgcType.TechApplication -> R.string.ugc_type_tech_application
    UgcType.TechComputerTech -> R.string.ugc_type_tech_computer_tech
    UgcType.TechIndustry -> R.string.ugc_type_tech_industry
    UgcType.TechDiy -> R.string.ugc_type_tech_diy

    UgcType.Information -> R.string.ugc_type_information
    UgcType.InformationHotspot -> R.string.ugc_type_information_hotspot
    UgcType.InformationGlobal -> R.string.ugc_type_information_global
    UgcType.InformationSocial -> R.string.ugc_type_information_social
    UgcType.InformationMultiple -> R.string.ugc_type_information_multiple

    UgcType.Food -> R.string.ugc_type_food
    UgcType.FoodMake -> R.string.ugc_type_food_make
    UgcType.FoodDetective -> R.string.ugc_type_food_detective
    UgcType.FoodMeasurement -> R.string.ugc_type_food_measurement
    UgcType.FoodRural -> R.string.ugc_type_food_rural
    UgcType.FoodRecord -> R.string.ugc_type_food_record

    UgcType.Life -> R.string.ugc_type_life
    UgcType.LifeFunny -> R.string.ugc_type_life_funny
    UgcType.LifeParenting -> R.string.ugc_type_life_parenting
    UgcType.LifeTravel -> R.string.ugc_type_life_travel
    UgcType.LiseRuralLife -> R.string.ugc_type_life_rural_life
    UgcType.LifeHome -> R.string.ugc_type_life_home
    UgcType.LifeHandMake -> R.string.ugc_type_life_hand_make
    UgcType.LifePainting -> R.string.ugc_type_life_painting
    UgcType.LifeDaily -> R.string.ugc_type_life_daily

    UgcType.Car -> R.string.ugc_type_car
    UgcType.CarKnowledge -> R.string.ugc_type_car_knowledge
    UgcType.CarStrategy -> R.string.ugc_type_car_strategy
    UgcType.CarNewEnergyVehicle -> R.string.ugc_type_car_new_energy_vehicle
    UgcType.CarRacing -> R.string.ugc_type_car_racing
    UgcType.CarModifiedVehicle -> R.string.ugc_type_car_modified_vehicle
    UgcType.CarMotorcycle -> R.string.ugc_type_car_motorcycle
    UgcType.CarTouringCar -> R.string.ugc_type_car_touring_car
    UgcType.CarLife -> R.string.ugc_type_car_life

    UgcType.Fashion -> R.string.ugc_type_fashion
    UgcType.FashionMakeup -> R.string.ugc_type_fashion_makeup
    UgcType.FashionCos -> R.string.ugc_type_fashion_cos
    UgcType.FashionClothing -> R.string.ugc_type_fashion_clothing
    UgcType.FashionCatwalk -> R.string.ugc_type_fashion_catwalk

    UgcType.Sports -> R.string.ugc_type_sports
    UgcType.SportsBasketball -> R.string.ugc_type_sports_basketball
    UgcType.SportsFootball -> R.string.ugc_type_sports_football
    UgcType.SportsAerobics -> R.string.ugc_type_sports_aerobics
    UgcType.SportsAthletic -> R.string.ugc_type_sports_athletic
    UgcType.SportsCulture -> R.string.ugc_type_sports_culture
    UgcType.SportsComprehensive -> R.string.ugc_type_sports_comprehensive

    UgcType.Animal -> R.string.ugc_type_animal
    UgcType.AnimalCat -> R.string.ugc_type_animal_cat
    UgcType.AnimalDog -> R.string.ugc_type_animal_dog
    UgcType.AnimalReptiles -> R.string.ugc_type_animal_reptiles
    UgcType.AnimalWildAnima -> R.string.ugc_type_animal_wild_anima
    UgcType.AnimalSecondEdition -> R.string.ugc_type_animal_second_edition
    UgcType.AnimalComposite -> R.string.ugc_type_animal_composite
}.stringRes(context)