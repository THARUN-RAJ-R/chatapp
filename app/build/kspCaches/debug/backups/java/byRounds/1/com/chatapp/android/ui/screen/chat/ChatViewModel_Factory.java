package com.chatapp.android.ui.screen.chat;

import com.chatapp.android.data.local.dao.ChatDao;
import com.chatapp.android.data.local.dao.MessageDao;
import com.chatapp.android.data.websocket.StompWebSocketClient;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<MessageDao> messageDaoProvider;

  private final Provider<ChatDao> chatDaoProvider;

  private final Provider<StompWebSocketClient> stompClientProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public ChatViewModel_Factory(Provider<MessageDao> messageDaoProvider,
      Provider<ChatDao> chatDaoProvider, Provider<StompWebSocketClient> stompClientProvider,
      Provider<TokenManager> tokenManagerProvider) {
    this.messageDaoProvider = messageDaoProvider;
    this.chatDaoProvider = chatDaoProvider;
    this.stompClientProvider = stompClientProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(messageDaoProvider.get(), chatDaoProvider.get(), stompClientProvider.get(), tokenManagerProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<MessageDao> messageDaoProvider,
      Provider<ChatDao> chatDaoProvider, Provider<StompWebSocketClient> stompClientProvider,
      Provider<TokenManager> tokenManagerProvider) {
    return new ChatViewModel_Factory(messageDaoProvider, chatDaoProvider, stompClientProvider, tokenManagerProvider);
  }

  public static ChatViewModel newInstance(MessageDao messageDao, ChatDao chatDao,
      StompWebSocketClient stompClient, TokenManager tokenManager) {
    return new ChatViewModel(messageDao, chatDao, stompClient, tokenManager);
  }
}
