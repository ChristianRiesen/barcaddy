# Keep ZXing reflection-discovered classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }

# Room schemas use reflection
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
