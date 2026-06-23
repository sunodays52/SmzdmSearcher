# Add project specific ProGuard rules here.
-keepattributes *Annotation*

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.smzdm.searcher.data.remote.** { *; }
