package ru.androidlearning.notes.data;
import com.squareup.otto.Bus;

public class SingleObjectsGetter {
    private static Notes notes = null;
    private static Bus bus = null;

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

    public static Bus getBus() {
        if(bus == null) {
            bus = new Bus();
        }
        return bus;
    }
}
