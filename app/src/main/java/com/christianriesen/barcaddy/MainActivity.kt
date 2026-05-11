package com.christianriesen.barcaddy

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.data.Settings
import com.christianriesen.barcaddy.nav.Routes
import com.christianriesen.barcaddy.ui.MainViewModel
import com.christianriesen.barcaddy.ui.components.AddCardSheet
import com.christianriesen.barcaddy.ui.components.BarcaddyFormat
import com.christianriesen.barcaddy.ui.components.CardActionsSheet
import com.christianriesen.barcaddy.ui.components.ConfirmDialog
import com.christianriesen.barcaddy.ui.screens.DisplayScreen
import com.christianriesen.barcaddy.ui.screens.FormScreen
import com.christianriesen.barcaddy.ui.screens.HomeScreen
import com.christianriesen.barcaddy.ui.screens.ReorderScreen
import com.christianriesen.barcaddy.ui.screens.ScanScreen
import com.christianriesen.barcaddy.ui.screens.SettingsScreen
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme
import com.christianriesen.barcaddy.util.CsvIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val app = application as BarcaddyApp

        val startRoute = runBlocking {
            val lastId = app.settingsRepository.snapshot().lastViewedCardId
            if (lastId != null && app.cardRepository.findById(lastId) != null) {
                Routes.display(lastId)
            } else {
                if (lastId != null) app.settingsRepository.setLastViewedCardId(null)
                Routes.Home
            }
        }

        setContent {
            val vm: MainViewModel = viewModel(factory = MainViewModel.factory(app))
            val cards by vm.cardList.collectAsState()
            val settings by vm.settingsState.collectAsState()

            BarcaddyTheme(darkTheme = settings.darkMode) {
                BarcaddyAppContent(vm = vm, cards = cards, settings = settings, startRoute = startRoute)
            }
        }
    }
}

