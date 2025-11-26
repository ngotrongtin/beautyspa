# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep data classes for JSON parsing
-keep class com.beautyspa.app.data.model.** { *; }

# Coil
-keep class coil.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Uncomment this to preserve the line number information for debugging
-keepattributes SourceFile,LineNumberTable
