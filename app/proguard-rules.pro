# ProGuard/R8 rules untuk Majopay Gateway.
#
# Library berikut sudah membawa consumer rules sendiri sehingga tidak perlu diulang:
# Retrofit, OkHttp, Room, Hilt, Kotlin coroutines, Compose, WorkManager.

# --- Stack trace tetap terbaca (nama file disamarkan, nomor baris dipertahankan) ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Gson ---
# Kita hanya memakai TypeToken<Map<String,String>> (anonymous subclass). R8 harus
# mempertahankan generic signature dan kelas TypeToken agar getType() tidak null.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn sun.misc.**

# --- Retrofit suspend fun: metadata Kotlin untuk Continuation ---
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# --- AndroidX Security Crypto (Tink) ---
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# --- Hilt / Dagger ---
-dontwarn dagger.hilt.internal.**

# --- kotlinx-datetime ---
# Mereferensi anotasi kotlinx.serialization secara opsional; proyek ini tidak memakai
# kotlinx-serialization, jadi cukup diabaikan.
-dontwarn kotlinx.serialization.**
