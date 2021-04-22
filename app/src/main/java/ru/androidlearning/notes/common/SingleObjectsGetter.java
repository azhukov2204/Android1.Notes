package ru.androidlearning.notes.common;

import com.squareup.otto.Bus;

import ru.androidlearning.notes.data.Notes;
import ru.androidlearning.notes.data.NotesImpl;

public class SingleObjectsGetter {
    private static Notes notes = null;
    private static Bus bus = null;
    private static final boolean TEST_MODE = true;

    public static Notes getNotes() {
        if (notes == null) {
            notes = new NotesImpl();
            notes.initNotes(TEST_MODE);
        }
        return notes;
    }

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus();
        }
        return bus;
    }
}
