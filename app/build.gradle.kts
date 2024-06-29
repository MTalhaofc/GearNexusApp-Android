plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
}

android {
    compileSdk = 34
    namespace = "com.project.gearnexus"


    // Other configurations

    defaultConfig {
        applicationId = "com.project.gearnexus"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.airbnb.android:lottie:6.4.1")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:28.4.2"))
    implementation("com.google.firebase:firebase-database-ktx")
        implementation ("com.google.firebase:firebase-database:20.0.5")
        implementation ("com.google.firebase:firebase-storage:20.0.1")
        implementation ("com.google.firebase:firebase-auth:21.0.1")
        implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.google.android.material:material:1.4.0")


    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("androidx.activity:activity-ktx:1.3.1")
    implementation ("androidx.fragment:fragment-ktx:1.3.6")
    implementation ("com.airbnb.android:lottie:4.1.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)// Replace `libs` with your actual library dependencies block if you have defined it elsewhere

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Replace `libs` with your actual library dependencies block if you have defined it elsewhere
