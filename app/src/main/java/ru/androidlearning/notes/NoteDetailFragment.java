package ru.androidlearning.notes;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import ru.androidlearning.notes.models.GetNotes;

public class NoteDetailFragment extends Fragment {

    private int currentIndexOfNote;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";

    public NoteDetailFragment() {
    }

    public static NoteDetailFragment newInstance(int indexOfNote) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_PARAM_KEY, indexOfNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentIndexOfNote = getArguments().getInt(BUNDLE_PARAM_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View noteEditTextFragment = inflater.inflate(R.layout.fragment_note_detail, container, false);
        TextView noteDate = noteEditTextFragment.findViewById(R.id.noteDate);
        TextView noteTitle = noteEditTextFragment.findViewById(R.id.noteTitle);
        TextView noteText = noteEditTextFragment.findViewById(R.id.noteText);

        if (currentIndexOfNote >= 0) {
            noteDate.setText(GetNotes.getNotes().getNoteFormattedCreatedDateAsStringByIndex(currentIndexOfNote));
            noteTitle.setText(GetNotes.getNotes().getNoteTitleByIndex(currentIndexOfNote));
            noteText.setText(GetNotes.getNotes().getNoteTextByIndex(currentIndexOfNote));
        }

        noteEditTextFragment.findViewById(R.id.saveAndCloseButton).setOnClickListener(v -> {
            saveNoteChanges();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        return noteEditTextFragment;
    }

    private void saveNoteChanges() {
        EditText noteTitle = Objects.requireNonNull(getActivity()).findViewById(R.id.noteTitle);
        EditText noteText = Objects.requireNonNull(getActivity()).findViewById(R.id.noteText);
        if (noteTitle != null && noteText != null) {
            if (currentIndexOfNote >= 0) {
                GetNotes.getNotes().updateNoteByIndex(currentIndexOfNote, noteTitle.getText().toString(), noteText.getText().toString());
            } else {
                //Создание новой заметки:
                GetNotes.getNotes().addNote(noteTitle.getText().toString(), noteText.getText().toString());
                currentIndexOfNote = GetNotes.getNotes().getAllNotesTitles().size() - 1;
            }
        }

        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        saveNoteChanges();
        outState.putInt(BUNDLE_PARAM_KEY, currentIndexOfNote);
        System.out.println("Saved: " + currentIndexOfNote);
        super.onSaveInstanceState(outState);
    }


}