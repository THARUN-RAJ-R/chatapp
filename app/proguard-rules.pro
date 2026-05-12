# Keep data classes for Gson
-keepclassmembers class com.chatapp.android.data.** { *; }
-keepclassmembers class com.chatapp.android.domain.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# OkHttp / Retrofit
-dontwarn okhttp3.**
-dontwarn retrofit2.**
