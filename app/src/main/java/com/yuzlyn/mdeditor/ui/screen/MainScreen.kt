package com.yuzlyn.mdeditor.ui.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yuzlyn.mdeditor.R
import com.yuzlyn.mdeditor.data.MonetPalette
import com.yuzlyn.mdeditor.data.NoteModel
import com.yuzlyn.mdeditor.data.NoteStorage
import com.yuzlyn.mdeditor.ui.viewmodel.FileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MDEditor_Debug"

private enum class DrawerScreen(
        val labelResId: Int,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val supportingTextResId: Int? = null
) {
  MARKDOWNS(R.string.drawer_markdowns, Icons.Filled.Description, Icons.Outlined.Description),
  NOTEBOOKS(R.string.drawer_notebooks, Icons.Filled.Folder, Icons.Outlined.Folder),
  ARCHIVE(R.string.drawer_archive, Icons.Filled.Archive, Icons.Outlined.Archive),
  TRASH(R.string.drawer_trash, Icons.Filled.Delete, Icons.Outlined.Delete),
  SETTINGS(R.string.drawer_settings, Icons.Filled.Settings, Icons.Outlined.Settings),
  ABOUT(R.string.drawer_about, Icons.Filled.Info, Icons.Outlined.Info)
}

private enum class SortOrder(val labelResId: Int) {
  CUSTOM(R.string.sort_custom),
  CREATED(R.string.sort_created),
  MODIFIED(R.string.sort_modified)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: FileViewModel) {
  val notes by NoteStorage.notes.collectAsState()
  val context = LocalContext.current
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  var currentScreen by remember { mutableStateOf(DrawerScreen.MARKDOWNS) }
  var selectedNotebook by remember { mutableStateOf<String?>(null) }
  var showNewNotebookDialog by remember { mutableStateOf(false) }
  var newNotebookName by remember { mutableStateOf("") }

  var selectedIds by remember { mutableStateOf(setOf<String>()) }
  var showMultiColorSheet by remember { mutableStateOf(false) }
  var showRenameDialog by remember { mutableStateOf(false) }
  var isGridView by rememberSaveable { mutableStateOf(true) }
  var currentSortOrder by remember { mutableStateOf(SortOrder.CUSTOM) }
  var isSearching by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf(TextFieldValue()) }
  var showSortSheet by remember { mutableStateOf(false) }
  var pendingScreen by remember { mutableStateOf<DrawerScreen?>(null) }

  val gridState = rememberLazyStaggeredGridState()
  val listState = rememberLazyListState()

  LaunchedEffect(currentSortOrder, currentScreen, selectedNotebook) {
    if (isGridView) {
      gridState.scrollToItem(0)
    } else {
      listState.scrollToItem(0)
    }
  }
  var drawerContentVisible by remember { mutableStateOf(false) }
  val isSelectionMode = selectedIds.isNotEmpty()

  val notebookCounts = remember(notes) { NoteStorage.getNotebookCounts() }

  fun toggleSelection(noteId: String) {
    selectedIds = if (noteId in selectedIds) selectedIds - noteId else selectedIds + noteId
  }

  fun exitSelectionMode() {
    selectedIds = emptySet()
  }

  LaunchedEffect(drawerState.isClosed) {
    if (drawerState.isClosed && pendingScreen != null) {
      val target = pendingScreen!!
      if (target == DrawerScreen.SETTINGS) {
        navController.navigate("settings")
        pendingScreen = null
      } else {
        currentScreen = target
        selectedNotebook = null
        Log.d(TAG, "侧栏动画完成后切换画面：${target.name}")
        pendingScreen = null
      }
    }
  }

  LaunchedEffect(drawerState.targetValue) {
    drawerContentVisible = drawerState.targetValue == DrawerValue.Open
  }

  fun batchPin() {
    val ids = selectedIds
    ids.forEach { viewModel.pinNote(context, it) }
    exitSelectionMode()
  }

  fun batchArchive() {
    val ids = selectedIds
    ids.forEach { viewModel.archiveNote(context, it) }
    exitSelectionMode()
  }

  fun batchDelete() {
    val ids = selectedIds
    ids.forEach { viewModel.deleteNote(context, it) }
    exitSelectionMode()
  }

  fun batchColor(color: Long) {
    val ids = selectedIds
    ids.forEach { viewModel.setNoteBackgroundColor(context, it, color) }
    showMultiColorSheet = false
    exitSelectionMode()
  }

  fun batchRestore() {
    val ids = selectedIds
    ids.forEach { viewModel.restoreNote(context, it) }
    exitSelectionMode()
  }

  fun batchPermanentDelete() {
    val ids = selectedIds
    ids.forEach { viewModel.permanentlyDeleteNote(context, it) }
    exitSelectionMode()
  }

  fun batchRename(newTitle: String) {
    val ids = selectedIds
    ids.firstOrNull()?.let { viewModel.renameNote(context, it, newTitle) }
    showRenameDialog = false
    exitSelectionMode()
  }

  val filteredNotes =
          remember(notes, currentScreen, selectedNotebook, currentSortOrder) {
            val base =
                    when {
                      currentScreen == DrawerScreen.TRASH -> notes.filter { it.isDeleted }
                      currentScreen == DrawerScreen.ARCHIVE ->
                              notes.filter { it.isArchived && !it.isDeleted }
                      currentScreen == DrawerScreen.NOTEBOOKS && selectedNotebook != null ->
                              notes.filter {
                                it.notebookTag == selectedNotebook &&
                                        !it.isArchived &&
                                        !it.isDeleted
                              }
                      else -> notes.filter { !it.isArchived && !it.isDeleted }
                    }
            when (currentSortOrder) {
              SortOrder.CUSTOM ->
                      base.sortedWith(
                              compareByDescending<NoteModel> { it.isPinned }.thenByDescending {
                                it.lastModified
                              }
                      )
              SortOrder.CREATED -> base.sortedBy { it.lastModified }
              SortOrder.MODIFIED -> base.sortedByDescending { it.lastModified }
            }
          }

  val folderPickerLauncher =
          rememberLauncherForActivityResult(
                  contract = ActivityResultContracts.OpenDocumentTree()
          ) { uri ->
            if (uri != null) {
              viewModel.selectAndImportFolder(context, uri)
              Log.d(TAG, "选中的 SAF 文件夹：$uri")
            }
          }

  ModalNavigationDrawer(
          drawerState = drawerState,
          scrimColor = DrawerDefaults.scrimColor,
          drawerContent = {
            ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.85f).widthIn(max = 360.dp),
                    drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                      text = stringResource(R.string.drawer_title),
                      style = MaterialTheme.typography.headlineSmall,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))

              DrawerScreen.entries.forEachIndexed { index, screen ->
                DrawerFlyInItem(
                        index = index,
                        visible = drawerContentVisible,
                        key = "drawer_item_${screen.name}"
                ) {
                  NavigationDrawerItem(
                          icon = {
                            Icon(
                                    imageVector =
                                            if (currentScreen == screen) screen.selectedIcon
                                            else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.labelResId)
                            )
                          },
                          label = {
                            Text(
                                    stringResource(screen.labelResId),
                                    style = MaterialTheme.typography.labelLarge
                            )
                          },
                          selected = currentScreen == screen,
                          onClick = {
                            if (currentScreen != screen) {
                              pendingScreen = screen
                            }
                            scope.launch { drawerState.close() }
                          },
                          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                          shape = CircleShape,
                          colors =
                                  NavigationDrawerItemDefaults.colors(
                                          selectedContainerColor =
                                                  MaterialTheme.colorScheme.secondaryContainer,
                                          unselectedContainerColor =
                                                  MaterialTheme.colorScheme.surface,
                                          selectedTextColor =
                                                  MaterialTheme.colorScheme.onSecondaryContainer,
                                          unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                          selectedIconColor =
                                                  MaterialTheme.colorScheme.onSecondaryContainer,
                                          unselectedIconColor =
                                                  MaterialTheme.colorScheme.onSurfaceVariant
                                  )
                  )
                }
                if (screen.supportingTextResId != null) {
                  Text(
                          text = stringResource(screen.supportingTextResId),
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                          modifier = Modifier.padding(start = 68.dp, bottom = 8.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.weight(1f))
              NavigationDrawerItem(
                      icon = { Icon(Icons.Filled.CreateNewFolder, contentDescription = "更换资料夹") },
                      label = { Text("更换资料夹", style = MaterialTheme.typography.labelLarge) },
                      selected = false,
                      onClick = {
                        scope.launch { drawerState.close() }
                        folderPickerLauncher.launch(null)
                      },
                      modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                      shape = CircleShape,
                      colors =
                              NavigationDrawerItemDefaults.colors(
                                      unselectedContainerColor = MaterialTheme.colorScheme.surface,
                                      unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                      unselectedIconColor =
                                              MaterialTheme.colorScheme.onSurfaceVariant
                              )
              )
            }
          }
  ) {
    Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            floatingActionButton = {
              when {
                currentScreen == DrawerScreen.TRASH -> {}
                currentScreen == DrawerScreen.ABOUT -> {}
                currentScreen == DrawerScreen.NOTEBOOKS && selectedNotebook == null -> {
                  FloatingActionButton(
                          onClick = { showNewNotebookDialog = true },
                          containerColor = MaterialTheme.colorScheme.primaryContainer,
                          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                          shape = MaterialTheme.shapes.large
                  ) { Icon(Icons.Default.Add, contentDescription = "新建笔记本") }
                }
                else -> {
                  FloatingActionButton(
                          onClick = {
                            val targetNotebook =
                                    if (currentScreen == DrawerScreen.NOTEBOOKS) selectedNotebook
                                    else null
                            val newNote = viewModel.createNewNote(context, targetNotebook)
                            if (newNote != null) {
                              viewModel.setCurrentEditNoteId(newNote.id)
                              navController.navigate("editor")
                            }
                          },
                          containerColor = MaterialTheme.colorScheme.primaryContainer,
                          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                          shape = MaterialTheme.shapes.large
                  ) { Icon(Icons.Default.Add, contentDescription = "新建笔记") }
                }
              }
            }
    ) { innerPadding ->
      Column(
              modifier =
                      Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())
      ) {
        if (currentScreen != DrawerScreen.ABOUT) {
          Surface(
                  modifier =
                          Modifier.fillMaxWidth()
                                  .statusBarsPadding()
                                  .padding(horizontal = 16.dp, vertical = 8.dp),
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.surfaceContainerHigh,
                  tonalElevation = 2.dp,
                  shadowElevation = 0.dp
          ) {
            Row(
                    Modifier.fillMaxWidth().height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
              if (isSelectionMode) {
                IconButton(onClick = { exitSelectionMode() }) {
                  Icon(
                          Icons.Default.Close,
                          contentDescription = stringResource(R.string.exit_selection)
                  )
                }
                Text(
                        stringResource(R.string.multi_select_count, selectedIds.size),
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
                if (selectedIds.size == 1) {
                  IconButton(onClick = { showRenameDialog = true }) {
                    Icon(
                            Icons.Default.DriveFileRenameOutline,
                            contentDescription = stringResource(R.string.rename)
                    )
                  }
                }
                IconButton(onClick = { batchPin() }) {
                  Icon(Icons.Outlined.PushPin, contentDescription = stringResource(R.string.pin))
                }
                IconButton(onClick = { showMultiColorSheet = true }) {
                  Icon(
                          Icons.Default.Palette,
                          contentDescription = stringResource(R.string.bg_color)
                  )
                }
                IconButton(onClick = { batchArchive() }) {
                  Icon(
                          Icons.Default.Archive,
                          contentDescription = stringResource(R.string.editor_archive)
                  )
                }
                IconButton(onClick = { batchDelete() }) {
                  Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
              } else if (isSearching) {
                IconButton(
                        onClick = {
                          isSearching = false
                          searchQuery = TextFieldValue()
                        }
                ) {
                  Icon(
                          Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = stringResource(R.string.back)
                  )
                }
                TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                          Text(
                                  stringResource(R.string.search_notes),
                                  style = MaterialTheme.typography.bodyLarge
                          )
                        },
                        singleLine = true,
                        colors =
                                TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                )
                )
                if (searchQuery.text.isNotBlank()) {
                  IconButton(onClick = { searchQuery = TextFieldValue() }) {
                    Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.exit_selection)
                    )
                  }
                }
              } else {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                  Icon(
                          Icons.Default.Menu,
                          contentDescription = stringResource(R.string.open_drawer)
                  )
                }
                Text(
                        stringResource(R.string.search_notes),
                        modifier =
                                Modifier.weight(1f).padding(horizontal = 4.dp).clickable {
                                  Log.d(TAG, "进入全荧幕搜寻")
                                  isSearching = true
                                },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                        onClick = {
                          isGridView = !isGridView
                          Log.d(TAG, "切换视图展示方式：Grid=$isGridView")
                        }
                ) {
                  Icon(
                          if (isGridView) Icons.Default.ViewStream else Icons.Default.GridView,
                          contentDescription = stringResource(R.string.layout_toggle),
                          tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                IconButton(
                        onClick = {
                          Log.d(TAG, "调起排序选单")
                          showSortSheet = true
                        }
                ) {
                  Icon(
                          Icons.AutoMirrored.Filled.Sort,
                          stringResource(R.string.sort_title),
                          tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
          Crossfade(
                  targetState = currentScreen to selectedNotebook,
                  animationSpec = tween(300, easing = FastOutSlowInEasing),
                  label = "content_crossfade"
          ) {
            when {
              currentScreen == DrawerScreen.ABOUT -> {
                AboutScreen(onBack = { currentScreen = DrawerScreen.MARKDOWNS })
              }
              !viewModel.hasFolderSelected(context) -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            Icons.Default.FolderOpen,
                            null,
                            Modifier.height(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            stringResource(R.string.folder_select_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            stringResource(R.string.folder_select_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    FilledTonalButton(onClick = { folderPickerLauncher.launch(null) }) {
                      Icon(Icons.Default.CreateNewFolder, null, Modifier.padding(end = 8.dp))
                      Text(stringResource(R.string.select_folder))
                    }
                  }
                }
              }
              currentScreen == DrawerScreen.NOTEBOOKS && selectedNotebook == null -> {
                val tags = notebookCounts.keys.sorted()
                if (tags.isEmpty()) {
                  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                              stringResource(R.string.empty_notebooks),
                              style = MaterialTheme.typography.titleMedium,
                              color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                              stringResource(R.string.empty_notebooks_desc),
                              style = MaterialTheme.typography.bodyMedium,
                              color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                } else {
                  LazyVerticalStaggeredGrid(
                          columns = StaggeredGridCells.Fixed(2),
                          modifier = Modifier.fillMaxSize(),
                          contentPadding =
                                  PaddingValues(
                                          start = 8.dp,
                                          end = 8.dp,
                                          top = 8.dp,
                                          bottom = 88.dp
                                  ),
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          verticalItemSpacing = 8.dp
                  ) {
                    itemsIndexed(tags, key = { _, it -> it }) { index, tag ->
                      StaggeredFlyInCard(key = tag, index = index) {
                        NotebookCard(
                                name = tag,
                                count = notebookCounts[tag] ?: 0,
                                onClick = { selectedNotebook = tag }
                        )
                      }
                    }
                  }
                }
              }
              filteredNotes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            if (currentScreen == DrawerScreen.TRASH)
                                    stringResource(R.string.empty_trash)
                            else stringResource(R.string.empty_notes),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            if (currentScreen == DrawerScreen.TRASH)
                                    stringResource(R.string.empty_trash_desc)
                            else stringResource(R.string.empty_notes_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
              else -> {
                val searchText = searchQuery.text.trim()
                val displayNotes =
                        if (isSearching && searchText.isNotBlank())
                                filteredNotes.filter {
                                  it.displayTitle("").contains(searchText, ignoreCase = true) ||
                                          it.content.contains(searchText, ignoreCase = true)
                                }
                        else filteredNotes
                Box(modifier = Modifier.fillMaxSize()) {
                  androidx.compose.runtime.key(currentSortOrder) {
                    if (isGridView) {
                      LazyVerticalStaggeredGrid(
                              columns = StaggeredGridCells.Fixed(2),
                              state = gridState,
                              modifier = Modifier.fillMaxSize(),
                              contentPadding =
                                      PaddingValues(
                                              start = 8.dp,
                                              end = 8.dp,
                                              top = 8.dp,
                                              bottom = 88.dp
                                      ),
                              horizontalArrangement = Arrangement.spacedBy(8.dp),
                              verticalItemSpacing = 8.dp
                      ) {
                        itemsIndexed(displayNotes, key = { _, it -> it.id }) { index, note ->
                          StaggeredFlyInCard(
                                  key = currentScreen.name + "_" + note.id,
                                  index = index
                          ) {
                            NoteCard(
                                    note = note,
                                    viewModel = viewModel,
                                    isSelected = note.id in selectedIds,
                                    onClick = {
                                      if (isSelectionMode) {
                                        toggleSelection(note.id)
                                      } else if (currentScreen != DrawerScreen.TRASH) {
                                        viewModel.setCurrentEditNoteId(note.id)
                                        navController.navigate("editor")
                                      }
                                    },
                                    onLongClick = { toggleSelection(note.id) }
                            )
                          }
                        }
                      }
                    } else {
                      LazyColumn(
                              state = listState,
                              modifier = Modifier.fillMaxSize(),
                              contentPadding =
                                      PaddingValues(
                                              start = 8.dp,
                                              end = 8.dp,
                                              top = 8.dp,
                                              bottom = 88.dp
                                      ),
                              verticalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        items(displayNotes.size, key = { displayNotes[it].id }) { index ->
                          StaggeredFlyInCard(
                                  key = currentScreen.name + "_" + displayNotes[index].id,
                                  index = index
                          ) {
                            NoteListItem(
                                    note = displayNotes[index],
                                    viewModel = viewModel,
                                    isSelected = displayNotes[index].id in selectedIds,
                                    onClick = {
                                      if (isSelectionMode) {
                                        toggleSelection(displayNotes[index].id)
                                      } else if (currentScreen != DrawerScreen.TRASH) {
                                        viewModel.setCurrentEditNoteId(displayNotes[index].id)
                                        navController.navigate("editor")
                                      }
                                    },
                                    onLongClick = { toggleSelection(displayNotes[index].id) },
                                    modifier = Modifier.animateItem()
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      if (showNewNotebookDialog) {
        AlertDialog(
                onDismissRequest = {
                  showNewNotebookDialog = false
                  newNotebookName = ""
                },
                title = { Text(stringResource(R.string.new_notebook)) },
                text = {
                  OutlinedTextField(
                          value = newNotebookName,
                          onValueChange = { newNotebookName = it },
                          label = { Text(stringResource(R.string.notebook_name)) },
                          singleLine = true,
                          modifier = Modifier.fillMaxWidth()
                  )
                },
                confirmButton = {
                  TextButton(
                          onClick = {
                            val name = newNotebookName.trim()
                            if (name.isNotBlank()) {
                              val newNote = viewModel.createNewNote(context, name)
                              if (newNote != null) {
                                viewModel.setCurrentEditNoteId(newNote.id)
                                navController.navigate("editor")
                              }
                              showNewNotebookDialog = false
                              newNotebookName = ""
                            }
                          }
                  ) { Text(stringResource(R.string.create)) }
                },
                dismissButton = {
                  TextButton(
                          onClick = {
                            showNewNotebookDialog = false
                            newNotebookName = ""
                          }
                  ) { Text(stringResource(R.string.cancel)) }
                }
        )
      }

      if (showMultiColorSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
                onDismissRequest = { showMultiColorSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                    stringResource(R.string.select_color),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
            )
            val columns = 5
            val rows = (MonetPalette.entries.size + columns - 1) / columns
            for (row in 0 until rows) {
              Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                for (col in 0 until columns) {
                  val index = row * columns + col
                  if (index < MonetPalette.entries.size) {
                    Surface(
                            onClick = { batchColor(MonetPalette.entries[index].bgColorLong) },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            color = Color(MonetPalette.entries[index].bgColorLong.toInt())
                    ) { Box(modifier = Modifier.height(48.dp).fillMaxWidth()) }
                  } else {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
              Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }

      if (showSortSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(
                    stringResource(R.string.sort_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            SortOrder.entries.forEachIndexed { idx, order ->
              val isSelected = currentSortOrder == order
              Surface(
                      onClick = {
                        currentSortOrder = order
                        Log.d(TAG, "排序切换：${order.name}")
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                          showSortSheet = false
                        }
                      },
                      shape = MaterialTheme.shapes.large,
                      color =
                              if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                              else Color.Transparent,
                      modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                          Icons.Default.Check,
                          contentDescription = null,
                          tint =
                                  if (isSelected) MaterialTheme.colorScheme.primary
                                  else Color.Transparent,
                          modifier = Modifier.size(20.dp)
                  )
                  Spacer(Modifier.size(16.dp))
                  Text(
                          stringResource(order.labelResId),
                          style = MaterialTheme.typography.bodyLarge,
                          color =
                                  if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                                  else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
              if (idx < SortOrder.entries.lastIndex) {
                Spacer(modifier = Modifier.height(2.dp))
              }
            }
          }
        }
      }

      if (showRenameDialog) {
        val noteId = selectedIds.firstOrNull()
        val note = noteId?.let { notes.find { n -> n.id == noteId } }
        var renameValue by remember { mutableStateOf(note?.displayTitle("") ?: "") }
        AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text(stringResource(R.string.rename_note)) },
                text = {
                  OutlinedTextField(
                          value = renameValue,
                          onValueChange = { renameValue = it },
                          label = { Text(stringResource(R.string.rename)) },
                          singleLine = true
                  )
                },
                confirmButton = {
                  TextButton(onClick = { batchRename(renameValue) }) {
                    Text(stringResource(R.string.rename))
                  }
                },
                dismissButton = {
                  TextButton(onClick = { showRenameDialog = false }) {
                    Text(stringResource(R.string.cancel))
                  }
                }
        )
      }
    }
  }
}

@Composable
private fun StaggeredFlyInCard(
        key: Any,
        index: Int,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
) {
  val density = LocalDensity.current
  val offsetPx = with(density) { 80.dp.toPx() }
  val animStarted = remember { mutableStateOf(false) }

  LaunchedEffect(key, index) {
    delay(index * 30L)
    animStarted.value = true
  }

  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(animStarted.value) {
    if (animStarted.value) {
      Log.d(TAG, "卡片 [$index] 触发飞入动画")
      animProgress.animateTo(
              targetValue = 1f,
              animationSpec =
                      spring(
                              dampingRatio = Spring.DampingRatioLowBouncy,
                              stiffness = Spring.StiffnessMediumLow
                      )
      )
    }
  }

  Box(
          modifier =
                  modifier.graphicsLayer {
                    translationY = offsetPx * (1f - animProgress.value)
                    alpha = animProgress.value
                  }
  ) { content() }
}

@Composable
private fun DrawerFlyInItem(
        index: Int,
        visible: Boolean,
        key: String,
        content: @Composable () -> Unit
) {
  val animProgress = remember(key) { Animatable(0f) }
  val density = LocalDensity.current

  LaunchedEffect(visible) {
    if (visible) {
      delay(index * 18L)
      Log.d(TAG, "侧栏胶囊项目 [$index] 触发阶梯飞入")
      animProgress.animateTo(
              targetValue = 1f,
              animationSpec =
                      spring(
                              dampingRatio = Spring.DampingRatioMediumBouncy,
                              stiffness = Spring.StiffnessLow
                      )
      )
    } else {
      animProgress.snapTo(0f)
    }
  }

  val offsetPx = with(density) { 60.dp.toPx() }
  Box(
          modifier =
                  Modifier.graphicsLayer {
                    translationY = offsetPx * (1f - animProgress.value)
                    alpha = animProgress.value
                  }
  ) { content() }
}

@Composable
private fun NotebookCard(name: String, count: Int, onClick: () -> Unit) {
  Card(
          onClick = onClick,
          modifier =
                  Modifier.fillMaxWidth()
                          .border(
                                  0.5.dp,
                                  MaterialTheme.colorScheme.outlineVariant,
                                  MaterialTheme.shapes.large
                          ),
          shape = MaterialTheme.shapes.large,
          colors =
                  CardDefaults.cardColors(
                          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                  )
  ) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
              Icons.Default.Folder,
              contentDescription = null,
              modifier = Modifier.height(48.dp),
              tint = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
              name,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
              stringResource(R.string.notebook_count, count),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
        note: NoteModel,
        viewModel: FileViewModel,
        isSelected: Boolean = false,
        onClick: () -> Unit,
        onLongClick: () -> Unit
) {
  val cardBg =
          MonetPalette.bgColorFor(
                  note.backgroundColor,
                  MaterialTheme.colorScheme.surfaceContainerHigh
          )
  val cardTextColor = MonetPalette.textColorFor(note.backgroundColor)
  val cardTextVariant = cardTextColor.copy(alpha = 0.65f)
  val cardOutline = cardTextColor.copy(alpha = 0.35f)

  Card(
          modifier =
                  Modifier.fillMaxWidth()
                          .then(
                                  if (isSelected)
                                          Modifier.border(
                                                  2.dp,
                                                  MaterialTheme.colorScheme.primary,
                                                  MaterialTheme.shapes.large
                                          )
                                  else
                                          Modifier.border(
                                                  0.5.dp,
                                                  MaterialTheme.colorScheme.outlineVariant,
                                                  MaterialTheme.shapes.large
                                          )
                          ),
          onClick = onClick,
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = cardBg)
  ) {
    Column(
            modifier =
                    Modifier.padding(12.dp)
                            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
      val displayTitle = note.displayTitle(stringResource(R.string.untitled))
      if (displayTitle.isNotBlank()) {
        Text(
                displayTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = cardTextColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
        )
      }
      if (note.content.isNotBlank()) {
        val contentFirstLine = note.content.lines().firstOrNull() ?: ""
        val displayContent =
                if (displayTitle == contentFirstLine) {
                  note.content.lines().drop(1).joinToString("\n").trim()
                } else {
                  note.content
                }
        if (displayContent.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
                  displayContent,
                  style = MaterialTheme.typography.bodySmall,
                  color = cardTextVariant,
                  maxLines = 6,
                  overflow = TextOverflow.Ellipsis
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
              viewModel.getDateFormatted(note.lastModified),
              style = MaterialTheme.typography.labelSmall,
              color = cardOutline
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteListItem(
        note: NoteModel,
        viewModel: FileViewModel,
        isSelected: Boolean = false,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
        modifier: Modifier = Modifier
) {
  val displayTitle = note.displayTitle(stringResource(R.string.untitled))
  val rawFirstLine = note.content.lines().firstOrNull() ?: ""
  val firstLine = rawFirstLine.take(80)
  val secondLine =
          if (note.content.isNotBlank()) note.content.lines().drop(1).joinToString(" ").take(80)
          else ""
  val textColor = MonetPalette.textColorFor(note.backgroundColor)
  val variantColor = textColor.copy(alpha = 0.6f)
  val mutedColor = textColor.copy(alpha = 0.38f)

  val borderMod =
          if (isSelected)
                  Modifier.border(
                          2.dp,
                          MaterialTheme.colorScheme.primary,
                          RoundedCornerShape(12.dp)
                  )
          else
                  Modifier.border(
                          0.5.dp,
                          MaterialTheme.colorScheme.outlineVariant,
                          RoundedCornerShape(12.dp)
                  )

  Card(
          onClick = onClick,
          modifier = modifier.fillMaxWidth().sizeIn(minHeight = 64.dp).then(borderMod),
          shape = RoundedCornerShape(12.dp),
          colors =
                  CardDefaults.cardColors(
                          containerColor =
                                  MonetPalette.bgColorFor(
                                          note.backgroundColor,
                                          MaterialTheme.colorScheme.surfaceContainerHigh
                                  )
                  )
  ) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Text(
                  text = displayTitle,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = textColor,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
                  text = viewModel.getDateFormatted(note.lastModified),
                  style = MaterialTheme.typography.bodySmall,
                  color = mutedColor,
                  maxLines = 1
          )
        }

        if (firstLine.isNotBlank() && displayTitle != rawFirstLine) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
                  text = firstLine,
                  style = MaterialTheme.typography.bodyMedium,
                  color = variantColor,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
          )
        }

        if (secondLine.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
                  text = secondLine,
                  style = MaterialTheme.typography.bodySmall,
                  color = mutedColor,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}
