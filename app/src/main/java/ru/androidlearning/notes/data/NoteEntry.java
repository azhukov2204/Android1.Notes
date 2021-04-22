package ru.androidlearning.notes.data;

import java.util.Calendar;
import java.util.Locale;

public class NoteEntry {
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
