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
package com.mshdabiola.designsystem.component

import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mshdabiola.designsystem.theme.SamTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SamLoading(
    modifier: Modifier = Modifier,
) {
    LoadingIndicator(modifier)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SamContainedLoading(
    modifier: Modifier = Modifier,
) {
    ContainedLoadingIndicator(modifier)
}

@Preview
@Composable
fun SamLoadingPreview() {
    SamTheme {
        Surface {
            SamLoading()
        }
    }
}

@Preview
@Composable
fun SamContainedLoadingPreview() {
    SamTheme {
        Surface {
            SamContainedLoading()
        }
    }
}