@Composable
private fun BarcaddyAppContent(
    vm: MainViewModel,
    cards: List<Card>,
    settings: Settings,
    startRoute: String,
) {
    val nav = rememberNavController()
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    var addSheetOpen by remember { mutableStateOf(false) }
    var actionsForId by remember { mutableStateOf<String?>(null) }
    var pendingDelete by remember { mutableStateOf<Card?>(null) }
    var clearAllPrompt by remember { mutableStateOf(false) }
    var seedDraft by remember { mutableStateOf<Pair<String, BarcaddyFormat>?>(null) }

    val pickCsv = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val imported = withContext(Dispatchers.IO) {
                    activity.contentResolver.openInputStream(uri)?.use { CsvIO.import(it) }
                }
                val list = imported.orEmpty()
                if (list.isEmpty()) {
                    Toast.makeText(activity, "No cards found in CSV", Toast.LENGTH_SHORT).show()
                } else {
                    val merged = vm.snapshot() + list
                    vm.replaceAll(merged)
                    Toast.makeText(activity, "Imported ${list.size} cards", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val createCsv = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    activity.contentResolver.openOutputStream(uri)?.use { out ->
                        CsvIO.export(vm.snapshot(), out)
                    }
                }
                Toast.makeText(activity, "Exported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(nav) {
        nav.currentBackStackEntryFlow.collect { entry ->
            val route = entry.destination.route ?: return@collect
            if (route == Routes.DisplayPattern) {
                val id = entry.arguments?.getString("id")
                vm.setLastViewedCardId(id)
            } else {
                vm.setLastViewedCardId(null)
            }
        }
    }

    val navAnimSpec = tween<Float>(durationMillis = 350)
    NavHost(
        navController = nav,
        startDestination = startRoute,
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        enterTransition = { fadeIn(animationSpec = navAnimSpec) },
        exitTransition = { fadeOut(animationSpec = navAnimSpec) },
        popEnterTransition = { fadeIn(animationSpec = navAnimSpec) },
        popExitTransition = { fadeOut(animationSpec = navAnimSpec) },
    ) {
        composable(Routes.Home) {
            HomeScreen(
                cards = cards,
                onOpen = { id -> nav.navigate(Routes.display(id)) },
                onMore = { id -> actionsForId = id },
                onAdd = { addSheetOpen = true },
                onSettings = { nav.navigate(Routes.Settings) },
                onReorder = { nav.navigate(Routes.Reorder) },
            )
        }

        composable(Routes.Scan) {
            ScanScreen(
                onClose = { nav.popBackStack() },
                onCaptured = { value, format ->
                    seedDraft = value to format
                    nav.popBackStack()
                    nav.navigate(Routes.form(Routes.FormNew))
                },
            )
        }

        composable(Routes.FormPattern) { backStack ->
            val id = backStack.arguments?.getString("id") ?: Routes.FormNew
            var existing by remember { mutableStateOf<Card?>(null) }
            var loaded by remember { mutableStateOf(false) }
            LaunchedEffect(id) {
                existing = if (id == Routes.FormNew) null else vm.findCard(id)
                loaded = true
            }
            if (loaded) {
                val seed = seedDraft.takeIf { id == Routes.FormNew }
                FormScreen(
                    existing = existing,
                    seedValue = seed?.first,
                    seedFormat = seed?.second,
                    suggestions = remember(cards) {
                        val baseline = listOf("Loyalty card", "Coupon", "Membership", "Gift card", "Library card")
                        (baseline + cards.map { it.description }.filter { it.isNotBlank() })
                            .distinct()
                    },
                    onCancel = {
                        seedDraft = null
                        nav.popBackStack()
                    },
                    onSave = { card ->
                        vm.saveCard(card)
                        seedDraft = null
                        nav.popBackStack()
                    },
                    onDelete = { delId ->
                        vm.deleteCard(delId)
                        nav.popBackStack()
                    },
                )
            }
        }

        composable(Routes.DisplayPattern) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val card = cards.firstOrNull { it.id == id }
            if (card != null) {
                DisplayScreen(
                    card = card,
                    showCodeValue = settings.showCodeValue,
                    keepAwake = settings.keepAwake,
                    boostBrightness = settings.boostBrightness,
                    onBack = {
                        if (!nav.popBackStack()) {
                            nav.navigate(Routes.Home) {
                                popUpTo(Routes.DisplayPattern) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }
        }

        composable(Routes.Reorder) {
            ReorderScreen(
                initialCards = cards,
                onCommit = { ids -> vm.reorder(ids) },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                settings = settings,
                onToggleDarkMode = vm::setDarkMode,
                onToggleKeepAwake = vm::setKeepAwake,
                onToggleBoostBrightness = vm::setBoostBrightness,
                onToggleShowCodeValue = vm::setShowCodeValue,
                onExport = {
                    createCsv.launch("barcaddy-cards.csv")
                },
                onImport = {
                    pickCsv.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*"))
                },
                onClearAll = { clearAllPrompt = true },
                onBack = { nav.popBackStack() },
            )
        }
    }

    if (addSheetOpen) {
        AddCardSheet(
            onClose = { addSheetOpen = false },
            onScan = { nav.navigate(Routes.Scan) },
            onManual = { nav.navigate(Routes.form(Routes.FormNew)) },
        )
    }

    val actionCard = actionsForId?.let { id -> cards.firstOrNull { it.id == id } }
    if (actionCard != null) {
        CardActionsSheet(
            cardName = actionCard.name,
            onClose = { actionsForId = null },
            onShow = { nav.navigate(Routes.display(actionCard.id)) },
            onEdit = { nav.navigate(Routes.form(actionCard.id)) },
            onDelete = { pendingDelete = actionCard },
        )
    }

    pendingDelete?.let { card ->
        ConfirmDialog(
            title = "Delete ${card.name}?",
            body = "This card will be removed from this device. This can't be undone.",
            confirmLabel = "Delete",
            danger = true,
            onCancel = { pendingDelete = null },
            onConfirm = {
                vm.deleteCard(card.id)
                pendingDelete = null
            },
        )
    }

    if (clearAllPrompt) {
        ConfirmDialog(
            title = "Clear all cards?",
            body = "This will permanently delete all ${cards.size} cards from this device. This can't be undone.",
            confirmLabel = "Delete everything",
            danger = true,
            onCancel = { clearAllPrompt = false },
            onConfirm = {
                vm.deleteAll()
                clearAllPrompt = false
            },
        )
    }
}
