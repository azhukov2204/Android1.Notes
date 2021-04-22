package ru.androidlearning.notes.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NotesFirebaseImpl implements Notes {

    private static final String NOTES_FIREBASE_COLLECTION = "notes";
    private static final String LOG_TAG = "[NotesFirebaseImpl]";
    private List<NoteEntry> notesListLocal;
    private FirebaseFirestore firestoreDB;
    private CollectionReference notesListFirestore;


    public NotesFirebaseImpl() {
        notesListLocal = new ArrayList<>();
        firestoreDB = FirebaseFirestore.getInstance();
        notesListFirestore = firestoreDB.collection(NOTES_FIREBASE_COLLECTION);
    }

    @Override
    public Notes initNotes(boolean isTest) {
        if (isTest) {
            notesListLocal.addAll(Arrays.asList(
                    new NoteEntry("title1", "massage1"),
                    new NoteEntry("title2", "message2"),
                    new NoteEntry("title3", "message3"),
                    new NoteEntry("title4", "message4"),
                    new NoteEntry("title5", "message5"),
                    new NoteEntry("title6", "message6"),
                    new NoteEntry("title7", "message7")));
        }
        return this;
    }

    @Override
    public Notes initNotes(NotesResponse notesResponse) {
        notesListFirestore.orderBy(NotesDataMapping.Fields.NOTE_CREATED_DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesListLocal = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                            Map<String, Object> doc = queryDocumentSnapshot.getData();
                            String id = queryDocumentSnapshot.getId();
                            NoteEntry noteEntry = NotesDataMapping.toNoteEntry(id, doc);
                            notesListLocal.add(noteEntry);
                        }
                        Log.d(LOG_TAG, "Success. Read " + notesListLocal.size() + " notes");
                        notesResponse.afterInitialization(NotesFirebaseImpl.this);
                    } else {
                        Log.d(LOG_TAG, "Failed with: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.d(LOG_TAG, "Failed with: ", e));
        return this;
    }

    public void addNote(String title, String noteText, String noteDate) {
        NoteEntry noteEntry = new NoteEntry(title, noteText, noteDate);
        notesListFirestore.add(NotesDataMapping.toFirebaseDocument(noteEntry))
                .addOnSuccessListener(documentReference -> noteEntry.setFirebaseId(documentReference.getId()));
        //notesListLocal.add(noteEntry);
    }

    public void deleteNoteByIndex(int index) {
        notesListFirestore.document(notesListLocal.get(index).getFirebaseId()).delete();
        notesListLocal.remove(index);
    }

    public void updateNoteByIndex(int index, String noteTitle, String noteText, String noteDate) {
        String firebaseId = notesListLocal.get(index).getFirebaseId();
        /*notesListLocal.get(index).setNoteTitle(noteTitle);
        notesListLocal.get(index).setNoteText(noteText);
        notesListLocal.get(index).setNoteCreatedDate(noteDate);*/
        notesListFirestore.document(firebaseId).set(new NoteEntry(noteTitle, noteText, noteDate));
    }

    public String getNoteTitleByIndex(int index) {
        return notesListLocal.get(index).getNoteTitle();
    }

    public String getNoteTextByIndex(int index) {
        return notesListLocal.get(index).getNoteText();
    }

    public String getNoteFormattedCreatedDateAsStringByIndex(int index) {
        return notesListLocal.get(index).getNoteCreatedDate();
    }

    public NoteEntry getNoteEntryByIndex(int index) {
        return notesListLocal.get(index);
    }

    public List<String> getAllNotesTitles() {
        List<String> allNoteTitles = new ArrayList<>();
        for (NoteEntry note : notesListLocal) {
            allNoteTitles.add(note.getNoteTitle());
        }
        return allNoteTitles;
    }

}
