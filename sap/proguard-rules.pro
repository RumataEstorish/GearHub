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

-keepclassmembers class com.samsung.** { *;}
-keep class com.samsung.** { *; }
-dontwarn com.samsung.**
-keepattributes InnerClasses

-keepclassmembers class gearsoftware.sap.Sap.** { *; }
-keep class gearsoftware.sap.Sap { *; }

#OKhttp
#noinspection ShrinkerUnresolvedReference
-keep class okhttp3.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class kotlin.Metadata { *; }
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#Toothpick 3
-dontwarn javax.inject.**
-dontwarn javax.annotation.**
-keep class javax.inject.**
-keep class javax.annotation.**
-keepclassmembers class * {
	@javax.inject.Inject <init>(...);
	@javax.inject.Inject <init>();
	@javax.inject.Inject <fields>;
	public <init>(...);
}
-keepnames @toothpick.InjectConstructor class *
-keepclasseswithmembernames class * { toothpick.ktp.delegate.* *; }
-keepclassmembers class * {
    toothpick.ktp.delegate.* *;
}
-keep @javax.inject.Qualifier public class *
-dontwarn java.lang.invoke.StringConcatFactory