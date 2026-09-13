import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

// Load webhook configuration from local.properties (gitignored) or environment
// variables. Used to bundle the webhook URL + method + headers into BuildConfig
// so end users cannot change them at runtime.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun resolveConfig(key: String, default: String): String =
    localProperties.getProperty(key) ?: System.getenv(key) ?: default

fun stringField(value: String): String {
    val escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
}

android {
    namespace = "id.majopay.ngateway"
    compileSdk = 36

    defaultConfig {
        applicationId = "id.majopay.ngateway"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "WEBHOOK_URL",
            stringField(resolveConfig("WEBHOOK_URL", ""))
        )
        buildConfigField(
            "String",
            "WEBHOOK_METHOD",
            stringField(resolveConfig("WEBHOOK_METHOD", "POST"))
        )
        buildConfigField(
            "String",
            "WEBHOOK_HEADERS_JSON",
            stringField(resolveConfig("WEBHOOK_HEADERS_JSON", "{}"))
        )
        // URL kebijakan privasi publik. Wajib sama dengan yang didaftarkan di Play Console.
        buildConfigField(
            "String",
            "PRIVACY_POLICY_URL",
            stringField(resolveConfig("PRIVACY_POLICY_URL", "https://majopay.id/gateway/privacy"))
        )
    }

    buildTypes {
        release {
            // R8: shrink + obfuscate. Menaikkan biaya reverse engineering, bukan mencegahnya.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        // android.util.Log dipanggil di use case; kembalikan nilai default di JVM unit test.
        unitTests.isReturnDefaultValues = true
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        warningsAsErrors = false
        checkReleaseBuilds = false
        // BIND_NOTIFICATION_LISTENER_SERVICE memang signature-level; diberikan user lewat
        // Settings > Akses notifikasi. QUERY_ALL_PACKAGES sudah dihapus (kebijakan Play).
        disable += setOf("ProtectedPermissions")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

// KSP configuration
ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
    arg("room.incremental", "true")
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM and UI dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Lifecycle and ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room Database
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.room:room-paging:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // Paging
    implementation("androidx.paging:paging-runtime:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Date and time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // Encrypted storage for API credentials
    implementation(libs.androidx.security.crypto)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.room:room-testing:2.7.2")
    testImplementation("androidx.work:work-testing:2.9.0")
    
    // Android Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    
    // Debug dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}