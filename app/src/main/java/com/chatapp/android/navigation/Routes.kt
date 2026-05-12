package com.chatapp.android.navigation

object Routes {
    const val SPLASH        = "splash"
    const val PHONE         = "phone"
    const val OTP           = "otp/{phone}"
    const val PROFILE_SETUP = "profile_setup"
    const val HOME          = "home"
    const val CHAT          = "chat/{chatId}"
    const val CONTACTS      = "contacts"
    const val CREATE_GROUP  = "create_group"
    const val GROUP_INFO    = "group_info/{chatId}"
    const val IMAGE_VIEWER  = "image_viewer"

    fun otp(phone: String)       = "otp/$phone"
    fun chat(chatId: String)     = "chat/$chatId"
    fun groupInfo(chatId: String) = "group_info/$chatId"
}
