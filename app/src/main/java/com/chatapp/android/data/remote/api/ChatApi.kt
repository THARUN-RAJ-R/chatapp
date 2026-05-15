package com.chatapp.android.data.remote.api

import com.chatapp.android.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {
    @GET("api/chats")
    suspend fun getChats(): Response<ApiResponse<List<ChatDto>>>

    @POST("api/chats/direct")
    suspend fun startDirectChat(@Body request: StartDirectChatRequest): Response<ApiResponse<ChatDto>>

    @GET("api/chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30,
        @Query("afterSeq") afterSeq: Long? = null
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("api/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<ApiResponse<ChatDto>>

    @POST("api/groups/{chatId}/members")
    suspend fun addMembers(
        @Path("chatId") chatId: String,
        @Body request: AddMembersRequest
    ): Response<ApiResponse<Void>>

    @DELETE("api/groups/{chatId}/members/{userId}")
    suspend fun removeMember(
        @Path("chatId") chatId: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<Void>>
}
