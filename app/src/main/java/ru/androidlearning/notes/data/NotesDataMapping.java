package ru.androidlearning.notes.data;

import java.util.HashMap;
import java.util.Map;

public class NotesDataMapping {
    public static class Fields {
        public final static String NOTE_TITLE = "noteTitle";
        public final static String NOTE_TEXT = "noteText";
        public final static String NOTE_CREATED_DATE = "noteCreatedDate";
    }

    public static NoteEntry toNoteEntry(String id, Map<String, Object> doc) {
        String noteTitle = (String) doc.get(Fields.NOTE_TITLE);
        String noteText =  (String) doc.get(Fields.NOTE_TEXT);
        String noteCreatedDate =  (String) doc.get(Fields.NOTE_CREATED_DATE);
        NoteEntry noteEntry = new NoteEntry(noteTitle, noteText, noteCreatedDate);
        noteEntry.setFirebaseId(id);
        return noteEntry;
    }

    public static Map<String, Object> toFirebaseDocument(NoteEntry noteEntry) {
        Map<String, Object> firebaseDocument = new HashMap<>();
        firebaseDocument.put(Fields.NOTE_TITLE, noteEntry.getNoteTitle());
        firebaseDocument.put(Fields.NOTE_TEXT, noteEntry.getNoteText());
        firebaseDocument.put(Fields.NOTE_CREATED_DATE, noteEntry.getNoteCreatedDate());
        return firebaseDocument;
    }

}
