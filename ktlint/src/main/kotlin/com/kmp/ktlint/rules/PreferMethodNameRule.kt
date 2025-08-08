/*
 * Copyright (C) 2025 MshdAbiola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.kmp.ktlint.rules

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtCallExpression

class PreferMethodNameRule : Rule(
    RuleId("sample:prefer-method-name"),
    about =
    Rule.About(
        maintainer = "Your Name",
        repositoryUrl = "https://github.com/mshdabiola/sample",
        issueTrackerUrl = "https://github.com/mshdabiola/sample",
    ),
) {
    // Define the mapping of deprecated method names to preferred method names
    private val methodNameReplacements =
        mapOf(
            "MaterialTheme" to "SamTheme",
            "Button" to "SamButton",
            "OutlinedButton" to "SamOutlinedButton",
            "TextButton" to "SamTextButton",
            "FilterChip" to "SamFilterChip",
            "ElevatedFilterChip" to "SamFilterChip",
            "NavigationBar" to "SamNavigationBar",
            "NavigationBarItem" to "SamNavigationBarItem",
            "NavigationRail" to "SamNavigationRail",
            "NavigationRailItem" to "SamNavigationRailItem",
            "TabRow" to "SamTabRow",
            "Tab" to "SamTab",
            "IconToggleButton" to "SamIconToggleButton",
            "FilledIconToggleButton" to "SamIconToggleButton",
            "FilledTonalIconToggleButton" to "SamIconToggleButton",
            "OutlinedIconToggleButton" to "SamIconToggleButton",
            "CenterAlignedTopAppBar" to "SamTopAppBar",
            "SmallTopAppBar" to "SamTopAppBar",
            "MediumTopAppBar" to "SamTopAppBar",
            "LargeTopAppBar" to "SamTopAppBar",
        )

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeFixed: Boolean) -> Unit,
    ) {
        // Check if the current node is a method call expression
        if (node.psi is KtCallExpression) {
            val callExpression = node.psi as KtCallExpression
            val methodName = callExpression.calleeExpression?.text

            // Check if the method name is in our mapping
            if (methodName != null && methodNameReplacements.containsKey(methodName)) {
                val preferredName = methodNameReplacements[methodName]
                val errorMessage =
                    "Using '$methodName' instead of '$preferredName'. Consider using '$preferredName'."

                // Report the violation
                emit(
                    node.startOffset,
                    errorMessage,
                    false,
                )
            }
        }
    }
}
