package com.yuzlyn.mdeditor.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yuzlyn.mdeditor.R
import com.yuzlyn.mdeditor.data.MonetPalette
import com.yuzlyn.mdeditor.data.NoteStorage
import com.yuzlyn.mdeditor.ui.viewmodel.FileViewModel
import kotlinx.coroutines.launch

private const val TAG = "MDEditor_Debug"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(navController: NavHostController, viewModel: FileViewModel) {
  val noteId =
          viewModel.currentEditNoteId.value
                  ?: run {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                    return
                  }

  val context = LocalContext.current
  val allNotes by NoteStorage.notes.collectAsState()
  val note = remember(allNotes) { allNotes.find { it.id == noteId } }

  if (note == null) {
    LaunchedEffect(Unit) { navController.popBackStack() }
    return
  }

  var textFieldValue by remember {
    mutableStateOf(TextFieldValue(text = note.content, selection = TextRange(0)))
  }
  var isPreviewMode by remember { mutableStateOf(false) }
  var isExiting by remember { mutableStateOf(false) }
  var showRenameDialog by remember { mutableStateOf(false) }
  val editorScrollState = rememberScrollState()
  var scrollRatio by remember { mutableStateOf(0f) }
  var pendingScrollRestore by remember { mutableStateOf(false) }
  var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
  var hasCompensated by remember { mutableStateOf(false) }
  var viewportHeightPx by remember { mutableStateOf(0) }
  val focusRequester = remember { FocusRequester() }
  var focusRequestVersion by remember { mutableStateOf(0) }

  val density = LocalDensity.current
  val isImeVisible = WindowInsets.ime.getBottom(density) > 0

  LaunchedEffect(focusRequestVersion) {
    if (focusRequestVersion > 0) {
      try {
        focusRequester.requestFocus()
      } catch (_: Exception) {}
    }
  }

  LaunchedEffect(isImeVisible) {
    if (!isImeVisible) {
      hasCompensated = false
      Log.d(TAG, "鍵盤隱藏，重置補償鎖定")
    }
  }

  LaunchedEffect(isImeVisible, textFieldValue.selection, textLayoutResult, viewportHeightPx) {
    if (!isPreviewMode &&
                    isImeVisible &&
                    !hasCompensated &&
                    textLayoutResult != null &&
                    viewportHeightPx > 0
    ) {
      try {
        val cursorRect = textLayoutResult!!.getCursorRect(textFieldValue.selection.start)
        val safetyPx = with(density) { 50.dp.toPx() }
        // cursorRect 在文本佈局坐標系中，editorScrollState.value 是滾動偏移量
        // 光標在可視區域中的位置 = cursorRect.bottom - editorScrollState.value
        val cursorVisibleY = cursorRect.bottom - editorScrollState.value
        if (cursorVisibleY > viewportHeightPx - safetyPx || cursorVisibleY < 0) {
          val target =
                  (editorScrollState.value + cursorVisibleY - viewportHeightPx / 2)
                          .toInt()
                          .coerceAtLeast(0)
          Log.d(
                  TAG,
                  "檢測到鍵盤彈起，光標被遮擋：cursorBottom=${cursorRect.bottom}, " +
                          "scrollValue=${editorScrollState.value}, cursorVisible=$cursorVisibleY, " +
                          "viewport=$viewportHeightPx, target=$target"
          )
          editorScrollState.animateScrollTo(target)
        } else {
          Log.d(
                  TAG,
                  "鍵盤彈起但光標在可視區內 (cursorVisible=$cursorVisibleY, viewport=$viewportHeightPx)，畫面保持靜態"
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "光標遮擋計算失敗: ${e.message}")
      }
      hasCompensated = true
    }
  }

  LaunchedEffect(isExiting) {
    if (isExiting) {
      viewModel.updateNoteInMemory(noteId, textFieldValue.text)
      viewModel.saveNoteToDisk(context, noteId)
      navController.popBackStack()
    }
  }

  LaunchedEffect(isPreviewMode) { pendingScrollRestore = true }

  LaunchedEffect(editorScrollState.maxValue, pendingScrollRestore) {
    if (pendingScrollRestore && editorScrollState.maxValue > 0 && scrollRatio > 0f) {
      val ms = editorScrollState.maxValue
      val target = ((scrollRatio * ms).toInt()).let { if (it < 0) 0 else if (it > ms) ms else it }
      editorScrollState.scrollTo(target)
      Log.d(TAG, "锚定: ratio=$scrollRatio, target=$target, max=$ms")
      pendingScrollRestore = false
    }
  }

  BackHandler { if (!isExiting) isExiting = true }

  val isDark = isSystemInDarkTheme()
  val surfaceBg =
          MonetPalette.bgColorFor(
                  note.backgroundColor,
                  MaterialTheme.colorScheme.surface,
                  darkTheme = isDark
          )
  val onSurfaceText = MonetPalette.textColorFor(note.backgroundColor, darkTheme = isDark)

  Surface(color = surfaceBg) {
    Column(modifier = Modifier.fillMaxSize()) {
      TopAppBar(
              title = {},
              navigationIcon = {
                IconButton(onClick = { isExiting = true }) {
                  Icon(
                          Icons.AutoMirrored.Filled.ArrowBack,
                          stringResource(R.string.editor_back),
                          tint = onSurfaceText.copy(alpha = 0.7f)
                  )
                }
              },
              actions = {
                IconButton(
                        onClick = {
                          val maxScroll = editorScrollState.maxValue
                          if (maxScroll > 0) {
                            scrollRatio = editorScrollState.value.toFloat() / maxScroll.toFloat()
                            Log.d(
                                    TAG,
                                    "切换模式前保存滚动进度：$scrollRatio (offset=${editorScrollState.value}/$maxScroll)"
                            )
                          }
                          isPreviewMode = !isPreviewMode
                        }
                ) {
                  Icon(
                          imageVector =
                                  if (isPreviewMode) Icons.Default.Visibility
                                  else Icons.Default.VisibilityOff,
                          contentDescription =
                                  if (isPreviewMode) stringResource(R.string.editor_code_mode)
                                  else stringResource(R.string.editor_preview_mode),
                          tint =
                                  if (isPreviewMode) MaterialTheme.colorScheme.primary
                                  else onSurfaceText.copy(alpha = 0.7f)
                  )
                }
                IconButton(onClick = { viewModel.pinNote(context, noteId) }) {
                  Icon(
                          imageVector =
                                  if (note.isPinned) Icons.Filled.PushPin
                                  else Icons.Outlined.PushPin,
                          contentDescription =
                                  if (note.isPinned) stringResource(R.string.editor_pinned)
                                  else stringResource(R.string.editor_unpinned),
                          tint =
                                  if (note.isPinned) MaterialTheme.colorScheme.primary
                                  else onSurfaceText.copy(alpha = 0.7f)
                  )
                }
                IconButton(
                        onClick = {
                          viewModel.archiveNote(context, noteId)
                          isExiting = true
                        }
                ) {
                  Icon(
                          Icons.Default.Archive,
                          stringResource(R.string.editor_archive),
                          tint = onSurfaceText.copy(alpha = 0.7f)
                  )
                }
                IconButton(onClick = { showRenameDialog = true }) {
                  Icon(
                          Icons.Default.DriveFileRenameOutline,
                          stringResource(R.string.rename),
                          tint = onSurfaceText.copy(alpha = 0.7f)
                  )
                }
              },
              colors =
                      TopAppBarDefaults.topAppBarColors(
                              containerColor = surfaceBg,
                              titleContentColor = onSurfaceText,
                              navigationIconContentColor = onSurfaceText.copy(alpha = 0.7f),
                              actionIconContentColor = onSurfaceText.copy(alpha = 0.7f)
                      )
      )

      Box(
              modifier =
                      Modifier.weight(1f)
                              .fillMaxWidth()
                              .navigationBarsPadding()
                              .windowInsetsPadding(WindowInsets.ime)
      ) {
        if (isPreviewMode) {
          MarkdownPreview(
                  markdown = textFieldValue.text,
                  textColor = onSurfaceText,
                  scrollState = editorScrollState,
                  modifier = Modifier
          )
        } else {
          BoxWithConstraints(
                  modifier =
                          Modifier.fillMaxSize()
                                  .verticalScroll(editorScrollState)
                                  .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            androidx.compose.runtime.SideEffect {
              viewportHeightPx = with(density) { maxHeight.toPx().toInt() }
            }
            BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                      textFieldValue = newValue
                      viewModel.updateNoteInMemory(noteId, newValue.text)
                    },
                    onTextLayout = { result -> textLayoutResult = result },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    textStyle =
                            TextStyle(
                                    color = onSurfaceText,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                            ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                      Box {
                        if (textFieldValue.text.isEmpty()) {
                          Text(
                                  stringResource(R.string.editor_placeholder),
                                  style = MaterialTheme.typography.bodyLarge,
                                  color = onSurfaceText.copy(alpha = 0.38f)
                          )
                        }
                        innerTextField()
                      }
                    }
            )
          }
        }
      }

      EditorBottomBar(
              modifier = Modifier,
              noteId = noteId,
              textFieldValue = textFieldValue,
              isPreviewMode = isPreviewMode,
              viewModel = viewModel,
              onTextFieldUpdate = {
                textFieldValue = it
                focusRequestVersion++
              },
              bgColor = surfaceBg,
              textColor = onSurfaceText
      )
    }

    if (showRenameDialog) {
      var renameValue by remember { mutableStateOf(note.displayTitle("")) }
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
                TextButton(
                        onClick = {
                          viewModel.renameNote(context, noteId, renameValue)
                          showRenameDialog = false
                        }
                ) { Text(stringResource(R.string.rename)) }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditorBottomBar(
        modifier: Modifier,
        noteId: String,
        textFieldValue: TextFieldValue,
        isPreviewMode: Boolean,
        viewModel: FileViewModel,
        onTextFieldUpdate: (TextFieldValue) -> Unit,
        bgColor: Color,
        textColor: Color
) {
  val context = LocalContext.current
  var showInsertSheet by remember { mutableStateOf(false) }
  var showColorSheet by remember { mutableStateOf(false) }
  var showFormatSheet by remember { mutableStateOf(false) }
  var showMoreMenu by remember { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current

  if (isPreviewMode) return

  Surface(
          modifier =
                  modifier.fillMaxWidth()
                          .padding(horizontal = 16.dp, vertical = 12.dp)
                          .navigationBarsPadding()
                          .windowInsetsPadding(WindowInsets.ime)
                          .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
          shape = CircleShape,
          color = bgColor,
          tonalElevation = 6.dp,
          shadowElevation = 0.dp
  ) {
    Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
              onClick = {
                focusManager.clearFocus()
                showInsertSheet = true
              }
      ) {
        Icon(
                Icons.Default.Add,
                stringResource(R.string.editor_insert),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      IconButton(
              onClick = {
                focusManager.clearFocus()
                showColorSheet = true
              }
      ) {
        Icon(
                Icons.Default.Palette,
                stringResource(R.string.editor_bg_color),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      IconButton(
              onClick = {
                focusManager.clearFocus()
                showFormatSheet = true
              }
      ) {
        Icon(
                Icons.Default.TextFields,
                stringResource(R.string.editor_format),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Box {
        IconButton(onClick = { showMoreMenu = true }) {
          Icon(
                  Icons.Default.MoreVert,
                  stringResource(R.string.editor_more),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
          DropdownMenuItem(
                  text = { Text(stringResource(R.string.editor_delete)) },
                  onClick = {
                    viewModel.deleteNote(context, noteId)
                    showMoreMenu = false
                  },
                  leadingIcon = {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                  }
          )
          DropdownMenuItem(
                  text = { Text(stringResource(R.string.editor_copy)) },
                  onClick = {
                    viewModel.copyNote(context, noteId)
                    showMoreMenu = false
                  },
                  leadingIcon = {
                    Icon(
                            Icons.Default.ContentCopy,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
          )
          DropdownMenuItem(
                  text = { Text(stringResource(R.string.editor_share)) },
                  onClick = {
                    viewModel.shareNote(context, noteId)
                    showMoreMenu = false
                  },
                  leadingIcon = {
                    Icon(
                            Icons.Default.Share,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
          )
        }
      }
    }
  }

  if (showInsertSheet) {
    InsertSheet(
            onDismiss = { showInsertSheet = false },
            textFieldValue = textFieldValue,
            onTextFieldUpdate = {
              onTextFieldUpdate(it)
              viewModel.updateNoteInMemory(noteId, it.text)
            }
    )
  }
  if (showColorSheet) {
    ColorSheet(onDismiss = { showColorSheet = false }) { colorLong ->
      viewModel.setNoteBackgroundColor(context, noteId, colorLong)
      showColorSheet = false
    }
  }
  if (showFormatSheet) {
    FormatSheet(
            onDismiss = { showFormatSheet = false },
            textFieldValue = textFieldValue,
            onTextFieldUpdate = {
              onTextFieldUpdate(it)
              viewModel.updateNoteInMemory(noteId, it.text)
            }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsertSheet(
        onDismiss: () -> Unit,
        textFieldValue: TextFieldValue,
        onTextFieldUpdate: (TextFieldValue) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  ModalBottomSheet(
          onDismissRequest = onDismiss,
          sheetState = sheetState,
          shape = MaterialTheme.shapes.extraLarge,
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  ) {
    Column(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
      Text(
              stringResource(R.string.editor_insert_title),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
      )
      SheetItem(
              icon = {
                Icon(Icons.Default.Code, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
              },
              label = stringResource(R.string.editor_insert_code)
      ) {
        insertAtCursor(textFieldValue, "\n```\n\n```\n", onTextFieldUpdate, cursorOffset = 5)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.GridView,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_insert_table)
      ) {
        insertAtCursor(
                textFieldValue,
                "\n| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Cell     | Cell     | Cell     |\n| Cell     | Cell     | Cell     |\n",
                onTextFieldUpdate,
                cursorOffset = 3
        )
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.FormatListNumbered,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_insert_numbered)
      ) {
        insertAtCurrentLine(textFieldValue, "1. ", onTextFieldUpdate)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.AutoMirrored.Filled.FormatListBulleted,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_insert_bulleted)
      ) {
        insertAtCurrentLine(textFieldValue, "- ", onTextFieldUpdate)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.Functions,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_insert_math)
      ) {
        insertAtCursor(textFieldValue, "$$\n\n$$", onTextFieldUpdate, cursorOffset = 3)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(Icons.Default.Link, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
              },
              label = stringResource(R.string.editor_insert_link)
      ) {
        val sel = textFieldValue.selection
        val linkText = "[title](url)"
        val newText =
                textFieldValue.text.substring(0, sel.start) +
                        linkText +
                        textFieldValue.text.substring(sel.end)
        onTextFieldUpdate(
                TextFieldValue(text = newText, selection = TextRange(sel.start + 1, sel.start + 6))
        )
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSheet(onDismiss: () -> Unit, onColorSelected: (Long) -> Unit) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  val isDark = isSystemInDarkTheme()
  ModalBottomSheet(
          onDismissRequest = onDismiss,
          sheetState = sheetState,
          shape = MaterialTheme.shapes.extraLarge,
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  ) {
    Column(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
      Text(
              stringResource(R.string.editor_color_monet),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      MonetPalette.entries.chunked(2).forEach { row ->
        Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          row.forEach { monet ->
            val bg =
                    MonetPalette.bgColorFor(
                            monet.bgColorLong,
                            MaterialTheme.colorScheme.surface,
                            darkTheme = isDark
                    )
            val tColor = MonetPalette.textColorFor(monet.bgColorLong, darkTheme = isDark)
            val theme = com.yuzlyn.mdeditor.data.MonetColorTheme.forBgColor(monet.bgColorLong)
            if (theme != null) {
              Log.d(TAG, "莫奈卡片色彩組件熱刷新：[${theme.name}] isDark=$isDark")
            }
            Surface(
                    onClick = {
                      onColorSelected(monet.bgColorLong)
                      scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = bg,
                    tonalElevation = 2.dp,
                    modifier = Modifier.weight(1f)
            ) {
              Column(
                      Modifier.fillMaxWidth().padding(vertical = 16.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(Modifier.size(32.dp).background(tColor, CircleShape))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        stringResource(monet.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        color = tColor
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FormatSheet(
        onDismiss: () -> Unit,
        textFieldValue: TextFieldValue,
        onTextFieldUpdate: (TextFieldValue) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  ModalBottomSheet(
          onDismissRequest = onDismiss,
          sheetState = sheetState,
          shape = MaterialTheme.shapes.extraLarge,
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  ) {
    Column(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
      Text(
              stringResource(R.string.editor_format_title),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
              Modifier.fillMaxWidth().padding(horizontal = 24.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        (1..6).forEach { level ->
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Surface(
                    onClick = {
                      insertAtCurrentLine(
                              textFieldValue,
                              "#".repeat(level) + " ",
                              onTextFieldUpdate
                      )
                      scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                      "H$level",
                      style = MaterialTheme.typography.labelLarge,
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.ContentCopy,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_format_bold)
      ) {
        insertMarkdownSyntax(textFieldValue, onTextFieldUpdate)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.ContentCopy,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_format_italic)
      ) {
        insertItalicSyntax(textFieldValue, onTextFieldUpdate)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.ContentCopy,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_format_underline)
      ) {
        insertAtCursor(textFieldValue, "<u></u>", onTextFieldUpdate, cursorOffset = 3)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      SheetItem(
              icon = {
                Icon(
                        Icons.Default.ContentCopy,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = stringResource(R.string.editor_format_strikethrough)
      ) {
        insertStrikethroughSyntax(textFieldValue, onTextFieldUpdate)
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun SheetItem(icon: @Composable () -> Unit, label: String, onClick: () -> Unit) {
  Row(
          Modifier.fillMaxWidth()
                  .clickable(onClick = onClick)
                  .padding(horizontal = 24.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Spacer(modifier = Modifier.width(16.dp))
    Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
    )
  }
}

private fun insertAtCursor(
        currentValue: TextFieldValue,
        text: String,
        onUpdate: (TextFieldValue) -> Unit,
        cursorOffset: Int = text.length
) {
  val sel = currentValue.selection
  val newText =
          currentValue.text.substring(0, sel.start) + text + currentValue.text.substring(sel.end)
  onUpdate(TextFieldValue(text = newText, selection = TextRange(sel.start + cursorOffset)))
}

private fun insertAtCurrentLine(
        currentValue: TextFieldValue,
        prefix: String,
        onUpdate: (TextFieldValue) -> Unit
) {
  val text = currentValue.text
  val sel = currentValue.selection
  val lineStart = text.lastIndexOf('\n', sel.start - 1) + 1
  onUpdate(
          TextFieldValue(
                  text = text.substring(0, lineStart) + prefix + text.substring(lineStart),
                  selection = TextRange(sel.start + prefix.length)
          )
  )
}

private fun insertMarkdownSyntax(currentValue: TextFieldValue, onUpdate: (TextFieldValue) -> Unit) {
  val sel = currentValue.selection
  val text = currentValue.text
  if (sel.length > 0) {
    val s = text.substring(sel.start, sel.end)
    onUpdate(
            TextFieldValue(
                    text = text.substring(0, sel.start) + "**$s**" + text.substring(sel.end),
                    selection = TextRange(sel.start + 2, sel.end + 2)
            )
    )
  } else {
    val prefix = if (text.endsWith("\n") || text.isEmpty()) "**" else "\n**"
    onUpdate(
            TextFieldValue(
                    text = text + prefix + "**",
                    selection = TextRange(text.length + prefix.length)
            )
    )
  }
}

private fun insertItalicSyntax(currentValue: TextFieldValue, onUpdate: (TextFieldValue) -> Unit) {
  val sel = currentValue.selection
  val text = currentValue.text
  if (sel.length > 0) {
    val s = text.substring(sel.start, sel.end)
    onUpdate(
            TextFieldValue(
                    text = text.substring(0, sel.start) + "*$s*" + text.substring(sel.end),
                    selection = TextRange(sel.start + 1, sel.end + 1)
            )
    )
  } else {
    val prefix = if (text.endsWith("\n") || text.isEmpty()) "*" else "\n*"
    onUpdate(
            TextFieldValue(
                    text = text + prefix + "*",
                    selection = TextRange(text.length + prefix.length)
            )
    )
  }
}

private fun insertStrikethroughSyntax(
        currentValue: TextFieldValue,
        onUpdate: (TextFieldValue) -> Unit
) {
  val sel = currentValue.selection
  val text = currentValue.text
  if (sel.length > 0) {
    val s = text.substring(sel.start, sel.end)
    onUpdate(
            TextFieldValue(
                    text = text.substring(0, sel.start) + "~~$s~~" + text.substring(sel.end),
                    selection = TextRange(sel.start + 2, sel.end + 2)
            )
    )
  } else {
    val prefix = if (text.endsWith("\n") || text.isEmpty()) "~~" else "\n~~"
    onUpdate(
            TextFieldValue(
                    text = text + prefix + "~~",
                    selection = TextRange(text.length + prefix.length)
            )
    )
  }
}
