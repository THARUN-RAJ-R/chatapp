package com.chatapp.android.data.remote.api

import com.chatapp.android.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/users/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    @Multipart
    @PUT("api/users/me")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): Response<ApiResponse<UserDto>>

    /**
     * Look up a user by phone number.
     * Returns 200 with UserDto if found, 404 if not registered.
     */
    @GET("api/users/by-phone")
    suspend fun findByPhone(@Query("phone") phone: String): Response<ApiResponse<UserDto>>

    @PUT("api/users/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Void>>
}
