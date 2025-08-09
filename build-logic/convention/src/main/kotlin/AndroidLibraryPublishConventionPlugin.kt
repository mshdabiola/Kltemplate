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
import com.mshdabiola.app.libs
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials

class AndroidLibraryPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
                apply("org.jetbrains.dokka")
            }
            extensions.configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "githubPackages"
                        url = uri("https://maven.pkg.github.com/mshdabiola/kltemplate")
                        credentials(PasswordCredentials::class)
                    }
                }
            }
            extensions.configure<MavenPublishBaseExtension> {
                // Define coordinates for the published artifact
                coordinates(
                    groupId = libs.findVersion("groupId").get().toString(),
                    version = libs.findVersion("versionName").get().toString(),
                )

                // Configure POM metadata for the published artifact
                pom {
                    inceptionYear.set("2025")
                    url.set("https://github.com/mshdabiola/kltemplate")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    // Specify developers information
                    developers {
                        developer {
                            id.set("mshdabiola")
                            name.set("Lawal abiola")
                            email.set("mshdabiola@gmail.com")
                        }
                    }

                    // Specify SCM information
                    scm {
                        url.set("https://github.com/mshdabiola/kltemplate")
                    }
                }

                // Configure publishing to Maven Central
                publishToMavenCentral(automaticRelease = true)

                // Enable GPG signing for all publications
                signAllPublications()
            }
        }
    }
}
