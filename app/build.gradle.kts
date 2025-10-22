plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.eurotax.supplylens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eurotax.supplylens"
        minSdk = 26
        targetSdk = 34
        versionCode = providers.gradleProperty("VERSION_CODE").get().toInt()
        versionName = providers.gradleProperty("VERSION_NAME").get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Signing will be configured via environment variables in CI
            // For local builds, this can be configured in local.properties
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (keystoreFile != null) {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_ALIAS_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Only apply signing config if it's properly configured
            val releaseSigningConfig = signingConfigs.getByName("release")
            if (releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_API_URL", "\"https://api-dev.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_BASE_URL", "\"https://api-mock-token.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_KEY", "\"mock_dev_key\"")
            buildConfigField("String", "ANTHROPIC_API_KEY", "\"\"")
        }
        create("stage") {
            dimension = "environment"
            applicationIdSuffix = ".stage"
            versionNameSuffix = "-stage"
            buildConfigField("String", "BASE_API_URL", "\"https://api-stage.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_BASE_URL", "\"https://api-mock-token.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_KEY", "\"mock_stage_key\"")
            buildConfigField("String", "ANTHROPIC_API_KEY", "\"\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_API_URL", "\"https://api.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_BASE_URL", "\"https://api-token.supplylens.io\"")
            buildConfigField("String", "TOKEN_API_KEY", "\"\"")
            buildConfigField("String", "ANTHROPIC_API_KEY", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = true
        htmlReport = true
        htmlOutput = file("$buildDir/reports/lint-results.html")
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:billing"))
    implementation(project(":core:ai"))
    implementation(project(":feature:search"))
    implementation(project(":feature:token"))
    implementation(project(":feature:watchlist"))
    implementation(project(":feature:trade"))
    implementation(project(":feature:alerts"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:forecast"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/../config/detekt/detekt.yml")
}
