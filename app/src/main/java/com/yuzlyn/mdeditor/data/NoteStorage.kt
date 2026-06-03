package com.yuzlyn.mdeditor.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NoteStorage {

  private val _notes = MutableStateFlow<List<NoteModel>>(emptyList())
  val notes: StateFlow<List<NoteModel>> = _notes.asStateFlow()

  fun getAllNotes(): List<NoteModel> = _notes.value

  fun getNoteById(id: String): NoteModel? = _notes.value.find { it.id == id }

  fun getNotebookTags(): List<String> =
          _notes.value
                  .filter { !it.isArchived && !it.isDeleted }
                  .map { it.notebookTag }
                  .distinct()
                  .sorted()

  fun getNotesByTag(tag: String): List<NoteModel> =
          _notes.value.filter { it.notebookTag == tag && !it.isArchived && !it.isDeleted }

  fun getNotebookCounts(): Map<String, Int> =
          _notes.value
                  .filter { !it.isArchived && !it.isDeleted }
                  .groupBy { it.notebookTag }
                  .mapValues { it.value.size }

  fun changeNotebookTag(noteId: String, newTag: String) {
    _notes.update { current ->
      current.map { note -> if (note.id == noteId) note.copy(notebookTag = newTag) else note }
    }
  }

  fun getDeletedNotes(): List<NoteModel> =
          _notes.value.filter { it.isDeleted }.sortedByDescending { it.deletedAt }

  fun getActiveNotes(): List<NoteModel> = _notes.value.filter { !it.isDeleted }

  fun replaceAll(newNotes: List<NoteModel>) {
    _notes.update { newNotes }
  }

  fun addNote(note: NoteModel) {
    _notes.update { current ->
      if (current.any { it.id == note.id }) {
        current.map { if (it.id == note.id) note else it }
      } else {
        current + note
      }
    }
  }

  fun updateNote(id: String, content: String, lastModified: Long) {
    _notes.update { current ->
      current.map { note ->
        if (note.id == id) note.copy(content = content, lastModified = lastModified) else note
      }
    }
  }

  fun updateNoteFields(id: String, transform: (NoteModel) -> NoteModel) {
    _notes.update { current ->
      current.map { note -> if (note.id == id) transform(note) else note }
    }
  }

  fun togglePin(id: String) {
    _notes.update { current ->
      current.map { note -> if (note.id == id) note.copy(isPinned = !note.isPinned) else note }
    }
  }

  fun toggleArchive(id: String) {
    _notes.update { current ->
      current.map { note -> if (note.id == id) note.copy(isArchived = !note.isArchived) else note }
    }
  }

  fun setBackgroundColor(id: String, color: Long) {
    _notes.update { current ->
      current.map { note -> if (note.id == id) note.copy(backgroundColor = color) else note }
    }
  }

  fun renameNote(id: String, newTitle: String) {
    _notes.update { current ->
      current.map { note ->
        if (note.id == id) note.copy(title = newTitle, isTitleCustom = true) else note
      }
    }
  }

  fun autoTitleFromContent(id: String) {
    _notes.update { current ->
      current.map { note ->
        if (note.id == id && !note.isTitleCustom) {
          val firstLine = note.content.lines().firstOrNull()?.trim() ?: ""
          note.copy(title = firstLine)
        } else note
      }
    }
  }

  fun softDeleteNote(id: String) {
    _notes.update { current ->
      current.map { note ->
        if (note.id == id) note.copy(isDeleted = true, deletedAt = System.currentTimeMillis())
        else note
      }
    }
  }

  fun restoreNote(id: String) {
    _notes.update { current ->
      current.map { note ->
        if (note.id == id) note.copy(isDeleted = false, deletedAt = 0L) else note
      }
    }
  }

  fun permanentlyRemoveNote(id: String) {
    _notes.update { current -> current.filter { it.id != id } }
  }

  fun expireTrash(retentionMs: Long = 7 * 24 * 60 * 60 * 1000L): List<NoteModel> {
    val cutoff = System.currentTimeMillis() - retentionMs
    val expired = _notes.value.filter { it.isDeleted && it.deletedAt < cutoff }
    _notes.update { current -> current.filterNot { it in expired } }
    return expired
  }

  fun removeNote(id: String) {
    _notes.update { current -> current.filter { it.id != id } }
  }

  fun noteCount(): Int = _notes.value.size
}
