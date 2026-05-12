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

    @POST("api/users/contacts/sync")
    suspend fun syncContacts(@Body request: ContactSyncRequest): Response<ApiResponse<List<UserDto>>>

    @PUT("api/users/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Void>>
}
