import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zhenxiang.superimage"
    compileSdk = 33

    val changelogFileName = "changelog.txt"

    defaultConfig {
        applicationId = "com.zhenxiang.superimage"
        minSdk = 24
        targetSdk = 33
        versionCode = 121
        versionName = "1.2.1"

        buildConfigField("String", "CHANGELOG_ASSET_NAME", "\"$changelogFileName\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Copy changelog for app assets
    val copiedChangelogPath = File(buildDir, "generated/changelogAsset")
    val copyArtifactsTask = tasks.register<Copy>("copyChangelog") {
        delete(copiedChangelogPath)
        from(File(rootProject.rootDir, "fastlane/metadata/android/en-US/changelogs/${defaultConfig.versionCode}.txt"))
        into(copiedChangelogPath)
        rename { changelogFileName }
    }
    tasks.preBuild {
        dependsOn(copyArtifactsTask)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }

    flavorDimensions.add("version")

    productFlavors {
        create("free") {
            dimension = "version"
        }
        create("playstore") {
            dimension = "version"
        }
    }

    sourceSets {
        getByName("main") {
            // Add changelog to assets
            assets.srcDirs(copiedChangelogPath)
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":common"))
    "freeImplementation"(project(":playstore:no-op"))
    "playstoreImplementation"(project(":playstore:impl"))
    implementation(project(":realesrgan"))

    val compose_version = "1.3.3"
    val lifecycle_version = "2.6.0-alpha05"
    val koin_android_version= "3.3.2"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.compose.material3:material3:1.1.0-alpha05")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.github.Dimezis:BlurView:version-2.0.3")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.1-alpha")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.insert-koin:koin-android:$koin_android_version")
    implementation("joda-time:joda-time:2.12.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
}