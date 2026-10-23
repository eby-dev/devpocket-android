import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val appwriteProperties = Properties().apply {
    val configFile = rootProject.file("appwrite.properties")
    if (configFile.exists()) configFile.inputStream().use(::load)
}

fun appwriteValue(key: String) = appwriteProperties.getProperty(key, "")

android {
    namespace = "com.ebydev.devpocket"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.ebydev.devpocket"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APPWRITE_ENDPOINT", "\"${appwriteValue("endpoint")}\"")
        buildConfigField("String", "APPWRITE_PROJECT_ID", "\"${appwriteValue("projectId")}\"")
        buildConfigField("String", "APPWRITE_DATABASE_ID", "\"${appwriteValue("databaseId")}\"")
        buildConfigField("String", "APPWRITE_TABLE_ID", "\"${appwriteValue("tableId")}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.appwrite.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.junit)
}
