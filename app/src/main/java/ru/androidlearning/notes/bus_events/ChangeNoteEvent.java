package ru.androidlearning.notes.bus_events;

public class ChangeNoteEvent {
    private final int newIndexOfNote;
    private final ChangeNoteTypes changeNoteType;

    public ChangeNoteEvent(int currentIndexOfNote, ChangeNoteTypes changeNoteType) {
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
