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