package com.chatapp.android.data.remote.api

import com.chatapp.android.data.remote.dto.ApiResponse
import com.chatapp.android.data.remote.dto.LoginRequest
import com.chatapp.android.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<UserDto>>
}
