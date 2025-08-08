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
package com.hobit.sample.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag // Ensure this import is present
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.mshdabiola.designsystem.drawable.SamIcons
import com.mshdabiola.designsystem.strings.SamStrings
import com.mshdabiola.detail.navigation.Detail
import com.mshdabiola.detail.navigation.navigateToDetail
import com.hobit.sample.app.generated.resources.Res
import com.hobit.sample.app.generated.resources.add_content_description
import com.hobit.sample.app.generated.resources.brand_content_description
import com.hobit.sample.app.generated.resources.fab_add_note_text
import com.hobit.sample.app.generated.resources.home_label
import com.hobit.sample.app.generated.resources.rail_action_collapse
import com.hobit.sample.app.generated.resources.rail_action_expand
import com.hobit.sample.app.generated.resources.rail_state_collapsed
import com.hobit.sample.app.generated.resources.rail_state_expanded
import com.hobit.sample.app.generated.resources.settings_label
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.model.testtag.SamScaffoldTestTags
import com.mshdabiola.setting.navigation.Setting
import com.mshdabiola.ui.LocalSharedTransitionScope
import com.mshdabiola.ui.SharedTransitionContainer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SamScaffold(
    modifier: Modifier = Modifier,
    appState: SamAppState,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val sharedScope = LocalSharedTransitionScope.current

    val topDestination = remember {
        setOf(
            TopLevelRoute(
                route = Main,
                selectedIcon = SamIcons.Home,
                unSelectedIcon = SamIcons.HomeOutlined,
                label = Res.string.home_label,
            ),
            TopLevelRoute(
                route = Setting,
                selectedIcon = SamIcons.Settings,
                unSelectedIcon = SamIcons.SettingsOutlined,
                label = Res.string.settings_label,
            ),

        )
    }
    val currentDestination = appState.navController
        .currentBackStackEntryAsState().value?.destination
    val isMain = remember(currentDestination) {
        currentDestination?.hasRoute(Main::class) == true
    }
    val isTopDestination = remember(currentDestination) {
        topDestination.any {
            currentDestination
                ?.hasRoute(it.route::class)
                ?: false
        }
    }

    with(sharedScope) {
        if (appState is Compact) {
            ModalNavigationDrawer(
                modifier = modifier.testTag(SamScaffoldTestTags.MODAL_NAVIGATION_DRAWER),
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .width(300.dp)
                            .testTag(SamScaffoldTestTags.MODAL_DRAWER_SHEET),
                        drawerState = appState.drawerState,
                    ) {
                        DrawerContent(
                            modifier = Modifier.padding(16.dp),
                            appState = appState,
                            isMain = isMain,
                            topDestination = topDestination,
                        )
                    }
                },
                drawerState = appState.drawerState,
                gesturesEnabled = isTopDestination,
            ) {
                Scaffold(
                    modifier = Modifier.testTag(SamScaffoldTestTags.SCAFFOLD_CONTENT_AREA + "_compact"),
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    contentColor = contentColor,
                    topBar = topBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = {
                        AnimatedVisibility(isMain) {
                            Fab(
                                appState = appState,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("note_-1"),
                                        animatedVisibilityScope = this,
                                    ),
                            )
                        }
                    },
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
        } else {
            PermanentNavigationDrawer(
                modifier = modifier.testTag(SamScaffoldTestTags.PERMANENT_NAVIGATION_DRAWER),
                drawerContent = {
                    if (isTopDestination) {
                        if (appState is Medium) {
                            WideNavigationRail(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .testTag(SamScaffoldTestTags.WIDE_NAVIGATION_RAIL),
                                state = appState.wideNavigationRailState,
                                colors = WideNavigationRailDefaults.colors(containerColor = containerColor),
                                header = {
                                    val expand = stringResource(Res.string.rail_state_expanded)
                                    val collapse = stringResource(Res.string.rail_state_collapsed)
                                    IconButton(
                                        modifier = Modifier
                                            .padding(start = 24.dp)
                                            .semantics {
                                                stateDescription =
                                                    if (appState.wideNavigationRailState.currentValue ==
                                                        WideNavigationRailValue.Expanded
                                                    ) {
                                                        expand
                                                    } else {
                                                        collapse
                                                    }
                                            }
                                            .testTag(SamScaffoldTestTags.RAIL_TOGGLE_BUTTON),
                                        onClick = {
                                            if (appState.wideNavigationRailState.targetValue ==
                                                WideNavigationRailValue.Expanded
                                            ) {
                                                appState.collapse()
                                            } else {
                                                appState.expand()
                                            }
                                        },
                                    ) {
                                        if (appState.wideNavigationRailState.targetValue ==
                                            WideNavigationRailValue.Expanded
                                        ) {
                                            Icon(SamIcons.MenuOpen, stringResource(Res.string.rail_action_collapse))
                                        } else {
                                            Icon(SamIcons.Menu, stringResource(Res.string.rail_action_expand))
                                        }
                                    }
                                },
                            ) {
                                DrawerContent(
                                    appState = appState,
                                    isMain = isMain,
                                    topDestination = topDestination,
                                )
                            }
                        }
                        if (appState is Expand) {
                            PermanentDrawerSheet(
                                drawerContainerColor = containerColor,
                                modifier = Modifier
                                    .width(300.dp)
                                    .testTag(SamScaffoldTestTags.PERMANENT_DRAWER_SHEET),
                            ) {
                                DrawerContent(
                                    modifier = Modifier.padding(16.dp),
                                    appState = appState,
                                    isMain = isMain,
                                    topDestination = topDestination,
                                )
                            }
                        }
                    }
                },
            ) {
                Scaffold(
                    modifier = Modifier.testTag(SamScaffoldTestTags.SCAFFOLD_CONTENT_AREA + "_permanent"),
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    contentColor = contentColor,
                    topBar = topBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun SamScaffoldPreview() {
    val navController = rememberNavController().apply {
        graph =
            createGraph(startDestination = Main) {
                composable<Main> { }
                composable<Detail> { }
                composable<Setting> { }
            }
    }
    val appState = Medium(
        navController = navController,
        coroutineScope = rememberCoroutineScope(),
        wideNavigationRailState = rememberWideNavigationRailState(),
    )

    SharedTransitionContainer {
        SamScaffold(appState = appState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text =
                    "Note: This demo is best shown in portrait mode, as landscape mode" +
                        " may result in a compact height in certain devices. For any" +
                        " compact screen dimensions, use a Navigation Bar instead.",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    appState: SamAppState,
    isMain: Boolean,
    topDestination: Set<TopLevelRoute<out Any>>,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .testTag(SamScaffoldTestTags.DrawerContentTestTags.DRAWER_CONTENT_COLUMN),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(appState !is Medium) {
            Row(
                modifier = Modifier.testTag(SamScaffoldTestTags.DrawerContentTestTags.BRAND_ROW),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(SamScaffoldTestTags.DrawerContentTestTags.BRAND_ICON),
                    imageVector = SamIcons.AppIcon,
                    contentDescription = stringResource(Res.string.brand_content_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    SamStrings.brand, // Assuming SamStrings.brand is already a resource or intended to be so.
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag(SamScaffoldTestTags.DrawerContentTestTags.BRAND_TEXT),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
        AnimatedVisibility(appState !is Compact && isMain) {
            val fabModifier = if (appState is Medium) {
                Modifier.padding(start = 24.dp)
            } else {
                Modifier
            }
            Fab(
                modifier = fabModifier, // Modifier for FAB is passed, its internal tags handle specifics
                appState = appState,
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
        topDestination.forEach { item ->

            if (appState is Medium) {
                WideNavigationRailItem(
                    modifier = Modifier.testTag(
                        SamScaffoldTestTags.DrawerContentTestTags.wideNavigationRailItemTag(item.route),
                    ),
                    railExpanded = appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route)) {
                                item.selectedIcon
                            } else {
                                item.unSelectedIcon
                            }
                        Icon(imageVector = imageVector, contentDescription = stringResource(item.label))
                    },
                    label = { Text(stringResource(item.label)) },
                    selected = appState.isInCurrentRoute(item.route),
                    onClick = {
                        appState.navigateTopRoute(item.route)
                    },
                )
            } else {
                NavigationDrawerItem(
                    modifier = Modifier.testTag(
                        SamScaffoldTestTags
                            .DrawerContentTestTags.navigationItemTag(item.route),
                    ),
                    icon = {
                        val imageVector =
                            if (appState.isInCurrentRoute(item.route)) {
                                item.selectedIcon
                            } else {
                                item.unSelectedIcon
                            }
                        Icon(imageVector = imageVector, contentDescription = stringResource(item.label))
                    },
                    label = { Text(stringResource(item.label)) },
                    selected = appState.isInCurrentRoute(item.route),
                    onClick = {
                        appState.navigateTopRoute(item.route)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Fab(
    modifier: Modifier = Modifier, // The passed modifier might already include sharedBounds
    appState: SamAppState,
) {
    AnimatedContent(
        targetState = appState is Medium &&
            appState.wideNavigationRailState.targetValue == WideNavigationRailValue.Collapsed,
        modifier = modifier.testTag(SamScaffoldTestTags.FabTestTags.FAB_ANIMATED_CONTENT),
        // Tag the AnimatedContent wrapper
    ) { isCollapsedMediumFab ->
        if (isCollapsedMediumFab) {
            SmallFloatingActionButton(
                modifier = Modifier.testTag(SamScaffoldTestTags.FabTestTags.SMALL_FAB), // Tag the specific FAB type
                onClick = { appState.navController.navigateToDetail(Detail(-1)) },
            ) {
                Icon(
                    imageVector = SamIcons.Add,
                    contentDescription = stringResource(Res.string.add_content_description),
                    modifier = Modifier.testTag(SamScaffoldTestTags.FabTestTags.FAB_ADD_ICON),
                )
            }
        } else {
            SmallExtendedFloatingActionButton(
                modifier = Modifier.testTag(SamScaffoldTestTags.FabTestTags.EXTENDED_FAB),
                // Tag the specific FAB type
                onClick = { appState.navController.navigateToDetail(Detail(-1)) },
            ) {
                Icon(
                    imageVector = SamIcons.Add,
                    contentDescription = stringResource(Res.string.add_content_description),
                    modifier = Modifier.testTag(SamScaffoldTestTags.FabTestTags.FAB_ADD_ICON),
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    stringResource(Res.string.fab_add_note_text),
                    modifier = Modifier.testTag(SamScaffoldTestTags.FabTestTags.FAB_ADD_TEXT),
                )
            }
        }
    }
}
