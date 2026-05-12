package com.chatapp.android.ui.screen.home;

import com.chatapp.android.data.local.dao.ChatDao;
import com.chatapp.android.data.remote.api.ChatApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ChatDao> chatDaoProvider;

  private final Provider<ChatApi> chatApiProvider;

  public HomeViewModel_Factory(Provider<ChatDao> chatDaoProvider,
      Provider<ChatApi> chatApiProvider) {
    this.chatDaoProvider = chatDaoProvider;
    this.chatApiProvider = chatApiProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(chatDaoProvider.get(), chatApiProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ChatDao> chatDaoProvider,
      Provider<ChatApi> chatApiProvider) {
    return new HomeViewModel_Factory(chatDaoProvider, chatApiProvider);
  }

  public static HomeViewModel newInstance(ChatDao chatDao, ChatApi chatApi) {
    return new HomeViewModel(chatDao, chatApi);
  }
}
