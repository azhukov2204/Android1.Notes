package ru.androidlearning.notes.models;

public class GetNotes {
    private static Notes notes = null;

    public static Notes getNotes() {
        if (notes == null) {
            notes = new Notes();
        }
        return notes;
    }

    public static Notes getNotes(boolean isTest) {
        if (notes == null) {
            notes = new Notes(isTest);
        }
        return notes;
    }

}
