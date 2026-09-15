package com.mms.minzmahallu.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mms.minzmahallu.BuildConfig
import com.mms.minzmahallu.MmsApp
import com.mms.minzmahallu.data.model.AuthUser
import com.mms.minzmahallu.data.model.Dest
import com.mms.minzmahallu.data.model.ToastMsg
import com.mms.minzmahallu.i18n.I18n
import com.mms.minzmahallu.ui.components.*
import com.mms.minzmahallu.ui.screens.*
import com.mms.minzmahallu.ui.theme.*
import com.mms.minzmahallu.util.Format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Renders a hand-crafted vector icon tinted (no Material dependency). */
@Composable
fun MmsVec(vec: ImageVector, size: Dp = 22.dp, tint: Color = Color.White, modifier: Modifier = Modifier) {
    Image(
        imageVector = vec,
        contentDescription = null,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
fun MmsRoot(modifier: Modifier = Modifier) {
    val app = MmsApp.instance
    val repo = try { app.repo } catch (e: Throwable) { null }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var splash by remember { mutableStateOf(true) }
    var initError by remember { mutableStateOf<Throwable?>(app.initError) }
    var user by remember { mutableStateOf(try { repo?.auth?.currentUser } catch (_: Exception) { null }) }
    var needsSetup by remember { mutableStateOf(false) }
    var dest by remember { mutableStateOf(Dest.Dashboard) }
    var islandOpen by remember { mutableStateOf(false) }
    var navArg by remember { mutableStateOf("") }
    val toasts = remember { mutableStateListOf<ToastMsg>() }
    val lang by I18n.lang.collectAsState()

    fun toast(msg: String, kind: ToastMsg.Kind = ToastMsg.Kind.Success) {
        toasts += ToastMsg(System.currentTimeMillis(), msg, kind)
    }

    LaunchedEffect(Unit) {
        if (repo == null) {
            initError = app.initError ?: IllegalStateException("Database not initialised")
            kotlinx.coroutines.delay(600)
            splash = false
            return@LaunchedEffect
        }
        try {
            val setup: Boolean
            val s: Map<String, Any?>
            withContext(Dispatchers.IO) {
                setup = try { repo.auth.needsInitialSetup() } catch (e: Exception) {
                    android.util.Log.e("MmsRoot", "needsInitialSetup failed", e)
                    false
                }
                s = try { repo.settingsLoad() } catch (e: Exception) {
                    android.util.Log.e("MmsRoot", "settingsLoad failed", e)
                    emptyMap()
                }
                Format.currencySymbol = Format.str(s, "currency_symbol").ifBlank { "₹" }
            }
            needsSetup = setup
            val theme = Format.str(s, "theme")
            MmsThemeController.setDark(theme == "dark")
            val l = Format.str(s, "language")
            if (l in listOf("en", "ml")) I18n.setLang(ctx, l)
        } catch (e: Exception) {
            android.util.Log.e("MmsRoot", "init failed", e)
            initError = e
            toast(e.message ?: "Init failed", ToastMsg.Kind.Error)
        }
        kotlinx.coroutines.delay(1600)
        splash = false
    }

    val c = C()
    if (repo == null) {
        Box(modifier.background(c.bodyBg)) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.text.BasicText(
                    "Database error",
                    style = MmsType.title.copy(color = c.cRose, fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.foundation.text.BasicText(
                    (initError?.message ?: "Failed to open database").take(300),
                    style = MmsType.bodySm.copy(color = c.mut)
                )
                Spacer(Modifier.height(12.dp))
                androidx.compose.foundation.text.BasicText(
                    "Clear app data and reopen. Settings → Apps → Minz Mahallu → Storage → Clear Data.",
                    style = MmsType.caption.copy(color = c.fnt)
                )
            }
            ToastHost(toasts.toList()) { id -> toasts.removeAll { it.id == id } }
        }
        return
    }

    Box(modifier.background(C().bodyBg)) {
        when {
            splash -> SplashScreen()
            user == null -> LoginScreen(
                needsSetup = needsSetup,
                onLogin = { u, p ->
                    scope.launch {
                        try {
                            val a = withContext(Dispatchers.IO) { repo.auth.login(u, p) }
                            user = a
                            toast(I18n.t("login_button") + " ✓")
                        } catch (e: Exception) {
                            toast(e.message ?: "Login failed", ToastMsg.Kind.Error)
                        }
                    }
                },
                onSetup = { u, n, p ->
                    scope.launch {
                        try {
                            val a = withContext(Dispatchers.IO) { repo.auth.createInitialAdministrator(u, n, p) }
                            user = a
                            needsSetup = false
                            toast("Administrator created ✓")
                        } catch (e: Exception) {
                            toast(e.message ?: "Setup failed", ToastMsg.Kind.Error)
                        }
                    }
                }
            )
            else -> AppShell(
                user = user!!,
                dest = dest,
                navArg = navArg,
                islandOpen = islandOpen,
                onDest = { d, arg ->
                    dest = d
                    navArg = arg
                    islandOpen = false
                },
                onIsland = { islandOpen = !islandOpen },
                onLogout = {
                    islandOpen = false
                    repo.auth.logout()
                    user = null
                    dest = Dest.Dashboard
                    navArg = ""
                },
                onToggleLang = {
                    I18n.toggle(ctx)
                    scope.launch(Dispatchers.IO) {
                        try {
                            val s = repo.settingsLoad().toMutableMap()
                            s["language"] = I18n.lang.value
                            repo.settingsSave(s)
                        } catch (_: Exception) { }
                    }
                },
                onToggleTheme = {
                    MmsThemeController.toggle()
                    scope.launch(Dispatchers.IO) {
                        try {
                            val s = repo.settingsLoad().toMutableMap()
                            s["theme"] = if (MmsThemeController.dark.value) "dark" else "light"
                            repo.settingsSave(s)
                        } catch (_: Exception) { }
                    }
                },
                toast = ::toast,
            )
        }
        ToastHost(toasts.toList()) { id -> toasts.removeAll { it.id == id } }
    }
}

@Composable
private fun AppShell(
    user: AuthUser,
    dest: Dest,
    navArg: String,
    islandOpen: Boolean,
    onDest: (Dest, String) -> Unit,
    onIsland: () -> Unit,
    onLogout: () -> Unit,
    onToggleLang: () -> Unit,
    onToggleTheme: () -> Unit,
    toast: (String, ToastMsg.Kind) -> Unit,
) {
    val c = C()
    val lang by I18n.lang.collectAsState()
    val tint = Tints.of(dest.tint, c.isDark)
    var searchOpen by remember { mutableStateOf(false) }
    var alertsOpen by remember { mutableStateOf(false) }
    var alerts by remember { mutableStateOf(listOf<Map<String, Any?>>()) }
    val scope = rememberCoroutineScope()

    fun go(d: Dest) = onDest(d, "")
    fun reloadAlerts() = scope.launch {
        try {
            alerts = withContext(Dispatchers.IO) { MmsApp.instance.repo.alerts() }
        } catch (_: Exception) { }
    }
    LaunchedEffect(dest) { reloadAlerts() }

    BackHandler(enabled = searchOpen) { searchOpen = false }
    BackHandler(enabled = islandOpen && !searchOpen) { onIsland() }

    CompositionLocalProvider(LocalTint provides tint) {
        Box(Modifier.fillMaxSize().background(c.bodyBg)) {
            Column(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(
                            c.cSky.copy(0.04f),
                            c.bodyBg,
                            c.em.copy(0.04f)
                        )
                    )
                )
            ) {
                // ---- top app bar (phone): logo · title · search · alerts · language
                Row(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(62.dp)
                        .background(c.panel.copy(alpha = 0.96f))
                        .border(1.dp, c.line.copy(alpha = 0.4f))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(c.emLight, c.em))),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.text.BasicText(
                            "M",
                            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        androidx.compose.foundation.text.BasicText(
                            I18n.t(dest.titleKey),
                            style = MmsType.title.copy(color = c.tx, fontSize = 15.5.sp, fontWeight = FontWeight.SemiBold),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        androidx.compose.foundation.text.BasicText(
                            I18n.t("app_name"),
                            style = MmsType.caption.copy(color = c.mut, fontSize = 9.sp, letterSpacing = 0.8.sp),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    TopIconBtn({ searchOpen = true }) {
                        MmsVec(MmsIcons.Search, 18.dp, c.tx)
                    }
                    // alerts bell with badge
                    Box {
                        TopIconBtn({ reloadAlerts(); alertsOpen = true }) {
                            MmsVec(MmsIcons.Bell, 18.dp, if (alerts.isNotEmpty()) c.amber else c.fnt)
                        }
                        if (alerts.isNotEmpty()) {
                            Box(
                                Modifier.align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(c.cRose)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                androidx.compose.foundation.text.BasicText(
                                    alerts.size.toString(),
                                    style = TextStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                    // language toggle pill
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(c.panel2)
                            .border(1.dp, c.line, RoundedCornerShape(99.dp))
                            .mmsClickable(onClick = onToggleLang)
                            .padding(horizontal = 11.dp, vertical = 8.dp)
                    ) {
                        androidx.compose.foundation.text.BasicText(
                            if (lang == "ml") "മല" else "EN",
                            style = TextStyle(color = c.emd, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // ---- page content (extra bottom space so the menu capsule never covers the last row)
                Box(
                    Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(bottom = 92.dp)
                ) {
                    AnimatedContent(
                        targetState = dest,
                        transitionSpec = {
                            (fadeIn(tween(220)) + slideInVertically(tween(280)) { it / 12 }) togetherWith
                                fadeOut(tween(160))
                        },
                        label = "page"
                    ) { d ->
                        ModuleHost(d, navArg, toast = { m, k -> toast(m, k) }, onNavigate = { go(it) })
                    }
                }
            }

            // ---- island module menu (replaces the desktop-style side drawer)
            IslandMenu(
                visible = islandOpen,
                user = user,
                dest = dest,
                onDest = { go(it) },
                onDismiss = onIsland,
                onLogout = onLogout,
                onToggleTheme = onToggleTheme,
            )

            // ---- floating menu capsule (the island trigger, thumb-reachable)
            if (!islandOpen) MenuCapsule(onClick = onIsland)

            // ---- global search overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = searchOpen,
                enter = fadeIn(tween(180)) + slideInVertically(tween(220)) { -it / 6 },
                exit = fadeOut(tween(150))
            ) {
                SearchOverlay(
                    onClose = { searchOpen = false },
                    onPick = { d, arg ->
                        searchOpen = false
                        onDest(d, arg)
                    }
                )
            }
        }

        // ---- alerts dialog
        if (alertsOpen) {
            MmsDialog("Attention needed", onDismiss = { alertsOpen = false }, compact = true) {
                if (alerts.isEmpty()) {
                    InfoBanner("All clear — no overdue dues or pending requests.", "success")
                } else {
                    alerts.forEach { a ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                androidx.compose.foundation.text.BasicText(
                                    Format.str(a, "message"),
                                    style = MmsType.bodySm.copy(color = c.tx, fontWeight = FontWeight.Medium)
                                )
                            }
                            MmsButton("View", {
                                alertsOpen = false
                                go(if (Format.str(a, "type") == "welfare") Dest.Welfare else Dest.Subscriptions)
                            }, small = true, primary = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopIconBtn(onClick: () -> Unit, content: @Composable () -> Unit) {
    val c = C()
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.panel2)
            .border(1.dp, c.line, RoundedCornerShape(12.dp))
            .mmsClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
    Spacer(Modifier.width(7.dp))
}

// ---------------------------------------------------------------- island menu

/** The floating island popup — every module with its own icon and name,
 *  grouped by section, filterable, with the signed-in user on top. */
@Composable
private fun IslandMenu(
    visible: Boolean,
    user: AuthUser,
    dest: Dest,
    onDest: (Dest) -> Unit,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
) {
    val c = C()
    val isDark by MmsThemeController.dark.collectAsState()
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(140)),
        exit = fadeOut(tween(120)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1521).copy(alpha = 0.45f))
                .mmsClickable(onClick = onDismiss)
        )
    }
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)) { it } +
            fadeIn(tween(120)),
        exit = slideOutVertically(tween(190)) { it } + fadeOut(tween(140)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            IslandPanel(user, dest, onDest, onDismiss, onLogout, onToggleTheme, isDark)
        }
    }
}

@Composable
private fun IslandPanel(
    user: AuthUser,
    dest: Dest,
    onDest: (Dest) -> Unit,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
    isDark: Boolean,
) {
    val c = C()
    val lang by I18n.lang.collectAsState()
    var q by remember { mutableStateOf("") }

    val sectionMl = mapOf(
        "Management" to "മാനേജ്മെന്റ്",
        "Finance" to "സാമ്പത്തികം",
        "Registers" to "രജിസ്റ്ററുകൾ",
        "System" to "സിസ്റ്റം"
    )
    val filtered = Dest.all.filter { d ->
        q.isBlank() || I18n.t(d.titleKey).contains(q, ignoreCase = true) ||
            d.route.contains(q, ignoreCase = true) || d.name.contains(q, ignoreCase = true)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .shadow(32.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(c.panel.copy(alpha = 0.99f), c.panel)))
            .border(1.5.dp, c.line.copy(alpha = 0.55f), RoundedCornerShape(28.dp))
            .pointerInput(Unit) { detectTapGestures { } }   // swallow taps inside the island
            .imePadding()
    ) {
        // ---- user header
        Row(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(c.em.copy(alpha = 0.10f), Color.Transparent)))
                .padding(start = 18.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(c.skyLight, c.sky)))
                    .border(2.dp, c.panel, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.text.BasicText(
                    user.initials,
                    style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                androidx.compose.foundation.text.BasicText(
                    user.fullName,
                    style = MmsType.title.copy(color = c.tx, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                androidx.compose.foundation.text.BasicText(
                    "${user.role} · ${I18n.t("app_name")}",
                    style = MmsType.caption.copy(color = c.mut, fontSize = 10.sp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            TopIconBtn(onToggleTheme) {
                MmsVec(if (isDark) MmsIcons.Sun else MmsIcons.Moon, 18.dp, c.tx)
            }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(c.line.copy(alpha = 0.5f)))

        // ---- module filter
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchField(q, { q = it }, Modifier.weight(1f), placeholder = I18n.t("menu_filter_modules"))
        }

        // ---- module tiles grouped by section (3 per row, like a launcher)
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 430.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            val grouped = filtered.groupBy { it.section }
            grouped.forEach { (sec, dests) ->
                if (sec != null) {
                    val label = if (lang == "ml") sectionMl[sec] ?: sec else sec
                    Row(
                        Modifier.fillMaxWidth().padding(start = 4.dp, top = 10.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(3.dp).height(13.dp).clip(RoundedCornerShape(2.dp)).background(c.em))
                        Spacer(Modifier.width(8.dp))
                        androidx.compose.foundation.text.BasicText(
                            label.uppercase(),
                            style = MmsType.overline.copy(color = c.tx, fontSize = 9.5.sp, letterSpacing = 1.3.sp, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.weight(1f).height(1.dp).background(c.line.copy(alpha = 0.5f)))
                    }
                }
                dests.chunked(3).forEach { rowDests ->
                    Row(Modifier.fillMaxWidth()) {
                        rowDests.forEach { d ->
                            IslandTile(d, d == dest, Modifier.weight(1f)) { onDest(d) }
                        }
                        repeat(3 - rowDests.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
            if (filtered.isEmpty()) {
                EmptyState("No module matches", "Try another word")
            }
            Spacer(Modifier.height(10.dp))
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(c.line.copy(alpha = 0.5f)))

        // ---- footer: logout + version
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MmsButton("Logout", onLogout, modifier = Modifier.weight(1f), small = true, danger = true, ghost = true, icon = "⎋")
            Spacer(Modifier.width(12.dp))
            androidx.compose.foundation.text.BasicText(
                "v${BuildConfig.VERSION_NAME} · Android",
                style = MmsType.caption.copy(color = c.fnt, fontSize = 10.sp)
            )
        }
    }
}

// The island lays tiles 3 per row — wrap every 3 destinations into a Row.
// Implemented via chunking inside IslandPanel's Column above.

@Composable
private fun IslandTile(d: Dest, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = C()
    val tint = Tints.of(d.tint, c.isDark)
    Column(
        modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) c.selBg else Color.Transparent)
            .border(1.5.dp, if (selected) c.em.copy(alpha = 0.55f) else Color.Transparent, RoundedCornerShape(16.dp))
            .mmsClickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(tint.sc, tint.sc.copy(alpha = 0.74f))))
                .border(1.dp, tint.sl.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            MmsVec(MmsIcons.ofRoute(d.route), 25.dp, Color.White)
        }
        Spacer(Modifier.height(5.dp))
        androidx.compose.foundation.text.BasicText(
            I18n.t(d.titleKey),
            style = MmsType.caption.copy(
                color = if (selected) c.emd else c.tx,
                fontSize = 10.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MenuCapsule(onClick: () -> Unit) {
    val c = C()
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "cap")
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .scale(scale)
                .shadow(18.dp, RoundedCornerShape(99.dp), ambientColor = c.glowEmerald, spotColor = c.glowEmerald)
                .clip(RoundedCornerShape(99.dp))
                .background(Brush.horizontalGradient(listOf(c.emLight, c.em)))
                .border(1.5.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(99.dp))
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        pressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    })
                }
                .padding(horizontal = 22.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MmsVec(MmsIcons.Menu, 19.dp, Color.White)
            Spacer(Modifier.width(9.dp))
            androidx.compose.foundation.text.BasicText(
                I18n.t("nav_menu"),
                style = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
            )
        }
    }
}

@Composable
private fun SearchOverlay(onClose: () -> Unit, onPick: (Dest, String) -> Unit) {
    val c = C()
    val scope = rememberCoroutineScope()
    var q by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<Map<String, Any?>>()) }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(q) {
        if (q.length < 2) {
            results = emptyList()
            return@LaunchedEffect
        }
        busy = true
        try {
            results = withContext(Dispatchers.IO) { MmsApp.instance.repo.globalSearch(q) }
        } catch (_: Exception) { }
        busy = false
    }

    Box(
        Modifier.fillMaxSize().background(c.bodyBg).statusBarsPadding()
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopIconBtn(onClose) {
                    MmsVec(MmsIcons.Close, 18.dp, c.tx)
                }
                SearchField(q, { q = it }, Modifier.weight(1f), placeholder = "Search families, members, receipts…")
            }
            Spacer(Modifier.height(12.dp))
            if (busy) {
                LoadingList(3)
            } else if (q.length >= 2 && results.isEmpty()) {
                EmptyState("No matches", "Try a name, code, phone or receipt number")
            } else if (results.isNotEmpty()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { r ->
                        val kind = Format.str(r, "kind")
                        val (d, tintId) = when (kind) {
                            "family" -> Dest.Families to "em"
                            "member" -> Dest.Members to "teal"
                            else -> Dest.Donations to "pink"
                        }
                        MmsCard(onClick = { onPick(d, Format.str(r, "code")) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TintTile(
                                    Format.str(r, "title").ifBlank { "?" }.take(1),
                                    Tints.of(tintId, c.isDark), 38.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    CellText(Format.str(r, "title"), strong = true, sub = Format.str(r, "code"))
                                }
                                StatusPill(kind.replaceFirstChar { it.uppercase() }, "default")
                            }
                        }
                    }
                }
            } else {
                EmptyState("Global search", "Type at least 2 characters to search everything")
            }
        }
    }
}

