plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.codekendra"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.codekendra"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Network and image
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.picasso:picasso:2.8")


    // UI helpers
    implementation("io.getstream:photoview:1.0.3")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
//
    // Image cropping
    implementation("com.github.CanHub:Android-Image-Cropper:4.3.2")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Material Design and CircleImageView
    implementation("com.google.android.material:material:1.9.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Add these if not already present
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}