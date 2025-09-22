import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.carlosribeiro.tempo_wt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.carlosribeiro.tempo_wt"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "WEATHER_API_KEY", "\"bb6ecc2665b7996900f60174b6731200\"")
    }

    signingConfigs {
        create("release") {
            val keystorePath: String? =
                project.findProperty("MYAPP_KEYSTORE") as String? ?: System.getenv("MYAPP_KEYSTORE")

            if (keystorePath != null) {
                storeFile = file(keystorePath)
            } else {
                println("⚠️ Nenhum keystore encontrado (CI precisa do secret MYAPP_KEYSTORE)")
            }

            storePassword = project.findProperty("MYAPP_KEYSTORE_PASSWORD") as String?
                ?: System.getenv("MYAPP_KEYSTORE_PASSWORD")

            keyAlias = project.findProperty("MYAPP_KEY_ALIAS") as String?
                ?: System.getenv("MYAPP_KEY_ALIAS")

            keyPassword = project.findProperty("MYAPP_KEY_PASSWORD") as String?
                ?: System.getenv("MYAPP_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 🔑 Garante que o release sempre use a assinatura configurada acima
            signingConfig = signingConfigs.getByName("release")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // OkHttp + logging
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coroutines + ViewModel
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.coil)

    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Testes unitários
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Instrumentation tests (Espresso)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Para rodar testes em Activities/Fragments
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // Truth (asserts mais legíveis)
    androidTestImplementation("com.google.truth:truth:1.1.5")
}
