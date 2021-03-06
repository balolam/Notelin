package imangazaliev.notelin.mvp.presenters

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import imangazaliev.notelin.NotelinApplication
import imangazaliev.notelin.bus.NoteDeleteAction
import imangazaliev.notelin.bus.NoteEditAction
import imangazaliev.notelin.mvp.common.SortDate
import imangazaliev.notelin.mvp.common.SortName
import imangazaliev.notelin.mvp.models.Note
import imangazaliev.notelin.mvp.models.NoteWrapper
import imangazaliev.notelin.mvp.views.MainView
import imangazaliev.notelin.ui.activities.NoteActivity
import imangazaliev.notelin.utils.PrefsUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import javax.inject.Inject

@InjectViewState
class MainPresenter : MvpPresenter<MainView> {

    enum class SortNotesBy {
        DATE, NAME
    }

    @Inject
    lateinit var mNoteWrapper: NoteWrapper
    lateinit var mNotesList: ArrayList<Note>

    constructor() : super() {
        NotelinApplication.graph.inject(this)
        EventBus.getDefault().register(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadAllNotes()
    }

    /**
     * Загружает все существующие заметки и передает во View
     */
    fun loadAllNotes() {
        mNotesList = mNoteWrapper.loadAllNotes() as ArrayList<Note>
        Collections.sort(mNotesList, getSortComparator(getCurrentSortMethod()))
        viewState.onNotesLoaded(mNotesList)
    }

    /**
     * Удаляет все существующие заметки
     */
    fun deleteAllNotes() {
        mNoteWrapper.deleteAllNotes()
        mNotesList.removeAll(mNotesList)
        viewState.onAllNotesDeleted()
    }

    /**
     * Удаляет заметку по позиции
     */
    fun deleteNoteByPosition(position: Int) {
        val note = mNotesList[position];
        mNoteWrapper.deleteNote(note)
        mNotesList.remove(note)
        viewState.onNoteDeleted()
    }

    fun openNewNote(activity: Activity) {
        val newNote = mNoteWrapper.createNote()
        mNotesList.add(newNote)
        sortNotesBy(getCurrentSortMethod())
        openNote(activity, mNotesList.indexOf(newNote))
    }

    /**
     * Открывает активити с заметкой по позиции
     */
    fun openNote(activity: Activity, position: Int) {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_position", position)
        intent.putExtra("note_id", mNotesList[position].id)
        activity.startActivity(intent)
    }

    /**
     * Ищет заметку по имени
     */
    fun search(query: String) {
        if (query.equals("")) {
            viewState.onSearchResult(mNotesList)
        } else {
            val searchResults = mNotesList.filter { note -> note.title!!.toLowerCase().startsWith(query.toLowerCase()) }
            viewState.onSearchResult(searchResults as ArrayList<Note>)
        }
    }

    /**
     * Сортирует заметки
     */
    fun sortNotesBy(sortMethod: SortNotesBy) {
        mNotesList.sortWith(getSortComparator(sortMethod))
        PrefsUtils.setNotesSortMethod(sortMethod.toString())
        viewState.updateView()
    }

    fun getCurrentSortMethod(): SortNotesBy {
        val defaultSortMethodName = SortNotesBy.DATE.toString()
        val currentSortMethodName = PrefsUtils.getNotesSortMethodName(defaultSortMethodName)
        return SortNotesBy.valueOf(currentSortMethodName)
    }

    fun getSortComparator(sortMethod: SortNotesBy): Comparator<Note> {
        when (sortMethod) {
            SortNotesBy.NAME -> return SortName()
            SortNotesBy.DATE -> return SortDate()
        }
    }

    /**
     * Срабатывает при сохранении заметки на экране редактирования
     */
    @Subscribe
    fun onNoteEdit(action: NoteEditAction) {
        val notePosition = action.position
        mNotesList[notePosition] = mNoteWrapper.getNoteById(mNotesList[notePosition].id) //обновляем заметку по позиции
        sortNotesBy(getCurrentSortMethod())
    }

    /**
     * Срабатывает при удалении заметки на экране редактирования
     */
    @Subscribe
    fun onNoteDelete(action: NoteDeleteAction) {
        mNotesList.removeAt(action.position)
        viewState.updateView()
    }

    /**
     * Показывает контекстное меню заметки
     */
    fun showNoteContextDialog(position: Int) {
        viewState.showNoteContextDialog(position)
    }

    /**
     * Прячет контекстное меню заметки
     */
    fun hideNoteContextDialog() {
        viewState.hideNoteContextDialog()
    }

    /**
     * Показывает диалог удаления заметки
     */
    fun showNoteDeleteDialog(position: Int) {
        viewState.showNoteDeleteDialog(position)
    }

    /**
     * Прячет диалог удаления заметки
     */
    fun hideNoteDeleteDialog() {
        viewState.hideNoteDeleteDialog()
    }

    /**
     * Показывает диалог с информацией о заметке
     */
    fun showNoteInfo(position: Int) {
        viewState.showNoteInfoDialog(mNotesList[position].getInfo())
    }

    /**
     * Прячет диалог с информацией о заметке
     */
    fun hideNoteInfoDialog() {
        viewState.hideNoteInfoDialog()
    }

}
