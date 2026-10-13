plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pushiote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pushiote"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        viewBinding = true
    }
}

dependencies {
    // AndroidX Base y Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)

    // Volley para peticiones HTTP (AWS y pnk.cl)
    implementation("com.android.volley:volley:1.2.1")

    // SweetAlert para diálogos e interacción
    implementation("com.github.f0ris.sweetalert:library:1.6.2")

    // Lottie para animaciones (Splash y Sensores)
    implementation("com.airbnb.android:lottie:6.6.2")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}