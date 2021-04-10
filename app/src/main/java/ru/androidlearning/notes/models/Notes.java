package ru.androidlearning.notes.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.androidlearning.notes.types.NoteEntry;

public class Notes {
    private final List<NoteEntry> notesList;

    public Notes() {
        notesList = new ArrayList<>();
    }

    public Notes(boolean isTest) {
        notesList = new ArrayList<>();
        if (isTest) {
            notesList.addAll(Arrays.asList(
                    new NoteEntry("title1", "massage1"),
                    new NoteEntry("title2", "message2"),
                    new NoteEntry("title3", "message3"),
                    new NoteEntry("title4", "message4"),
                    new NoteEntry("title5", "message5"),
                    new NoteEntry("title6", "message6"),
                    new NoteEntry("title7", "message7")));
        }
    }

    public void addNote(String title, String noteText, String noteDate) {
        notesList.add(new NoteEntry(title, noteText, noteDate));
    }

    public void deleteNoteByIndex(int index) {
        notesList.remove(index);
    }

    public void updateNoteByIndex(int index, String noteTitle, String noteText, String noteDate) {
        notesList.get(index).setNoteTitle(noteTitle);
        notesList.get(index).setNoteText(noteText);
        notesList.get(index).setNoteCreatedDate(noteDate);
    }

    public String getNoteTitleByIndex(int index) {
        return notesList.get(index).getNoteTitle();
    }

    public String getNoteTextByIndex(int index) {
        return notesList.get(index).getNoteText();
    }

    public String getNoteFormattedCreatedDateAsStringByIndex(int index) {
        return notesList.get(index).getNoteCreatedDate();
    }

    public List<String> getAllNotesTitles() {
        List<String> allNoteTitles = new ArrayList<>();
        for (NoteEntry note : notesList) {
            allNoteTitles.add(note.getNoteTitle());
        }
        return allNoteTitles;
    }


}
