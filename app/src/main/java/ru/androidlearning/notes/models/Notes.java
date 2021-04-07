package ru.androidlearning.notes.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.androidlearning.notes.types.NoteEntryType;

public class Notes {
    private final List<NoteEntryType> notesList;

    public Notes() {
        notesList = new ArrayList<>();
    }

    public Notes(boolean isTest) {
        if (!isTest) {
            notesList = new ArrayList<>();
        } else {
            notesList = Arrays.asList(
                    new NoteEntryType("title1", "massage1"),
                    new NoteEntryType("title2", "message2"),
                    new NoteEntryType("title3", "message3"),
                    new NoteEntryType("title4", "message4"),
                    new NoteEntryType("title5", "message5"),
                    new NoteEntryType("title6", "message6"),
                    new NoteEntryType("title7", "message7")
            );
        }

    }

    public void addNote(String title, String noteText) {
        notesList.add(new NoteEntryType(title, noteText));
    }

    public void deleteNoteByIndex(int index) {
        notesList.remove(index);
    }

    public String getNoteTitleByIndex(int index) {
        return notesList.get(index).getNoteTitle();
    }

    public String getNoteTextByIndex(int index) {
        return notesList.get(index).getNoteText();
    }

    public String getNoteFormattedCreatedDateAsStringByIndex(int index) {
        return notesList.get(index).getFormattedCreatedDateAsString();
    }

    public List<String> getAllNotesTitles() {
        List<String> allNoteTitles = new ArrayList<>();
        for (NoteEntryType note : notesList) {
            allNoteTitles.add(note.getNoteTitle());
        }
        return allNoteTitles;
    }


}
