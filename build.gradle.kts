buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.3")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}