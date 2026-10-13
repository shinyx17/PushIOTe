plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pushiote"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pushiote"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Lottie para la animación del Splash
    implementation("com.airbnb.android:lottie:6.6.2")
    // SweetAlert para mensajes de éxito y error
    implementation("com.github.f0ris.sweetalert:library:1.6.2")
}