package com.chatapp.android.service;

import com.chatapp.android.data.remote.api.UserApi;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ChatFirebaseMessagingService_MembersInjector implements MembersInjector<ChatFirebaseMessagingService> {
  private final Provider<UserApi> userApiProvider;

  public ChatFirebaseMessagingService_MembersInjector(Provider<UserApi> userApiProvider) {
    this.userApiProvider = userApiProvider;
  }

  public static MembersInjector<ChatFirebaseMessagingService> create(
      Provider<UserApi> userApiProvider) {
    return new ChatFirebaseMessagingService_MembersInjector(userApiProvider);
  }

  @Override
  public void injectMembers(ChatFirebaseMessagingService instance) {
    injectUserApi(instance, userApiProvider.get());
  }

  @InjectedFieldSignature("com.chatapp.android.service.ChatFirebaseMessagingService.userApi")
  public static void injectUserApi(ChatFirebaseMessagingService instance, UserApi userApi) {
    instance.userApi = userApi;
  }
}
