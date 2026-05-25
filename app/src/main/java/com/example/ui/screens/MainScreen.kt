package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.ITunesAppResult
import com.example.data.model.SavedIcon
import com.example.ui.viewmodel.CuratedApp
import com.example.ui.viewmodel.IconExplorerViewModel
import com.example.ui.viewmodel.SearchUiState
import kotlinx.coroutines.launch

// Custom iOS App Icon Squircle shape percent
private const val SQUIRCLE_PERCENT = 22

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: IconExplorerViewModel) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isMacPlatform by viewModel.isMacPlatform.collectAsState()
    val searchUiState by viewModel.searchUiState.collectAsState()
    val savedIcons by viewModel.savedIcons.collectAsState()
    val selectedApp by viewModel.selectedApp.collectAsState()

    var activeStylisticTheme by remember { mutableStateOf("None") }
    var selectedCategory by remember { mutableStateOf("") }

    // Clean Minimalism background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2C2E6),
            Color(0xFFE2D1F9),
            Color(0xFFFAD0C4)
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawRect(brush = backgroundGradient) }
            .testTag("main_screen_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // App Name & Branding Section
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = "APPLE ICON",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4F46E5), // Clean Rich Indigo Accented Slate
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Showcase & Explorer",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = Color(0xFF0F172A) // Slate-900 Clean Typography
                    )
                }

                // Apple-style subtle logo overlay or decorative icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.5f)) // Translucent minimalist glass box
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Showcase rating",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // IOS & MacOS Platform Toggle pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.4f)) // Glassy capsule background
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // iOS / iPadOS Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (!isMacPlatform) Color.White else Color.Transparent) // Off-white clean active pill
                        .clickable { viewModel.onPlatformToggled(false) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "iOS Icon Search selection",
                            tint = if (!isMacPlatform) Color(0xFF0F172A) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "iOS / iPadOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (!isMacPlatform) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }
                }

                // Mac Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isMacPlatform) Color.White else Color.Transparent)
                        .clickable { viewModel.onPlatformToggled(true) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "macOS Icon Search selection",
                            tint = if (isMacPlatform) Color(0xFF0F172A) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "macOS Icons",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isMacPlatform) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glassmorphic Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onQueryChanged(it) },
                placeholder = {
                    Text(
                        text = "Search beautiful Apple apps (e.g. Flighty, Bear, Xcode)",
                        color = Color(0xFF64748B), // Slate-500 placeholder
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search input field icon",
                        tint = Color(0xFF4F46E5) // Clean Rich Indigo tint
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search input",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4F46E5),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedContainerColor = Color.White.copy(alpha = 0.85f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.55f),
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_field")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. STYLISTIC ICON THEMES GUIDE & FILTER SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stylistic Design Themes Guide",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    if (activeStylisticTheme != "None") {
                        Text(
                            text = "Reset Lens Filter",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4F46E5),
                            modifier = Modifier.clickable { activeStylisticTheme = "None" }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val themes = listOf(
                        Triple("Minimalist", "Monochromatic outlines, supreme negative space, no visual noise.", Color(0xFF64748B)),
                        Triple("Colorful", "Hyper-saturated neon overflows, vibrant spectrum gradients.", Color(0xFFEC4899)),
                        Triple("Skeuomorphic", "Tactile 3D shadows, embossed metal luster, glossy glass overlays.", Color(0xFFFBBF24)),
                        Triple("Flat Design", "Clean solid fills, 2D vector structures, bold high contrast boundaries.", Color(0xFF3B82F6))
                    )
                    items(themes) { (name, desc, color) ->
                        val isSelected = activeStylisticTheme == name
                        Card(
                            onClick = { activeStylisticTheme = name },
                            modifier = Modifier
                                .width(152.dp)
                                .height(72.dp)
                                .testTag("theme_card_$name"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF4F46E5) else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. DESIGN LIBRARY CATEGORIES SELECTOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Design Categories:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(end = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1.0f)
                ) {
                    val categories = listOf(
                        Pair("Productivity", Icons.Default.Edit),
                        Pair("Social", Icons.Default.Share),
                        Pair("Games", Icons.Default.PlayArrow),
                        Pair("Utilities", Icons.Default.Settings),
                        Pair("Health", Icons.Default.Favorite),
                        Pair("Entertainment", Icons.Default.List)
                    )
                    items(categories) { (name, icon) ->
                        val isSelected = searchQuery.equals(name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.onQueryChanged("")
                                } else {
                                    viewModel.onQueryChanged(name)
                                }
                            },
                            label = { Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "$name category selection indicator",
                                    modifier = Modifier.size(12.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.4f),
                                selectedContainerColor = Color(0xFF4F46E5),
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.White.copy(alpha = 0.2f),
                                selectedBorderColor = Color(0xFF4F46E5)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content Area (Dynamic Scrolling / Conditional Layouts)
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Saved Favorites Shelf (Display horizontally only if there are entries saved)
                    if (savedIcons.isNotEmpty()) {
                        Text(
                            text = "Saved App Icons",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155), // Slate-700
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        ) {
                            items(savedIcons) { saved ->
                                SavedIconItem(
                                    saved = saved,
                                    onClick = { viewModel.selectSavedIcon(saved) }
                                )
                            }
                        }
                    }

                    // Bottom list / grid depending on Search state
                    when (val state = searchUiState) {
                        is SearchUiState.Idle -> {
                            // Empty query -> Display Curated Showcase Panel
                            Text(
                                text = "Curated Vibe Showcase",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5), // Clean indigo highlight
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "Explore beautiful app layouts built by master developers using consistent Apple squircle visual templates.",
                                fontSize = 12.sp,
                                color = Color(0xFF475569), // Slate-600
                                modifier = Modifier.padding(bottom = 14.dp)
                            )

                             LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1.0f)
                            ) {
                                items(viewModel.curatedApps) { curated ->
                                    CuratedAppCard(
                                        app = curated,
                                        onClick = { viewModel.selectCuratedApp(curated) },
                                        themeLens = activeStylisticTheme
                                    )
                                }
                            }
                        }

                        is SearchUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1.0f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color(0xFF4F46E5))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Querying Apple App Store...", color = Color(0xFF475569), fontSize = 13.sp)
                                }
                            }
                        }

                        is SearchUiState.Success -> {
                            if (state.results.isEmpty()) {
                                EmptySearchResults()
                            } else {
                                Text(
                                    text = "Results for \"$searchQuery\"",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B), // Slate-800
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.weight(1.0f)
                                ) {
                                    items(state.results) { app ->
                                        AppSearchResultCard(
                                            app = app,
                                            onClick = { viewModel.selectApp(app) },
                                            themeLens = activeStylisticTheme
                                        )
                                    }
                                }
                            }
                        }

                        is SearchUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1.0f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Error notification icon",
                                        tint = Color(0xFFD97706), // Soft warm amber alert
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = state.message,
                                        color = Color(0xFF475569), // Slate-600
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail overlay dialog (acts exactly as a stunning overlay preview card inside the Single-Screen setup!)
    selectedApp?.let { app ->
        AppIconDetailDialog(
            app = app,
            isSaved = savedIcons.any { it.id == app.trackId?.toString() },
            onToggleSave = { viewModel.toggleSaveApp(app) },
            onClose = { viewModel.selectApp(null) }
        )
    }
}

