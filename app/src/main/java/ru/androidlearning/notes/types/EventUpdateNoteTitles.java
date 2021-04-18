package ru.androidlearning.notes.types;

public class EventUpdateNoteTitles {
    private final int newIndexOfNote;

    public EventUpdateNoteTitles(int currentIndexOfNote) {
        this.newIndexOfNote = currentIndexOfNote;
    }

    public int getNewIndexOfNote() {
        return newIndexOfNote;
    }
}
