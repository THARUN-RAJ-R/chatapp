package com.chatapp.android.di;

import com.chatapp.android.data.local.AppDatabase;
import com.chatapp.android.data.local.dao.ChatDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideChatDaoFactory implements Factory<ChatDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideChatDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ChatDao get() {
    return provideChatDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideChatDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideChatDaoFactory(dbProvider);
  }

  public static ChatDao provideChatDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChatDao(db));
  }
}