@Composable
private fun ModuleHost(
    dest: Dest,
    navArg: String,
    toast: (String, ToastMsg.Kind) -> Unit,
    onNavigate: (Dest) -> Unit,
) {
    when (dest) {
        Dest.Dashboard -> DashboardScreen(toast = toast, onNavigate = onNavigate)
        Dest.Families -> FamiliesScreen(toast = toast, initialSearch = navArg)
        Dest.Members -> MembersScreen(toast = toast, initialSearch = navArg)
        Dest.Staff -> StaffScreen(toast = toast)
        Dest.Committee -> CommitteeScreen(toast = toast)
        Dest.Subscriptions -> SubscriptionsScreen(toast = toast, initialSearch = navArg)
        Dest.Donations -> DonationsScreen(toast = toast, initialSearch = navArg)
        Dest.Accounting -> AccountingScreen(toast = toast)
        Dest.Assets -> AssetsScreen(toast = toast)
        Dest.Marriages -> MarriagesScreen(toast = toast)
        Dest.Deaths -> DeathsScreen(toast = toast)
        Dest.Welfare -> WelfareScreen(toast = toast)
        Dest.Certificates -> CertificatesScreen(toast = toast)
        Dest.Tokens -> TokensScreen(toast = toast)
        Dest.Reports -> ReportsScreen(toast = toast)
        Dest.Settings -> SettingsScreen(toast = toast)
        Dest.Users -> UsersScreen(toast = toast)
        Dest.Audit -> AuditScreen(toast = toast)
        Dest.Backup -> BackupScreen(toast = toast)
    }
}
