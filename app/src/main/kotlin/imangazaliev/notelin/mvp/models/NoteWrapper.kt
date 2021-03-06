package imangazaliev.notelin.mvp.models

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import java.util.*

class NoteWrapper {

    /**
     * Создает новую заметку
     */
    fun createNote(): Note {
        var note = Note("Новая заметка", Date())
        note.save()
        return note
    }

    /**
     * Сохраняет заметку в БД
     */
    fun saveNote(note: Note) : Long {
        return note.save()
    }

    /**
     * Загружает все существующие заметки и передает во View
     */
    fun loadAllNotes() : List<Note> {
        return Select().from(Note::class.java).execute<Note>()
    }

    /**
     * Ищет заметку по id и возвращает ее
     */
    fun getNoteById(noteId:Long) : Note {
        return Select().from(Note::class.java).where("id = ?", noteId).executeSingle<Note>()
    }

    /**
     * Удаляет все существующие заметки
     */
    fun deleteAllNotes()  {
        Delete().from(Note::class.java).execute<Note>();
    }

    /**
     * Удаляет заметку по id
     */
    fun deleteNote(note:Note)  {
        note.delete()
    }

}
