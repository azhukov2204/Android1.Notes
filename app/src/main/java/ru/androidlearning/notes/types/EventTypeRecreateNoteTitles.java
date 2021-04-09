package ru.androidlearning.notes.types;

public class EventTypeRecreateNoteTitles {
    private final int currentIndexOfNote;

    public EventTypeRecreateNoteTitles(int currentIndexOfNote) {
        this.currentIndexOfNote = currentIndexOfNote;
    }

    public int getCurrentIndexOfNote() {
        return currentIndexOfNote;
    }
}
