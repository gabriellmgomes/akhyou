-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okio.Okio.**

-keepattributes Signature
-keepattributes *Annotation*

-keep class com.squareup.picasso.** { *; }
-keepclasseswithmembers class * {
    @com.squareup.picasso.** *;
}
-keepclassmembers class * {
    @com.squareup.picasso.** *;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.internal.huc.**