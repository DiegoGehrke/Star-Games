# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# FirebaseUI auth classes
-keep class com.firebase.ui.auth.** { *; }
-keepnames class com.firebase.ui.auth.** { *; }

# FirebaseUI database classes
-keep class com.firebase.ui.database.** { *; }
-keepnames class com.firebase.ui.database.** { *; }

# FirebaseUI firestore classes
-keep class com.firebase.ui.firestore.** { *; }
-keepnames class com.firebase.ui.firestore.** { *; }

# FirebaseUI storage classes
-keep class com.firebase.ui.storage.** { *; }
-keepnames class com.firebase.ui.storage.** { *; }

# Firebase classes
-keep class com.google.firebase.** { *; }
-keepnames class com.google.firebase.** { *; }

# Google Play Services classes
-keep class com.google.android.gms.** { *; }
-keepnames class com.google.android.gms.** { *; }

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#noinspection ShrinkerUnresolvedReference
#unity
-keep class com.ironsource.unity.androidbridge.** { *;}
-keep class com.google.android.gms.ads.** {public *;}
-keep class com.google.android.gms.appset.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
#adapters
-keep class com.ironsource.adapters.** { *; }
#sdk
-dontwarn com.ironsource.**
-dontwarn com.ironsource.adapters.**
-keepclassmembers class com.ironsource.** { public *; }
-keep public class com.ironsource.**
-keep class com.ironsource.adapters.** { *;
}
-keep class com.apm.insight.CrashType.**
#omid
-dontwarn com.iab.omid.**
-keep class com.iab.omid.** {*;}
#javascript
-keepattributes JavascriptInterface
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keep class com.openmediation.sdk.** { *; }
-keep class com.bytedance.sdk.** { *; }
-keepclassmembers class com.bytedance.sdk.** {
    *;
}
# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# Keep ADCNative class members unobfuscated
-keepclassmembers class com.adcolony.sdk.ADCNative** {
    *;
 }
 -dontwarn com.apm.insight.AttachUserData
 -dontwarn com.apm.insight.CrashType
 -dontwarn com.apm.insight.ICrashCallback
 -dontwarn com.apm.insight.MonitorCrash$Config
 -dontwarn com.apm.insight.MonitorCrash
 -dontwarn com.bykv.vk.openvk.preload.falconx.loader.ILoader
 -dontwarn com.bykv.vk.openvk.preload.falconx.loader.a
 -dontwarn com.bykv.vk.openvk.preload.geckox.GeckoHubImp
 -dontwarn com.bykv.vk.openvk.preload.geckox.buffer.stream.BufferOutputStream
 -dontwarn com.bykv.vk.openvk.preload.geckox.net.INetWork
 -dontwarn com.bykv.vk.openvk.preload.geckox.net.Response
 -dontwarn com.bykv.vk.openvk.preload.geckox.statistic.IStatisticMonitor
 -dontwarn com.bykv.vk.openvk.preload.geckox.utils.CloseableUtils
 -dontwarn com.bytedance.JProtect
 -dontwarn com.bytedance.component.sdk.annotation.ColorInt
 -dontwarn com.bytedance.component.sdk.annotation.RequiresApi
 -dontwarn com.bytedance.component.sdk.annotation.UiThread
 -dontwarn com.bytedance.mobsec.metasec.ov.PglMSConfig$Builder
 -dontwarn com.bytedance.mobsec.metasec.ov.PglMSConfig
 -dontwarn com.bytedance.mobsec.metasec.ov.PglMSManager
 -dontwarn com.bytedance.mobsec.metasec.ov.PglMSManagerUtils
 -dontwarn com.bytedance.sdk.openadsdk.core.model.NetExtParams$RenderType
 -dontwarn com.bytedance.sdk.openadsdk.core.settings.TTSdkSettings$FETCH_REQUEST_SOURCE
 -keepattributes SourceFile,LineNumberTable
 -keep class com.inmobi.** { *; }
 -dontwarn com.inmobi.**
 -keep public class com.google.android.gms.**
 -dontwarn com.google.android.gms.**
 -dontwarn com.squareup.picasso.**
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{
      public *;
 }
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{
      public *;
 }
 # skip the Picasso library classes
 -keep class com.squareup.picasso.** {*;}
 -dontwarn com.squareup.picasso.**
 -dontwarn com.squareup.okhttp.**
 # skip Moat classes
 -keep class com.moat.** {*;}
 -dontwarn com.moat.**
 # skip AVID classes
 -keep class com.integralads.avid.library.* {*;}
 # Vungle
 -keep class com.vungle.warren.** { *; }
 -dontwarn com.vungle.warren.error.VungleError$ErrorCode
 # Moat SDK
 -keep class com.moat.** { *; }
 -dontwarn com.moat.**
 # Okio
 -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 # Retrofit
 -dontwarn okio.**
 -dontwarn retrofit2.Platform$Java8
 # Gson
 -keepattributes Signature
 -keepattributes *Annotation*
 -dontwarn sun.misc.**
 -keep class com.google.gson.examples.android.model.** { *; }
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer
 # Google Android Advertising ID
 -keep class com.google.android.gms.internal.** { *; }
 -dontwarn com.google.android.gms.ads.identifier.**

 -dontwarn com.google.android.exoplayer2.source.rtsp.RtspMessageChannel$MessageParser$ReadingState