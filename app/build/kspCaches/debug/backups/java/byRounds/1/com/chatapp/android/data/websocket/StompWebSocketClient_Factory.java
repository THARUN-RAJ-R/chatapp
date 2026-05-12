package com.chatapp.android.data.websocket;

import com.chatapp.android.util.TokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
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
public final class StompWebSocketClient_Factory implements Factory<StompWebSocketClient> {
  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public StompWebSocketClient_Factory(Provider<TokenManager> tokenManagerProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public StompWebSocketClient get() {
    return newInstance(tokenManagerProvider.get(), okHttpClientProvider.get());
  }

  public static StompWebSocketClient_Factory create(Provider<TokenManager> tokenManagerProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    return new StompWebSocketClient_Factory(tokenManagerProvider, okHttpClientProvider);
  }

  public static StompWebSocketClient newInstance(TokenManager tokenManager,
      OkHttpClient okHttpClient) {
    return new StompWebSocketClient(tokenManager, okHttpClient);
  }
}
