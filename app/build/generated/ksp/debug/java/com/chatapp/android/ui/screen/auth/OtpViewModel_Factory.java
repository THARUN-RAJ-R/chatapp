package com.chatapp.android.ui.screen.auth;

import com.chatapp.android.data.remote.api.AuthApi;
import com.chatapp.android.util.TokenManager;
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
public final class OtpViewModel_Factory implements Factory<OtpViewModel> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public OtpViewModel_Factory(Provider<AuthApi> authApiProvider,
      Provider<TokenManager> tokenManagerProvider) {
    this.authApiProvider = authApiProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public OtpViewModel get() {
    return newInstance(authApiProvider.get(), tokenManagerProvider.get());
  }

  public static OtpViewModel_Factory create(Provider<AuthApi> authApiProvider,
      Provider<TokenManager> tokenManagerProvider) {
    return new OtpViewModel_Factory(authApiProvider, tokenManagerProvider);
  }

  public static OtpViewModel newInstance(AuthApi authApi, TokenManager tokenManager) {
    return new OtpViewModel(authApi, tokenManager);
  }
}
