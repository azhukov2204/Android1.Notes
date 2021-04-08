package ru.androidlearning.notes.types;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteEntry {
    private String noteTitle;
    private String noteText;
    private Date noteCreatedDate;

    public NoteEntry(String noteTitle, String noteText, Date noteCreatedDate) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.noteCreatedDate = noteCreatedDate;
    }

    public NoteEntry(String noteTitle, String noteText) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.noteCreatedDate = new Date();
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public Date getNoteCreatedDate() {
        return noteCreatedDate;
    }

    public String getFormattedCreatedDateAsString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US);
        return timeFormat.format(noteCreatedDate);
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setNoteCreatedDate(Date noteCreatedDate) {
        this.noteCreatedDate = noteCreatedDate;
    }
}
