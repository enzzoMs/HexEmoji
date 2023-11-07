plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.20-RC-1.0.13" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.7.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}