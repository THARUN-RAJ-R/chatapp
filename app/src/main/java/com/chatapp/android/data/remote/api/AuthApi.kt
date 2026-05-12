package com.chatapp.android.data.remote.api

import com.chatapp.android.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<ApiResponse<Map<String, String>>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<ApiResponse<AuthData>>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<ApiResponse<AuthData>>
}
