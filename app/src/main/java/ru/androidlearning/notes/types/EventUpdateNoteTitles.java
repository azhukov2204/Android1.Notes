package ru.androidlearning.notes.types;

public class EventUpdateNoteTitles {
    private final int currentIndexOfNote;

    public EventUpdateNoteTitles(int currentIndexOfNote) {
        this.currentIndexOfNote = currentIndexOfNote;
    }

    public int getCurrentIndexOfNote() {
        return currentIndexOfNote;
    }
}
