package com.yuzlyn.mdeditor.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuzlyn.mdeditor.data.NoteModel
import com.yuzlyn.mdeditor.data.NoteStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class FileViewModel : ViewModel() {

  companion object {
    private const val TAG = "MDEditor_Debug"
    private const val PREFS_NAME = "mdeditor_saf_prefs"
    private const val KEY_TREE_URI = "saf_tree_uri"
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
  }

  private var fileLoadingJob: Job? = null
  private var hasImportedFromUri = false

  private fun buildFrontMatter(note: NoteModel): String {
    val json =
            JSONObject().apply {
              put("bg", note.backgroundColor)
              if (note.notebookTag.isNotBlank() && note.notebookTag != "根目录") {
                put("notebook", note.notebookTag)
              }
              if (note.isPinned) put("pinned", true)
              if (note.isArchived) put("archived", true)
              if (note.isDeleted) {
                put("deleted", true)
                put("deletedAt", note.deletedAt)
              }
            }
    return "---\n${json.toString()}\n---\n"
  }

  private fun parseFrontMatter(rawContent: String): Pair<JSONObject?, String> {
    if (!rawContent.startsWith("---\n")) return Pair(null, rawContent)
    val endIndex = rawContent.indexOf("\n---\n", 4)
    if (endIndex == -1) return Pair(null, rawContent)
    val jsonStr = rawContent.substring(4, endIndex)
    val cleanContent = rawContent.substring(endIndex + 5)
    return try {
      Pair(JSONObject(jsonStr), cleanContent)
    } catch (e: Exception) {
      Pair(null, rawContent)
    }
  }

  private val _currentEditNoteId = MutableStateFlow<String?>(null)
  val currentEditNoteId: StateFlow<String?> = _currentEditNoteId.asStateFlow()

  fun setCurrentEditNoteId(noteId: String) {
    _currentEditNoteId.value = noteId
  }

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  fun getSavedTreeUriString(context: Context): String? {
    return getPrefs(context).getString(KEY_TREE_URI, null)
  }

  fun hasFolderSelected(context: Context): Boolean {
    val savedUri = getSavedTreeUriString(context) ?: return false
    return try {
      val uri = Uri.parse(savedUri)
      val docFile = DocumentFile.fromTreeUri(context, uri)
      docFile != null && docFile.exists()
    } catch (e: Exception) {
      Log.e(TAG, "校验已保存 Uri 失败", e)
      false
    }
  }

  fun tryLoadSavedFolder(context: Context) {
    val savedUri =
            getSavedTreeUriString(context)
                    ?: run {
                      Log.d(TAG, "无已保存的 SAF 资料夹 Uri，等待用户选择")
                      return
                    }
    if (hasImportedFromUri) {
      Log.d(TAG, "SAF 导入已执行过，跳过重复导入")
      return
    }
    Log.d(TAG, "尝试从已保存 SAF Uri 恢复导入：$savedUri")
    importFilesFromUri(context, Uri.parse(savedUri))
  }

  fun selectAndImportFolder(context: Context, treeUri: Uri) {
    Log.d(TAG, "用户选定资料夹 treeUri：$treeUri")

    context.contentResolver.takePersistableUriPermission(
            treeUri,
            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )
    Log.d(TAG, "成功锁定持久化目录 Uri 权限")

    getPrefs(context).edit().putString(KEY_TREE_URI, treeUri.toString()).apply()
    Log.d(TAG, "持久化 treeUri 至 SharedPreferences：$treeUri")

    importFilesFromUri(context, treeUri)
  }

  private fun importFilesFromUri(context: Context, treeUri: Uri) {
    fileLoadingJob?.cancel()
    fileLoadingJob =
            viewModelScope.launch(Dispatchers.IO) {
              if (hasImportedFromUri) {
                Log.d(TAG, "SAF 已导入，跳过")
                return@launch
              }
              hasImportedFromUri = true

              Log.d(TAG, "SAF 开始全盘读取 md 档案：$treeUri")

              val rootDoc = DocumentFile.fromTreeUri(context, treeUri)
              if (rootDoc == null || !rootDoc.exists()) {
                Log.e(TAG, "SAF 目录无效或不存在")
                return@launch
              }

              val notes = mutableListOf<NoteModel>()
              collectMdFiles(context, rootDoc, rootDoc.name ?: "根目录", notes)

              NoteStorage.replaceAll(notes)
              Log.d(TAG, "SAF 一次性导入完成：加载 ${notes.size} 条笔记到内存，关闭磁碟通道")

              val expiredNotes = NoteStorage.expireTrash()
              expiredNotes.forEach { expired ->
                try {
                  val uri = Uri.parse(expired.uriString)
                  DocumentsContract.deleteDocument(context.contentResolver, uri)
                  Log.d(TAG, "过期清理已删除文件：${expired.title}")
                } catch (e: Exception) {
                  Log.e(TAG, "过期清理失败：${expired.uriString}", e)
                }
              }
              if (expiredNotes.isNotEmpty()) {
                Log.d(TAG, "回收站过期清理完成：移除了 ${expiredNotes.size} 条笔记")
              }
            }
  }

  private fun collectMdFiles(
          context: Context,
          directory: DocumentFile,
          tag: String,
          notes: MutableList<NoteModel>
  ) {
    val children = directory.listFiles()
    children.forEach { child ->
      if (child.isDirectory) {
        collectMdFiles(context, child, child.name ?: tag, notes)
      } else if (child.isFile && child.name?.lowercase()?.endsWith(".md") == true) {
        try {
          val rawContent =
                  context.contentResolver.openInputStream(child.uri)?.bufferedReader()?.use {
                    it.readText()
                  }
                          ?: ""

          val (frontMatter, cleanContent) = parseFrontMatter(rawContent)
          var bgColor = 0x00000000L
          var isPinned = false
          var isArchived = false
          var isDeleted = false
          var deletedAt = 0L
          var notebookFromFm: String? = null
          if (frontMatter != null) {
            bgColor = frontMatter.optLong("bg", 0x00000000)
            isPinned = frontMatter.optBoolean("pinned", false)
            isArchived = frontMatter.optBoolean("archived", false)
            isDeleted = frontMatter.optBoolean("deleted", false)
            deletedAt = frontMatter.optLong("deletedAt", 0L)
            notebookFromFm =
                    if (frontMatter.has("notebook")) frontMatter.getString("notebook") else null
          }

          val cleanText = cleanContent.trimStart()
          val title = cleanText.lines().firstOrNull()?.take(50) ?: ""
          val lastModified = child.lastModified()
          val notebookTag = notebookFromFm?.ifBlank { null } ?: tag

          notes.add(
                  NoteModel(
                          id = child.uri.toString(),
                          uriString = child.uri.toString(),
                          title = title,
                          content = cleanText,
                          notebookTag = notebookTag,
                          lastModified = lastModified,
                          displayPath = child.name ?: title.ifBlank { "未命名" },
                          isPinned = isPinned,
                          isArchived = isArchived,
                          backgroundColor = bgColor,
                          isDeleted = isDeleted,
                          deletedAt = deletedAt,
                          isTitleCustom = false
                  )
          )
        } catch (e: Exception) {
          Log.e(TAG, "SAF 读取 md 文件失败：${child.uri}", e)
        }
      }
    }
  }

  fun createNewNote(context: Context, notebookTag: String? = null): NoteModel? {
    val savedUriStr =
            getSavedTreeUriString(context)
                    ?: run {
                      Log.e(TAG, "新建文件失败：无已选资料夹")
                      return null
                    }
    val treeUri = Uri.parse(savedUriStr)
    val rootDoc =
            DocumentFile.fromTreeUri(context, treeUri)
                    ?: run {
                      Log.e(TAG, "新建文件失败：无法解析资料夹")
                      return null
                    }

    val timestamp = System.currentTimeMillis()
    val placeholderTitle = ""

    val fileName = "note_${timestamp}.md"

    Log.d(TAG, "SAF 新建文件：$fileName 在 $treeUri")

    try {
      val newFile = rootDoc.createFile("text/markdown", fileName)
      if (newFile == null) {
        Log.e(TAG, "SAF 新建文件失败：createFile 返回 null")
        return null
      }

      val tag = notebookTag ?: rootDoc.name ?: "根目录"
      val note =
              NoteModel(
                      id = newFile.uri.toString(),
                      uriString = newFile.uri.toString(),
                      title = placeholderTitle,
                      content = "",
                      notebookTag = tag,
                      lastModified = timestamp,
                      displayPath = fileName,
                      isTitleCustom = false
              )

      NoteStorage.addNote(note)

      if (tag != "根目录" && notebookTag != null) {
        saveNoteMetadataToDisk(context, note.id)
      }

      Log.d(TAG, "SAF 新建文件成功：${newFile.uri}, 标签：$tag")
      return note
    } catch (e: Exception) {
      Log.e(TAG, "SAF 新建文件异常", e)
      return null
    }
  }

  fun changeNoteNotebook(context: Context, noteId: String, newTag: String) {
    Log.d(TAG, "移动笔记 $noteId 到笔记本：$newTag")
    NoteStorage.changeNotebookTag(noteId, newTag)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun updateNoteInMemory(id: String, content: String) {
    Log.d(TAG, "内存修改：笔记 $id")
    NoteStorage.updateNote(id, content, System.currentTimeMillis())
    NoteStorage.autoTitleFromContent(id)
  }

  fun renameNote(context: Context, noteId: String, newTitle: String) {
    NoteStorage.renameNote(noteId, newTitle)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun saveNoteToDisk(context: Context, noteId: String) {
    val note = NoteStorage.getNoteById(noteId) ?: return
    Log.d(TAG, "退出并透过安全流写回 Uri：$noteId")

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val uri = Uri.parse(note.uriString)
        val fullContent = buildFrontMatter(note) + note.content
        context.contentResolver.openOutputStream(uri, "wt")?.use { out ->
          out.write(fullContent.toByteArray(Charsets.UTF_8))
        }
        Log.d(TAG, "SAF 流写回成功：$noteId")
      } catch (e: Exception) {
        Log.e(TAG, "SAF 流写回失败：$noteId", e)
      }
    }
  }

  fun deleteNote(context: Context, noteId: String) {
    Log.d(TAG, "软删除笔记（移入回收站）：$noteId")
    NoteStorage.softDeleteNote(noteId)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun restoreNote(context: Context, noteId: String) {
    Log.d(TAG, "恢复笔记（从回收站还原）：$noteId")
    NoteStorage.restoreNote(noteId)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun permanentlyDeleteNote(context: Context, noteId: String) {
    Log.d(TAG, "永久删除笔记：$noteId")
    val note = NoteStorage.getNoteById(noteId) ?: return
    NoteStorage.permanentlyRemoveNote(noteId)

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val uri = Uri.parse(note.uriString)
        DocumentsContract.deleteDocument(context.contentResolver, uri)
        Log.d(TAG, "永久删除文件成功：$noteId")
      } catch (e: Exception) {
        Log.e(TAG, "永久删除文件失败：$noteId", e)
      }
    }
  }

  fun saveNoteMetadataToDisk(context: Context, noteId: String) {
    val note = NoteStorage.getNoteById(noteId) ?: return
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val uri = Uri.parse(note.uriString)
        val existingRaw =
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use {
                  it.readText()
                }
                        ?: ""
        val (_, existingContent) = parseFrontMatter(existingRaw)
        val actualContent = existingContent.ifBlank { note.content }
        val fullContent = buildFrontMatter(note) + actualContent
        context.contentResolver.openOutputStream(uri, "wt")?.use { out ->
          out.write(fullContent.toByteArray(Charsets.UTF_8))
        }
        Log.d(TAG, "元数据落盘成功：$noteId")
      } catch (e: Exception) {
        Log.e(TAG, "元数据落盘失败：$noteId", e)
      }
    }
  }

  fun pinNote(noteId: String) {
    Log.d(TAG, "置顶切换：$noteId")
    NoteStorage.togglePin(noteId)
  }

  fun pinNote(context: Context, noteId: String) {
    pinNote(noteId)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun archiveNote(noteId: String) {
    Log.d(TAG, "封存切换：$noteId")
    NoteStorage.toggleArchive(noteId)
  }

  fun archiveNote(context: Context, noteId: String) {
    archiveNote(noteId)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun setNoteBackgroundColor(noteId: String, color: Long) {
    Log.d(TAG, "背景色变更：$noteId -> $color")
    NoteStorage.setBackgroundColor(noteId, color)
  }

  fun setNoteBackgroundColor(context: Context, noteId: String, color: Long) {
    setNoteBackgroundColor(noteId, color)
    saveNoteMetadataToDisk(context, noteId)
  }

  fun copyNote(context: Context, noteId: String) {
    val original = NoteStorage.getNoteById(noteId) ?: return
    Log.d(TAG, "创建副本：$noteId")

    val savedUriStr =
            getSavedTreeUriString(context)
                    ?: run {
                      Log.e(TAG, "创建副本失败：无已选资料夹")
                      return
                    }
    val treeUri = Uri.parse(savedUriStr)
    val rootDoc = DocumentFile.fromTreeUri(context, treeUri) ?: return

    val timestamp = System.currentTimeMillis()
    val copyTitle = "${original.title}_副本"
    val fileName = "$copyTitle.md"

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val newFile = rootDoc.createFile("text/markdown", fileName)
        if (newFile == null) {
          Log.e(TAG, "创建副本失败：createFile 返回 null")
          return@launch
        }
        val copyNote =
                original.copy(
                        id = newFile.uri.toString(),
                        uriString = newFile.uri.toString(),
                        title = copyTitle,
                        lastModified = timestamp,
                        displayPath = fileName,
                        isPinned = false,
                        isArchived = false,
                        backgroundColor = 0x00000000
                )
        val fullContent = buildFrontMatter(copyNote) + original.content
        context.contentResolver.openOutputStream(newFile.uri, "wt")?.use { out ->
          out.write(fullContent.toByteArray(Charsets.UTF_8))
        }
        NoteStorage.addNote(copyNote)
        Log.d(TAG, "创建副本成功：${newFile.uri}")
      } catch (e: Exception) {
        Log.e(TAG, "创建副本异常", e)
      }
    }
  }

  fun shareNote(context: Context, noteId: String) {
    val note = NoteStorage.getNoteById(noteId) ?: return
    Log.d(TAG, "分享笔记：$noteId")
    val intent =
            android.content.Intent(android.content.Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(android.content.Intent.EXTRA_SUBJECT, note.title)
              putExtra(android.content.Intent.EXTRA_TEXT, note.content)
            }
    context.startActivity(android.content.Intent.createChooser(intent, "分享笔记"))
  }

  fun resetFolderSelection(context: Context) {
    Log.d(TAG, "重置资料夹选择")
    fileLoadingJob?.cancel()
    hasImportedFromUri = false
    NoteStorage.replaceAll(emptyList())
    getPrefs(context).edit().remove(KEY_TREE_URI).apply()
  }

  fun getDateFormatted(timestamp: Long): String {
    return dateFormat.format(Date(timestamp))
  }

  override fun onCleared() {
    super.onCleared()
    fileLoadingJob?.cancel()
  }
}
