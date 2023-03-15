package com.example.recallify.view.ui.resource.controller

import com.example.recallify.R

sealed class Screens(
    val route: String,
    val icon: Int,
    val name: String,
) {

    object DashBoard: Screens("dash_board_Screen", R.drawable.home_48, "Home")
    object DailyDiary: Screens("daily_diary_screen", R.drawable.daily_diary_48, "Daily diary")
    object SideQuest: Screens("side_quest_screen", R.drawable.sidequest_58, "Side quest")
    object ThinkFast: Screens("think_fast_screen", R.drawable.thinkfast_48, "Think fast")
    object Accounts: Screens("accounts_screen", R.drawable.account, "Profile")

    object Items {
        val list = listOf(
            DashBoard,
            DailyDiary,
            SideQuest,
            ThinkFast,
            Accounts,
        )
    }
}