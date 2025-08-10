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
package com.mshdabiola.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to set versionName and versionCode in gradle/libs.versions.toml and update CHANGELOG.md.
 */
abstract class SetVersionFromTagTask : DefaultTask() {

    @get:Input
    abstract val newVersionName: Property<String>

    @get:InputFile
    abstract val libsVersionsTomlFile: RegularFileProperty

    @get:InputFile // Added for the changelog
    abstract val changelogFile: RegularFileProperty

    @get:OutputFile
    abstract val outputLibsVersionsTomlFile: RegularFileProperty // Typically the same file for in-place updates

    @TaskAction
    fun setVersion() {
        val tomlFile = libsVersionsTomlFile.asFile.get()
        val versionGet = newVersionName.get()
        val versionNameToSet = if (versionGet.isNotEmpty() && versionGet[0].isLetter()) {
            versionGet.substring(1) // Remove the first character if it's an alphabet
        } else {
            versionGet // Otherwise, keep it as is
        }
        val versionCodeToSet = versionStringToNumber(versionNameToSet)

        println("Setting versionName to: $versionNameToSet")
        println("Setting versionCode to: $versionCodeToSet")

        // Read all lines from the TOML file
        val lines = tomlFile.readLines()
        val updatedLines = mutableListOf<String>()

        // Process lines to update versionName and versionCode
        for (line in lines) {
            var modifiedLine = line

            // 1. Update versionName
            val versionNameRegex = """(versionName\s*=\s*")[^"]+(")""".toRegex()
            if (line.contains("versionName = ") && versionNameRegex.containsMatchIn(line)) {
                modifiedLine = versionNameRegex.replace(line) { matchResult ->
                    val (prefix, suffix) = matchResult.destructured
                    "$prefix$versionNameToSet$suffix"
                }
                println("Updated versionName line: '$line' -> '$modifiedLine'")
            }

            // 2. Update versionCode
            // This regex needs to handle both `versionCode = "123"` and `versionCode = 123`
            val versionCodeRegex = """(versionCode\s*=\s*)(["']?)\d+\2""".toRegex()
            if (versionCodeRegex.containsMatchIn(modifiedLine)) {
                modifiedLine = versionCodeRegex.replace(modifiedLine) { matchResult ->
                    val (prefix, quote) = matchResult.destructured
                    "$prefix$quote$versionCodeToSet$quote"
                }
                println("Updated versionCode line: '$line' -> '$modifiedLine'")
            }
            updatedLines.add(modifiedLine)
        }

        // Write the updated lines back to the file
        tomlFile.writeText(updatedLines.joinToString("\n"))
        println("Successfully updated ${tomlFile.name}.")

        // Update changelog
        updateChangelog(versionNameToSet)
    }

    private fun updateChangelog(newVersion: String) {
        val changelog = changelogFile.asFile.get()
        val lines = changelog.readLines().toMutableList()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val unreleasedHeaderIndex = lines.indexOfFirst { it.trim() == "## [Unreleased]" }
        if (unreleasedHeaderIndex != -1) {
            lines[unreleasedHeaderIndex] = "## [$newVersion] - $currentDate"
        }

        val unreleasedLinkRegex = """^[Unreleased]: (https://github.com/mshdabiola/kltemplate/compare/)([^.]+)...HEAD$""".toRegex()
        val newUnreleasedLink = "[Unreleased]: https://github.com/mshdabiola/kltemplate/compare/$newVersion...HEAD"
        val newVersionLink = "[$newVersion]: https://github.com/mshdabiola/kltemplate/compare/$newVersion"

        var unreleasedLinkDefinitionFound = false
        for (i in lines.indices) {
            if (unreleasedLinkRegex.matches(lines[i])) {
                // lines[i] = unreleasedLinkRegex.replace(lines[i]) { matchResult ->
                //     val (baseUrl, _) = matchResult.destructured
                //     "${baseUrl}$newVersion...HEAD" // This line is actually for the *new* unreleased link
                // }
                // Insert the new version link before the updated unreleased link definition
                lines.add(i, newVersionLink)
                // Then, update the old unreleased link to point from the new version to HEAD
                lines[i+1] = newUnreleasedLink // i+1 because we just added an element
                unreleasedLinkDefinitionFound = true
                break
            }
        }

        // If the [Unreleased]: ... link definition was not found using the regex,
        // we try to find it by its prefix and add the new links.
        // This handles cases where the old version format in the link might vary.
        if (!unreleasedLinkDefinitionFound) {
            val unreleasedLinkDefinitionIndex = lines.indexOfFirst { it.startsWith("[Unreleased]:") }
            if (unreleasedLinkDefinitionIndex != -1) {
                lines.add(unreleasedLinkDefinitionIndex, newVersionLink)
                lines[unreleasedLinkDefinitionIndex + 1] = newUnreleasedLink // Update the old one
            } else {
                // If no [Unreleased] link definition exists at all, add both.
                lines.add(newVersionLink)
                lines.add(newUnreleasedLink)
            }
        }


        // Ensure there's a blank line after the "## [version] - date" line and before "### Added" or other content
        if (unreleasedHeaderIndex != -1 && unreleasedHeaderIndex + 1 < lines.size && lines[unreleasedHeaderIndex + 1].isNotBlank()) {
            lines.add(unreleasedHeaderIndex + 1, "")
        }

        // Ensure "## [Unreleased]" header exists for the next iteration, below the new version header.
        // It should be placed after the newly added version's header and its potential blank line.
        val newUnreleasedHeaderIndex = if (unreleasedHeaderIndex != -1) unreleasedHeaderIndex + 2 else 0 // Adjust index based on blank line
        // Check if "## [Unreleased]" already exists immediately after the new version section
        if (newUnreleasedHeaderIndex >= lines.size || lines[newUnreleasedHeaderIndex].trim() != "## [Unreleased]") {
             if (unreleasedHeaderIndex != -1) { // Only add if we successfully replaced the old [Unreleased] header
                // Insert "## [Unreleased]" and a blank line for its link definition
                lines.add(newUnreleasedHeaderIndex, "## [Unreleased]")
                // Also add a blank line after it for content, if not already blank
                 if (newUnreleasedHeaderIndex + 1 >= lines.size || lines[newUnreleasedHeaderIndex + 1].isNotBlank()) {
                    lines.add(newUnreleasedHeaderIndex + 1, "")
                }
            }
        }


        changelog.writeText(lines.joinToString("\n"))
        println("Successfully updated ${changelog.name} with version $newVersion.")
    }

    private fun versionStringToNumber(versionString: String): Long {
        // Remove all non-digit characters (like dots)
        var numericString = versionString.replace(".", "")

        if (numericString.contains("-")) {
            numericString = numericString.split("-")[0]
        }
        // Convert the resulting string to an integer
        return numericString.toLongOrNull() ?: 1
    }
}
