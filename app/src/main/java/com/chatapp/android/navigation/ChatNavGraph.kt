package com.chatapp.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.chatapp.android.ui.screen.auth.PhoneScreen
import com.chatapp.android.ui.screen.chat.ChatScreen
import com.chatapp.android.ui.screen.group.CreateGroupScreen
import com.chatapp.android.ui.screen.group.GroupInfoScreen
import com.chatapp.android.ui.screen.home.ContactsScreen
import com.chatapp.android.ui.screen.home.HomeScreen
import com.chatapp.android.ui.screen.splash.SplashScreen

@Composable
fun ChatNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome  = { navController.navigate(Routes.HOME)  { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToPhone = { navController.navigate(Routes.PHONE) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }

        composable(Routes.PHONE) {
            PhoneScreen(onLoggedIn = {
                navController.navigate(Routes.HOME) { popUpTo(Routes.PHONE) { inclusive = true } }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                onChatClick        = { chatId -> navController.navigate(Routes.chat(chatId)) },
                onContactsClick    = { navController.navigate(Routes.CONTACTS) },
                onCreateGroupClick = { navController.navigate(Routes.CREATE_GROUP) }
            )
        }

        composable(Routes.CONTACTS) {
            ContactsScreen(onChatStarted = { chatId ->
                navController.navigate(Routes.chat(chatId)) { popUpTo(Routes.CONTACTS) { inclusive = true } }
            })
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "chatapp://chat/{chatId}" })
        ) { back ->
            val chatId = back.arguments?.getString("chatId") ?: ""
            ChatScreen(
                chatId    = chatId,
                onBack    = { navController.popBackStack() },
                onInfoClick = { navController.navigate(Routes.groupInfo(chatId)) }
            )
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onGroupCreated = { chatId -> navController.navigate(Routes.chat(chatId)) { popUpTo(Routes.CREATE_GROUP) { inclusive = true } } },
                onBack         = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.GROUP_INFO,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { back ->
            val chatId = back.arguments?.getString("chatId") ?: ""
            GroupInfoScreen(chatId = chatId, onBack = { navController.popBackStack() })
        }
    }
}
