-target 1.6
-dontobfuscate
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-optimizations !code/simplification/arithmetic

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keep class org.faudroids.** { *; }

-keep class retrofit.** { *; }
-keep class com.google.inject.Binder
-keepclassmembers class * {
    @com.google.inject.Inject <init>(...);
}
-keepclassmembers class * {
    void *(**On*Event);
}

-keep class com.esotericsoftware.** { *; }
-keep class java.beans.** { *; }
-keep class sun.nio.ch.** { *; }


-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn roboguice.**
-dontwarn com.squareup.**
-dontwarn retrofit.**
-dontwarn com.google.**
-dontwarn org.ietf.**
-dontwarn com.jcraft.**
-dontwarn org.slf4j.**
-dontwarn okio.BufferedSink
-dontwarn java.lang.invoke.*
-dontwarn java.beans.**
-dontwarn sun.nio.ch.**
-dontwarn sun.misc.**

# required since appcompat v23, see also https://code.google.com/p/android/issues/detail?id=190237
-keep class com.google.android.gms.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
