# ====================================================================
# Rapid Quiz — Production ProGuard & R8 Obfuscation Rules
# ====================================================================

# 1. Keep Data Transfer Objects (DTOs) and Serialized Models
-keep class com.busraunal.rapidquiz.data.model.** { *; }
-keepclassmembers class com.busraunal.rapidquiz.data.model.** { *; }

# 2. Gson Serialization Rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keepclassmembers enum * { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 3. Retrofit 2 Rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# 4. OkHttp 3 Rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# 5. Kotlin Coroutines Rules
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# 6. Android Media3 / ExoPlayer Rules
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# 7. Jetpack Compose Rules
-keep class androidx.compose.** { *; }
