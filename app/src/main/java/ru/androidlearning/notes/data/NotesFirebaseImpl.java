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
import java.util.concurrent.atomic.AtomicReference;

public class NotesFirebaseImpl implements Notes {

    private static final String NOTES_FIREBASE_COLLECTION = "notes";
    private static final String LOG_TAG = "[NotesFirebaseImpl]";
    private List<NoteEntry> notesListLocal;
    private CollectionReference notesListFirestore;


    public NotesFirebaseImpl() {
        notesListLocal = new ArrayList<>();
    }

    @Override
    public void setFirebaseCollectionName(String firebaseCollectionName) {
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        notesListFirestore = firestoreDB.collection(NOTES_FIREBASE_COLLECTION + ":" + firebaseCollectionName);
    }

    @Override
    public Notes initNotes(boolean isTest) {
        return this;
    }

    @Override
    public Notes initNotes(NotesResponse notesResponse) {
        notesListFirestore.orderBy(NotesDataMapping.Fields.NOTE_CREATED_DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesListLocal = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                                Map<String, Object> doc = queryDocumentSnapshot.getData();
                                String id = queryDocumentSnapshot.getId();
                                NoteEntry noteEntry = NotesDataMapping.toNoteEntry(id, doc);
                                notesListLocal.add(noteEntry);
                            }
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
        final NoteEntry noteEntry = new NoteEntry(title, noteText, noteDate);
        notesListFirestore.add(NotesDataMapping.toFirebaseDocument(noteEntry)).addOnSuccessListener(documentReference -> noteEntry.setFirebaseId(documentReference.getId()));
        notesListLocal.add(noteEntry);
    }

    public void deleteNoteByIndex(int index) {
        notesListFirestore.document(notesListLocal.get(index).getFirebaseId()).delete();
        notesListLocal.remove(index);
    }

    public void updateNoteByIndex(int index, String noteTitle, String noteText, String noteDate) {
        String firebaseId = notesListLocal.get(index).getFirebaseId();
        notesListLocal.get(index).setNoteTitle(noteTitle);
        notesListLocal.get(index).setNoteText(noteText);
        notesListLocal.get(index).setNoteCreatedDate(noteDate);
        if (firebaseId != null && !firebaseId.isEmpty()) {
            notesListFirestore.document(firebaseId).set(NotesDataMapping.toFirebaseDocument(new NoteEntry(noteTitle, noteText, noteDate)));
        }
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
