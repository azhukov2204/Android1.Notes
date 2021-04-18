package ru.androidlearning.notes.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    public NoteEntry getNoteEntryByIndex(int index) {
        return notesList.get(index);
    }

    public List<String> getAllNotesTitles() {
        List<String> allNoteTitles = new ArrayList<>();
        for (NoteEntry note : notesList) {
            allNoteTitles.add(note.getNoteTitle());
        }
        return allNoteTitles;
    }


    public static class NoteEntry {
        private String noteTitle;
        private String noteText;
        private String noteCreatedDate;

        public NoteEntry(String noteTitle, String noteText, String noteCreatedDate) {
            this.noteTitle = noteTitle;
            this.noteText = noteText;
            this.noteCreatedDate = noteCreatedDate;
        }

        public NoteEntry(String noteTitle, String noteText) {
            this.noteTitle = noteTitle;
            this.noteText = noteText;
            Calendar calendar = Calendar.getInstance();
            this.noteCreatedDate = String.format(Locale.US, "%02d.%02d.%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        }

        public String getNoteTitle() {
            return noteTitle;
        }

        public String getNoteText() {
            return noteText;
        }

        public String getNoteCreatedDate() {
            return noteCreatedDate;
        }


        public void setNoteTitle(String noteTitle) {
            this.noteTitle = noteTitle;
        }

        public void setNoteText(String noteText) {
            this.noteText = noteText;
        }

        public void setNoteCreatedDate(String noteCreatedDate) {
            this.noteCreatedDate = noteCreatedDate;
        }
    }
}
