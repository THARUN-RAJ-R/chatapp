package com.chatapp.android;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.chatapp.android.data.local.AppDatabase;
import com.chatapp.android.data.local.dao.ChatDao;
import com.chatapp.android.data.local.dao.MessageDao;
import com.chatapp.android.data.remote.api.AuthApi;
import com.chatapp.android.data.remote.api.ChatApi;
import com.chatapp.android.data.remote.api.UserApi;
import com.chatapp.android.data.websocket.StompWebSocketClient;
import com.chatapp.android.di.DatabaseModule_ProvideChatDaoFactory;
import com.chatapp.android.di.DatabaseModule_ProvideDatabaseFactory;
import com.chatapp.android.di.DatabaseModule_ProvideMessageDaoFactory;
import com.chatapp.android.di.NetworkModule_ProvideAuthApiFactory;
import com.chatapp.android.di.NetworkModule_ProvideChatApiFactory;
import com.chatapp.android.di.NetworkModule_ProvideOkHttpClientFactory;
import com.chatapp.android.di.NetworkModule_ProvideRetrofitFactory;
import com.chatapp.android.di.NetworkModule_ProvideUserApiFactory;
import com.chatapp.android.service.ChatFirebaseMessagingService;
import com.chatapp.android.service.ChatFirebaseMessagingService_MembersInjector;
import com.chatapp.android.ui.screen.auth.OtpViewModel;
import com.chatapp.android.ui.screen.auth.OtpViewModel_HiltModules;
import com.chatapp.android.ui.screen.auth.PhoneViewModel;
import com.chatapp.android.ui.screen.auth.PhoneViewModel_HiltModules;
import com.chatapp.android.ui.screen.auth.ProfileSetupViewModel;
import com.chatapp.android.ui.screen.auth.ProfileSetupViewModel_HiltModules;
import com.chatapp.android.ui.screen.chat.ChatViewModel;
import com.chatapp.android.ui.screen.chat.ChatViewModel_HiltModules;
import com.chatapp.android.ui.screen.group.CreateGroupViewModel;
import com.chatapp.android.ui.screen.group.CreateGroupViewModel_HiltModules;
import com.chatapp.android.ui.screen.home.ContactsViewModel;
import com.chatapp.android.ui.screen.home.ContactsViewModel_HiltModules;
import com.chatapp.android.ui.screen.home.HomeViewModel;
import com.chatapp.android.ui.screen.home.HomeViewModel_HiltModules;
import com.chatapp.android.ui.screen.splash.SplashViewModel;
import com.chatapp.android.ui.screen.splash.SplashViewModel_HiltModules;
import com.chatapp.android.util.TokenManager;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerChatApplication_HiltComponents_SingletonC {
  private DaggerChatApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ChatApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ChatApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ChatApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ChatApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ChatApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ChatApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ChatApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ChatApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ChatApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ChatApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ChatApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ChatApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ChatApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_chat_ChatViewModel, ChatViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_home_ContactsViewModel, ContactsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_group_CreateGroupViewModel, CreateGroupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_OtpViewModel, OtpViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_PhoneViewModel, PhoneViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_ProfileSetupViewModel, ProfileSetupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_splash_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_chatapp_android_ui_screen_home_ContactsViewModel = "com.chatapp.android.ui.screen.home.ContactsViewModel";

      static String com_chatapp_android_ui_screen_splash_SplashViewModel = "com.chatapp.android.ui.screen.splash.SplashViewModel";

      static String com_chatapp_android_ui_screen_home_HomeViewModel = "com.chatapp.android.ui.screen.home.HomeViewModel";

      static String com_chatapp_android_ui_screen_auth_PhoneViewModel = "com.chatapp.android.ui.screen.auth.PhoneViewModel";

      static String com_chatapp_android_ui_screen_chat_ChatViewModel = "com.chatapp.android.ui.screen.chat.ChatViewModel";

      static String com_chatapp_android_ui_screen_auth_OtpViewModel = "com.chatapp.android.ui.screen.auth.OtpViewModel";

      static String com_chatapp_android_ui_screen_group_CreateGroupViewModel = "com.chatapp.android.ui.screen.group.CreateGroupViewModel";

      static String com_chatapp_android_ui_screen_auth_ProfileSetupViewModel = "com.chatapp.android.ui.screen.auth.ProfileSetupViewModel";

      @KeepFieldType
      ContactsViewModel com_chatapp_android_ui_screen_home_ContactsViewModel2;

      @KeepFieldType
      SplashViewModel com_chatapp_android_ui_screen_splash_SplashViewModel2;

      @KeepFieldType
      HomeViewModel com_chatapp_android_ui_screen_home_HomeViewModel2;

      @KeepFieldType
      PhoneViewModel com_chatapp_android_ui_screen_auth_PhoneViewModel2;

      @KeepFieldType
      ChatViewModel com_chatapp_android_ui_screen_chat_ChatViewModel2;

      @KeepFieldType
      OtpViewModel com_chatapp_android_ui_screen_auth_OtpViewModel2;

      @KeepFieldType
      CreateGroupViewModel com_chatapp_android_ui_screen_group_CreateGroupViewModel2;

      @KeepFieldType
      ProfileSetupViewModel com_chatapp_android_ui_screen_auth_ProfileSetupViewModel2;
    }
  }

  private static final class ViewModelCImpl extends ChatApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ChatViewModel> chatViewModelProvider;

    private Provider<ContactsViewModel> contactsViewModelProvider;

    private Provider<CreateGroupViewModel> createGroupViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<OtpViewModel> otpViewModelProvider;

    private Provider<PhoneViewModel> phoneViewModelProvider;

    private Provider<ProfileSetupViewModel> profileSetupViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.chatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.contactsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.createGroupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.otpViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.phoneViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.profileSetupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_chat_ChatViewModel, ((Provider) chatViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_home_ContactsViewModel, ((Provider) contactsViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_group_CreateGroupViewModel, ((Provider) createGroupViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_OtpViewModel, ((Provider) otpViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_PhoneViewModel, ((Provider) phoneViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_auth_ProfileSetupViewModel, ((Provider) profileSetupViewModelProvider)).put(LazyClassKeyProvider.com_chatapp_android_ui_screen_splash_SplashViewModel, ((Provider) splashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_chatapp_android_ui_screen_home_ContactsViewModel = "com.chatapp.android.ui.screen.home.ContactsViewModel";

      static String com_chatapp_android_ui_screen_auth_PhoneViewModel = "com.chatapp.android.ui.screen.auth.PhoneViewModel";

      static String com_chatapp_android_ui_screen_auth_OtpViewModel = "com.chatapp.android.ui.screen.auth.OtpViewModel";

      static String com_chatapp_android_ui_screen_chat_ChatViewModel = "com.chatapp.android.ui.screen.chat.ChatViewModel";

      static String com_chatapp_android_ui_screen_group_CreateGroupViewModel = "com.chatapp.android.ui.screen.group.CreateGroupViewModel";

      static String com_chatapp_android_ui_screen_splash_SplashViewModel = "com.chatapp.android.ui.screen.splash.SplashViewModel";

      static String com_chatapp_android_ui_screen_home_HomeViewModel = "com.chatapp.android.ui.screen.home.HomeViewModel";

      static String com_chatapp_android_ui_screen_auth_ProfileSetupViewModel = "com.chatapp.android.ui.screen.auth.ProfileSetupViewModel";

      @KeepFieldType
      ContactsViewModel com_chatapp_android_ui_screen_home_ContactsViewModel2;

      @KeepFieldType
      PhoneViewModel com_chatapp_android_ui_screen_auth_PhoneViewModel2;

      @KeepFieldType
      OtpViewModel com_chatapp_android_ui_screen_auth_OtpViewModel2;

      @KeepFieldType
      ChatViewModel com_chatapp_android_ui_screen_chat_ChatViewModel2;

      @KeepFieldType
      CreateGroupViewModel com_chatapp_android_ui_screen_group_CreateGroupViewModel2;

      @KeepFieldType
      SplashViewModel com_chatapp_android_ui_screen_splash_SplashViewModel2;

      @KeepFieldType
      HomeViewModel com_chatapp_android_ui_screen_home_HomeViewModel2;

      @KeepFieldType
      ProfileSetupViewModel com_chatapp_android_ui_screen_auth_ProfileSetupViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.chatapp.android.ui.screen.chat.ChatViewModel 
          return (T) new ChatViewModel(singletonCImpl.messageDao(), singletonCImpl.chatDao(), singletonCImpl.stompWebSocketClientProvider.get(), singletonCImpl.tokenManagerProvider.get());

          case 1: // com.chatapp.android.ui.screen.home.ContactsViewModel 
          return (T) new ContactsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideUserApiProvider.get(), singletonCImpl.provideChatApiProvider.get());

          case 2: // com.chatapp.android.ui.screen.group.CreateGroupViewModel 
          return (T) new CreateGroupViewModel(singletonCImpl.provideChatApiProvider.get());

          case 3: // com.chatapp.android.ui.screen.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.chatDao(), singletonCImpl.provideChatApiProvider.get());

          case 4: // com.chatapp.android.ui.screen.auth.OtpViewModel 
          return (T) new OtpViewModel(singletonCImpl.provideAuthApiProvider.get(), singletonCImpl.tokenManagerProvider.get());

          case 5: // com.chatapp.android.ui.screen.auth.PhoneViewModel 
          return (T) new PhoneViewModel(singletonCImpl.provideAuthApiProvider.get(), singletonCImpl.tokenManagerProvider.get());

          case 6: // com.chatapp.android.ui.screen.auth.ProfileSetupViewModel 
          return (T) new ProfileSetupViewModel(singletonCImpl.provideUserApiProvider.get());

          case 7: // com.chatapp.android.ui.screen.splash.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.tokenManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ChatApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ChatApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectChatFirebaseMessagingService(
        ChatFirebaseMessagingService chatFirebaseMessagingService) {
      injectChatFirebaseMessagingService2(chatFirebaseMessagingService);
    }

    @CanIgnoreReturnValue
    private ChatFirebaseMessagingService injectChatFirebaseMessagingService2(
        ChatFirebaseMessagingService instance) {
      ChatFirebaseMessagingService_MembersInjector.injectUserApi(instance, singletonCImpl.provideUserApiProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends ChatApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<TokenManager> tokenManagerProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<StompWebSocketClient> stompWebSocketClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<UserApi> provideUserApiProvider;

    private Provider<ChatApi> provideChatApiProvider;

    private Provider<AuthApi> provideAuthApiProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private MessageDao messageDao() {
      return DatabaseModule_ProvideMessageDaoFactory.provideMessageDao(provideDatabaseProvider.get());
    }

    private ChatDao chatDao() {
      return DatabaseModule_ProvideChatDaoFactory.provideChatDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 0));
      this.tokenManagerProvider = DoubleCheck.provider(new SwitchingProvider<TokenManager>(singletonCImpl, 2));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.stompWebSocketClientProvider = DoubleCheck.provider(new SwitchingProvider<StompWebSocketClient>(singletonCImpl, 1));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 5));
      this.provideUserApiProvider = DoubleCheck.provider(new SwitchingProvider<UserApi>(singletonCImpl, 4));
      this.provideChatApiProvider = DoubleCheck.provider(new SwitchingProvider<ChatApi>(singletonCImpl, 6));
      this.provideAuthApiProvider = DoubleCheck.provider(new SwitchingProvider<AuthApi>(singletonCImpl, 7));
    }

    @Override
    public void injectChatApplication(ChatApplication chatApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.chatapp.android.data.local.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.chatapp.android.data.websocket.StompWebSocketClient 
          return (T) new StompWebSocketClient(singletonCImpl.tokenManagerProvider.get(), singletonCImpl.provideOkHttpClientProvider.get());

          case 2: // com.chatapp.android.util.TokenManager 
          return (T) new TokenManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.tokenManagerProvider.get());

          case 4: // com.chatapp.android.data.remote.api.UserApi 
          return (T) NetworkModule_ProvideUserApiFactory.provideUserApi(singletonCImpl.provideRetrofitProvider.get());

          case 5: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 6: // com.chatapp.android.data.remote.api.ChatApi 
          return (T) NetworkModule_ProvideChatApiFactory.provideChatApi(singletonCImpl.provideRetrofitProvider.get());

          case 7: // com.chatapp.android.data.remote.api.AuthApi 
          return (T) NetworkModule_ProvideAuthApiFactory.provideAuthApi(singletonCImpl.provideRetrofitProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
