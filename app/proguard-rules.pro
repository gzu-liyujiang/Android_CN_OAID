#################
#项目自定义混淆配置
#################

# Android支持包
-dontwarn android.**
-keep class android.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep public class * extends androidx.**
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-keep class com.google.android.material.** { *; }
