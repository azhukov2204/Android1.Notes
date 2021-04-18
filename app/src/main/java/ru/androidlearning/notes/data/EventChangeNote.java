package ru.androidlearning.notes.data;

public class EventChangeNote {
    private final int newIndexOfNote;
    private final ChangeNoteTypes changeNoteType;

    public EventChangeNote(int currentIndexOfNote, ChangeNoteTypes changeNoteType) {
        this.newIndexOfNote = currentIndexOfNote;
        this.changeNoteType = changeNoteType;
    }

    public int getNewIndexOfNote() {
        return newIndexOfNote;
    }

    public ChangeNoteTypes getChangeNoteType() {
        return changeNoteType;
    }
}
