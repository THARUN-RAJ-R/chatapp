package com.chatapp.android.ui.screen.home;

import android.content.Context;
import com.chatapp.android.data.remote.api.ChatApi;
import com.chatapp.android.data.remote.api.UserApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ContactsViewModel_Factory implements Factory<ContactsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<UserApi> userApiProvider;

  private final Provider<ChatApi> chatApiProvider;

  public ContactsViewModel_Factory(Provider<Context> contextProvider,
      Provider<UserApi> userApiProvider, Provider<ChatApi> chatApiProvider) {
    this.contextProvider = contextProvider;
    this.userApiProvider = userApiProvider;
    this.chatApiProvider = chatApiProvider;
  }

  @Override
  public ContactsViewModel get() {
    return newInstance(contextProvider.get(), userApiProvider.get(), chatApiProvider.get());
  }

  public static ContactsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<UserApi> userApiProvider, Provider<ChatApi> chatApiProvider) {
    return new ContactsViewModel_Factory(contextProvider, userApiProvider, chatApiProvider);
  }

  public static ContactsViewModel newInstance(Context context, UserApi userApi, ChatApi chatApi) {
    return new ContactsViewModel(context, userApi, chatApi);
  }
}