@Composable
fun SavedIconItem(
    saved: SavedIcon,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(86.dp)
            .clickable { onClick() }
            .testTag("saved_icon_${saved.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(SQUIRCLE_PERCENT))
                    .background(Color.White.copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(SQUIRCLE_PERCENT))
            ) {
                AsyncImage(
                    model = saved.artworkUrl100,
                    contentDescription = "Saved Favorite app icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = saved.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B), // Slate-800
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CuratedAppCard(
    app: CuratedApp,
    onClick: () -> Unit,
    themeLens: String
) {
    val cardBorder = when (themeLens) {
        "Minimalist" -> BorderStroke(0.8.dp, Color(0xFF94A3B8).copy(alpha = 0.4f))
        "Colorful" -> BorderStroke(2.5.dp, Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))))
        "Skeuomorphic" -> BorderStroke(2.dp, Brush.linearGradient(listOf(Color.White, Color(0xFFCBD5E1), Color.White)))
        "Flat Design" -> BorderStroke(1.5.dp, Color(0xFF0F172A))
        else -> BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    }

    val iconBorderModifier = when (themeLens) {
        "Minimalist" -> Modifier.border(0.5.dp, Color(0xFF94A3B8), RoundedCornerShape(SQUIRCLE_PERCENT))
        "Colorful" -> Modifier.border(2.5.dp, Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFF3B82F6), Color(0xFFEC4899))), RoundedCornerShape(SQUIRCLE_PERCENT))
        "Skeuomorphic" -> Modifier.border(1.5.dp, Brush.verticalGradient(listOf(Color.White, Color.Black.copy(alpha = 0.3f))), RoundedCornerShape(SQUIRCLE_PERCENT)).shadow(4.dp, shape = RoundedCornerShape(SQUIRCLE_PERCENT))
        "Flat Design" -> Modifier.border(1.5.dp, Color(0xFF0F172A), RoundedCornerShape(SQUIRCLE_PERCENT))
        else -> Modifier.border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(SQUIRCLE_PERCENT)).shadow(2.dp, shape = RoundedCornerShape(SQUIRCLE_PERCENT))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("curated_card_${app.trackId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        border = cardBorder
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1.0f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SQUIRCLE_PERCENT))
                    .then(iconBorderModifier)
            ) {
                AsyncImage(
                    model = app.artworkUrl512,
                    contentDescription = "Curated custom designed app icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A), // Slate-900
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = app.primaryGenreName,
                fontSize = 10.sp,
                color = Color(0xFF64748B), // Slate-500
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AppSearchResultCard(
    app: ITunesAppResult,
    onClick: () -> Unit,
    themeLens: String
) {
    val cardBorder = when (themeLens) {
        "Minimalist" -> BorderStroke(0.8.dp, Color(0xFF94A3B8).copy(alpha = 0.4f))
        "Colorful" -> BorderStroke(2.5.dp, Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))))
        "Skeuomorphic" -> BorderStroke(2.dp, Brush.linearGradient(listOf(Color.White, Color(0xFFCBD5E1), Color.White)))
        "Flat Design" -> BorderStroke(1.5.dp, Color(0xFF0F172A))
        else -> BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    }

    val iconBorderModifier = when (themeLens) {
        "Minimalist" -> Modifier.border(0.5.dp, Color(0xFF94A3B8), RoundedCornerShape(SQUIRCLE_PERCENT))
        "Colorful" -> Modifier.border(2.5.dp, Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFF3B82F6), Color(0xFFEC4899))), RoundedCornerShape(SQUIRCLE_PERCENT))
        "Skeuomorphic" -> Modifier.border(1.5.dp, Brush.verticalGradient(listOf(Color.White, Color.Black.copy(alpha = 0.3f))), RoundedCornerShape(SQUIRCLE_PERCENT)).shadow(4.dp, shape = RoundedCornerShape(SQUIRCLE_PERCENT))
        "Flat Design" -> Modifier.border(1.5.dp, Color(0xFF0F172A), RoundedCornerShape(SQUIRCLE_PERCENT))
        else -> Modifier.border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(SQUIRCLE_PERCENT))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("search_result_card_${app.trackId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        border = cardBorder
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1.0f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SQUIRCLE_PERCENT))
                    .then(iconBorderModifier)
            ) {
                AsyncImage(
                    model = app.artworkUrl512 ?: app.artworkUrl100,
                    contentDescription = "Loaded App Store highres icon preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.trackName ?: "Apple App",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A), // Slate-900
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = app.primaryGenreName ?: "Utilities",
                fontSize = 10.sp,
                color = Color(0xFF64748B), // Slate-500
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmptySearchResults() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Search empty alert indicator",
                tint = Color(0xFF94A3B8), // Slate-400
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Icons Discovered",
                color = Color(0xFF0F172A), // Slate-900
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Double-check your spelling or look up developer names.",
                color = Color(0xFF475569), // Slate-600
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun AppIconDetailDialog(
    app: ITunesAppResult,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var cornerPercent by remember { mutableStateOf(SQUIRCLE_PERCENT) }
    var selectedWallpaperIdx by remember { mutableStateOf(0) }
    var mockupTemplateMode by remember { mutableStateOf(0) } // 0: Workspace, 1: Notification Badges, 2: Smart Widget Hub

    // Predefined vibrant gradient wallpapers for simulation
    val wallpapers = listOf(
        Brush.linearGradient(colors = listOf(Color(0xFFF43F5E), Color(0xFF8B5CF6))),
        Brush.linearGradient(colors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))),
        Brush.linearGradient(colors = listOf(Color(0xFF10B981), Color(0xFFFBBF24))),
        Brush.linearGradient(colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
    )

    // Fallback info fields
    val trackId = app.trackId?.toString() ?: ""
    val trackName = app.trackName ?: "Apple App"
    val devName = app.artistName ?: "Unknown Developer"
    val genreName = app.primaryGenreName ?: "Utilities"
    val ratingString = if (app.averageUserRating != null) "★ ${"%.1f".format(app.averageUserRating)}" else "★ 4.5"
    val priceStr = app.formattedPrice ?: "Free"
    val storeUrl = app.trackViewUrl ?: ""

    // Hardcoded generated design palette chips for interactive extraction simulation
    val designPaletteColors = remember(app.trackId) {
        listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFF3B82F6), // Blue
            Color(0xFFEC4899), // Pink
            Color(0xFFFBBF24), // Amber
            Color(0xFF10B981)  // Emerald
        )
    }

    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .shadow(24.dp, shape = RoundedCornerShape(28.dp))
                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                .testTag("detail_overlay_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)) // Beautiful clean light background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header navigation inside dialogue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Close detailed overlay dialog and return",
                            tint = Color(0xFF0F172A) // Slate-900 navigation element
                        )
                    }

                    Text(
                        text = "ICON STUDIO PREVIEW",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF4F46E5) // Clean Rich Indigo Accented Slate
                    )

                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Pin icon to favorites shelf",
                            tint = if (isSaved) Color.Red else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-Resolution App Icon displaying central interactive corner scaling preview
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(cornerPercent))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(cornerPercent))
                        .shadow(2.dp, shape = RoundedCornerShape(cornerPercent))
                ) {
                    AsyncImage(
                        model = app.artworkUrl512 ?: app.artworkUrl100,
                        contentDescription = "High-resolution display of Apple application icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = trackName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A), // Slate-900
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "by $devName",
                    fontSize = 13.sp,
                    color = Color(0xFF475569), // Slate-600
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic App details chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(genreName, color = Color(0xFF475569), fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE2E8F0))
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(ratingString, color = Color(0xFFD97706), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE2E8F0))
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(priceStr, color = Color(0xFF059669), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE2E8F0))
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 18.dp))

                // Contour interactive tester slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aesthetic Corner Tester",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A) // Slate-900
                        )
                        Text(
                            text = "${cornerPercent}% radius",
                            fontSize = 11.sp,
                            color = Color(0xFF4F46E5) // Clean Rich Indigo Accented Slate
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = cornerPercent.toFloat(),
                        onValueChange = { cornerPercent = it.toInt() },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4F46E5),
                            activeTrackColor = Color(0xFF4F46E5),
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("radius_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Square (0%)", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text("Apple Squircle (22%)", fontSize = 10.sp, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                        Text("Circular (50%)", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 18.dp))

                // COLOR SWATCH PALETTE EXPLORER
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Extracted Design Swatch",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A) // Slate-900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulated visual palette based on dominant colors extracted from the App icon vector path.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569) // Slate-600
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        designPaletteColors.forEachIndexed { index, color ->
                            var isCopiedTextVisible by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .aspectRatio(1.3f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color)
                                    .clickable {
                                        // Simple tactile click feed
                                    },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = "#%06X".format(color.value.toLong() and 0xFFFFFFL),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 18.dp))

                // INTERACTIVE IPHONE HOME SCREEN MOCKUP SANDBOX!
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Native iOS Dock Placement Simulator",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Observe how this high-resolution masterpiece sits inside a simulated premium iPhone home screen layout.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Realistic Mockup Templates Select Segment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val templates = listOf("Workspace Layout", "Notification Badges", "Dashboard Widget")
                        templates.forEachIndexed { index, title ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (mockupTemplateMode == index) Color.White else Color.Transparent)
                                    .clickable { mockupTemplateMode = index }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mockupTemplateMode == index) Color(0xFF0F172A) else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Wallpaper toggles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text("Wallpaper Mode:", fontSize = 11.sp, color = Color(0xFF475569))
                        wallpapers.forEachIndexed { idx, brush ->
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                                    .border(
                                        width = if (selectedWallpaperIdx == idx) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedWallpaperIdx = idx }
                            )
                        }
                    }

                    // Simulated iPhone screen body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(wallpapers[selectedWallpaperIdx])
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (mockupTemplateMode == 2) {
                                // 2. Dashboard Hub with simulated Smart Premium Widget layout
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.22f))
                                        .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Simulated smart calendar widget symbol",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Tuesday, Jun 12", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("Sunny 22°C • 5 active tasks remaining", color = Color.White.copy(alpha = 0.8f), fontSize = 7.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    SimulatedGridIcon(
                                        artworkUrl = app.artworkUrl512 ?: app.artworkUrl100 ?: "",
                                        name = if (trackName.length > 8) trackName.take(6) + ".." else trackName,
                                        customRadiusPercent = cornerPercent,
                                        hasBadge = false
                                    )
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Home, label = "Home", backgroundColor = Color(0xFF3B82F6))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Share, label = "Messages", backgroundColor = Color(0xFF10B981))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Settings, label = "Settings", backgroundColor = Color(0xFF475569))
                                }
                            } else {
                                // 0 or 1: Grid Mode layouts (Classic Layout or Notification Alerts with red indicators)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    SimulatedGridIcon(
                                        artworkUrl = app.artworkUrl512 ?: app.artworkUrl100 ?: "",
                                        name = if (trackName.length > 8) trackName.take(6) + ".." else trackName,
                                        customRadiusPercent = cornerPercent,
                                        hasBadge = (mockupTemplateMode == 1),
                                        badgeCount = "7"
                                    )

                                    SimulatedGridIconPlaceholder(
                                        icon = Icons.Default.Home,
                                        label = "Home",
                                        backgroundColor = Color(0xFF3B82F6),
                                        hasBadge = (mockupTemplateMode == 1),
                                        badgeCount = "12"
                                    )

                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Share, label = "Messages", backgroundColor = Color(0xFF10B981))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Settings, label = "Settings", backgroundColor = Color(0xFF475569))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.List, label = "Notes", backgroundColor = Color(0xFFFBBF24))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Search, label = "Search", backgroundColor = Color(0xFF93C5FD))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Star, label = "Stars", backgroundColor = Color(0xFFFDE047))
                                    SimulatedGridIconPlaceholder(icon = Icons.Default.Email, label = "Mail", backgroundColor = Color(0xFFA78BFA))
                                }
                            }

                            // Simulated blurred Dock matching physical iOS experience
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SimulatedDockPlaceholder(icon = Icons.Default.Phone, backgroundColor = Color(0xFF34D399))
                                    // Target App duplicate sitting beautifully in simulated dock list
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(cornerPercent))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(cornerPercent))
                                    ) {
                                        AsyncImage(
                                            model = app.artworkUrl100 ?: app.artworkUrl512 ?: "",
                                            contentDescription = "Dock replica",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    SimulatedDockPlaceholder(icon = Icons.Default.Search, backgroundColor = Color(0xFF93C5FD))
                                    SimulatedDockPlaceholder(icon = Icons.Default.Star, backgroundColor = Color(0xFFFDE047))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Launch official App Store link button
                Button(
                    onClick = {
                        if (storeUrl.isNotEmpty()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(
                                    context,
                                    "No browser or App Store application installed to open this link.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("appstore_link_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "App Store direct portal link icon", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View in Official Apple App Store",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun SimulatedGridIcon(
    artworkUrl: String,
    name: String,
    customRadiusPercent: Int,
    hasBadge: Boolean = false,
    badgeCount: String = "1"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(customRadiusPercent))
                    .background(Color.Transparent)
            ) {
                AsyncImage(
                    model = artworkUrl,
                    contentDescription = "Simulated application Icon overlay item",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .border(0.5.dp, Color.White, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount,
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(name, fontSize = 7.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SimulatedGridIconPlaceholder(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    hasBadge: Boolean = false,
    badgeCount: String = "1"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(SQUIRCLE_PERCENT))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "App placeholder icon item indicator",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .border(0.5.dp, Color.White, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount,
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 7.sp, color = Color.White)
    }
}

@Composable
fun SimulatedDockPlaceholder(
    icon: ImageVector,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(SQUIRCLE_PERCENT))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = "Dock system placeholder item icon", tint = Color.White, modifier = Modifier.size(14.dp))
    }
}
