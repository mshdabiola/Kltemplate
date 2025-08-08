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
package com.hobit.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.hobit.sample.di.appModule
import com.mshdabiola.model.Platform
import kotlinx.browser.document
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun mainApp() {
    ComposeViewport(document.body!!) {
        SamApp()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val logger =
        Logger(
            loggerConfigInit(
                minSeverity = Severity.Error,
                logWriters = arrayOf(platformLogWriter(DefaultFormatter)),
            ),
        )
    val applicationModule = module {
        single { Platform.Web } bind Platform::class
        single {
            logger
        }
    }

    startKoin {
        modules(
            appModule,
            applicationModule,
        )
    }
    mainApp()
}
