package imangazaliev.notelin.di

import dagger.Module
import dagger.Provides
import imangazaliev.notelin.mvp.models.NoteWrapper
import javax.inject.Singleton

@Module
class NoteWrapperModule {

    @Provides
    @Singleton
    fun provideNoteModel(): NoteWrapper = NoteWrapper()

}