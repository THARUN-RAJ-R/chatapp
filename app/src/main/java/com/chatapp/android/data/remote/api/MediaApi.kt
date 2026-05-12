package com.chatapp.android.data.remote.api

import com.chatapp.android.data.remote.dto.ApiResponse
import com.chatapp.android.data.remote.dto.MediaUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaApi {
    @Multipart
    @POST("api/media/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ApiResponse<MediaUploadResponse>>
}
