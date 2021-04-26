package ru.androidlearning.notes.data;

import java.util.List;

public interface Notes {
    Notes initNotes(boolean isTest);

    Notes initNotes(NotesResponse notesResponse);

    void addNote(String title, String noteText, String noteDate);

    void deleteNoteByIndex(int index);

    void updateNoteByIndex(int index, String noteTitle, String noteText, String noteDate);

    String getNoteTitleByIndex(int index);

    String getNoteTextByIndex(int index);

    String getNoteFormattedCreatedDateAsStringByIndex(int index);

    NoteEntry getNoteEntryByIndex(int index);

    List<String> getAllNotesTitles();

}
