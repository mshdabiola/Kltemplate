/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("mshdabiola.android.application")
    id("mshdabiola.android.application.compose")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.baselineprofile)

}

group = "com.hobit.sample"
version = libs.versions.versionName.get()

dependencies {

    baselineProfile(projects.benchmarks)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.profileinstaller)


}

kotlin {
    sourceSets {

        commonMain.dependencies {
            implementation(projects.library)

            implementation(compose.components.resources)


        }


        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }


    }
}


android {

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")

    namespace = "com.hobit.sample"
    defaultConfig {
        applicationId = "com.hobit.sample"
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = System.getenv("VERSION_NAME") ?: libs.versions.versionName.get()

        // Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

compose.desktop {
    application {
        mainClass = "com.hobit.sample.MainAppKt"


        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
            obfuscate.set(true)
            version.set("7.4.2")
        }

    }


}

configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}


configurations.configureEach {
    exclude("androidx.window.core", "window-core")
}


dependencyGuard {
    configuration("androidDebugCompileClasspath")


}
