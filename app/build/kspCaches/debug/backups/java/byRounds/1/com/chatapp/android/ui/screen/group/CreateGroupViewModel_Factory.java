package com.chatapp.android.ui.screen.group;

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
public final class CreateGroupViewModel_Factory implements Factory<CreateGroupViewModel> {
  private final Provider<ChatApi> chatApiProvider;

  public CreateGroupViewModel_Factory(Provider<ChatApi> chatApiProvider) {
    this.chatApiProvider = chatApiProvider;
  }

  @Override
  public CreateGroupViewModel get() {
    return newInstance(chatApiProvider.get());
  }

  public static CreateGroupViewModel_Factory create(Provider<ChatApi> chatApiProvider) {
    return new CreateGroupViewModel_Factory(chatApiProvider);
  }

  public static CreateGroupViewModel newInstance(ChatApi chatApi) {
    return new CreateGroupViewModel(chatApi);
  }
}
