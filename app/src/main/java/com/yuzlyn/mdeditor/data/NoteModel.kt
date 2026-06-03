package com.yuzlyn.mdeditor.data

data class NoteModel(
        val id: String,
        val uriString: String,
        val title: String,
        val content: String,
        val notebookTag: String,
        val lastModified: Long,
        val displayPath: String = "",
        val isPinned: Boolean = false,
        val isArchived: Boolean = false,
        val backgroundColor: Long = 0x00000000,
        val isDeleted: Boolean = false,
        val deletedAt: Long = 0L,
        val isTitleCustom: Boolean = false
) {
  fun displayTitle(defaultUntitled: String): String {
    val raw =
            if (isTitleCustom || title.isNotBlank()) {
              title.ifBlank { content.lines().firstOrNull()?.take(50) ?: defaultUntitled }
            } else {
              content.lines().firstOrNull()?.take(50) ?: defaultUntitled
            }
    return raw.stripMarkdown().ifBlank { defaultUntitled }
  }
}

private fun String.stripMarkdown(): String {
  var s = this
  s = s.replace(Regex("^#{1,6}\\s+"), "")
  s = s.replace("**", "").replace("__", "")
  s = s.replace("*", "").replace("_", "")
  s = s.replace("`", "")
  s = s.replace("~~", "")
  return s.trim()
}
