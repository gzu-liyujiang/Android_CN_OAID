#################
#系统通用混淆配置，参见 android-sdk/tools/proguard/proguard-android-optimize.txt
#################

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/* # 谷歌推荐的混淆算法及过滤器，一般不做更改
-allowaccessmodification # 混淆时是否允许改变作用域
-optimizationpasses 5 # 指定代码的压缩级别
-ignorewarnings # 抑制警告
-verbose # 混淆时是否记录日志（混淆后生产映射文件，包含有类名->混淆后类名的映射关系)
-dontpreverify # 不做预校验，preverify是混淆步骤之一，Android不需要preverify，去掉以便加快混淆速度。
#-dontoptimize # 不优化输入的类文件，需要优化才能过滤代码中的日志
#-dontshrink # 不启用压缩，需要压缩才能移除未用到的资源
-dontusemixedcaseclassnames # 混淆时不使用大小写混合，混淆后的类名为小写
-dontskipnonpubliclibraryclasses # 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers # 指定不去忽略非公共库的类成员

-printseeds proguard/seeds.txt # 未混淆的类和成员
-printusage proguard/unused.txt # 列出从 apk 中删除的代码
-printmapping proguard/mapping.txt # 混淆前后的映射

-keepattributes Exceptions # 解决AGPBI警告
-keepattributes Exceptions,InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable # 抛出异常时保留代码行号
-keepattributes Signature # 保留泛型
-keepattributes *Annotation* # 保留注解
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# 继承自安卓的四大组件不能混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Dialog
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.vending.licensing.ILicensingService
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.view.View

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 所有View的子类及其子类的get、set方法都不进行混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 不混淆资源索引类，以便通过资源名称获取资源
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保持指定规则的方法不被混淆（Android layout 布局文件中为控件配置的onClick方法不能混淆）
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 对于带有回调函数的**Event、**Listener的，不能被混淆
-keepclassmembers class * {
    void *(**Event);
    void *(**Listener);
    void gt**;
}

# 不混淆JS于Java的交互桥梁
-keepattributes *JavascriptInterface*
# 如果你的项目中用到了WebView的复杂操作 ，最好加入，对WebView简单说明下:
# 经过实战检验，做腾讯QQ登录，如果引用他们提供的jar，若不加防止WebChromeClient混淆的代码，OAuth认证无法回调，
# 反编译代码后可看到他们有用到WebChromeClient。
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}

# 保持枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 需要序列化和反序列化的类不能被混淆（注: Java反射用到的类也不能被混淆）
-keep class * implements android.os.Parcelable { *; }
-keep public class * implements java.io.Serializable { *; }
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
