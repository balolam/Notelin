package imangazaliev.notelin.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import imangazaliev.notelin.NotelinApplication
import imangazaliev.notelin.bus.NoteDeleteAction
import imangazaliev.notelin.bus.NoteEditAction
import imangazaliev.notelin.mvp.models.Note
import imangazaliev.notelin.mvp.models.NoteWrapper
import imangazaliev.notelin.mvp.views.NoteView
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

@InjectViewState
class NotePresenter : MvpPresenter<NoteView> {

    @Inject
    lateinit var mNoteWrapper: NoteWrapper
    lateinit var mNote: Note
    var mNotePosition: Int = -1

    constructor() : super() {
        NotelinApplication.graph.inject(this)
    }

    fun showNote(noteId: Long, notePosition: Int) {
        mNotePosition = notePosition
        mNote = mNoteWrapper.getNoteById(noteId)
        viewState.showNote(mNote)
    }

    fun saveNote(title: String, text: String) {
        mNote.title = title
        mNote.text = text
        mNote.changeDate = Date()
        mNoteWrapper.saveNote(mNote)
        EventBus.getDefault().post(NoteEditAction(mNotePosition))
        viewState.onNoteSaved()
    }

    fun deleteNote() {
        mNoteWrapper.deleteNote(mNote)
        EventBus.getDefault().post(NoteDeleteAction(mNotePosition))
        viewState.onNoteDeleted()
    }

    fun showNoteDeleteDialog() {
        viewState.showNoteDeleteDialog()
    }

    fun hideNoteDeleteDialog() {
        viewState.hideNoteDeleteDialog()
    }

    fun showNoteInfoDialog() {
        viewState.showNoteInfoDialog(mNote.getInfo())
    }

    fun hideNoteInfoDialog() {
        viewState.hideNoteInfoDialog()
    }

}
