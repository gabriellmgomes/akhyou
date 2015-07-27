-keepattributes Signature
-keepattributes *Annotation*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-printmapping

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okio.Okio.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp.internal.huc.**

-keep class com.squareup.picasso.** { *; }
-keepclasseswithmembers class * {
    @com.squareup.picasso.** *;
}
-keepclassmembers class * {
    @com.squareup.picasso.** *;
}


-keepclassmembers class ** {
    public void onEvent*(**);
}

-dontwarn rx.internal.util.**
-dontwarn java.lang.invoke.*

-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep class android.support.v7.widget.SearchView { *; }

-keep public class org.jsoup.** {
    public *;
}

-keep class dulleh.akhyou.Episodes.EpisodesPresenter { *; }