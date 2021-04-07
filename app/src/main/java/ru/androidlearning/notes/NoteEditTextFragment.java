package ru.androidlearning.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import ru.androidlearning.notes.models.GetNotes;
import ru.androidlearning.notes.models.Notes;

public class NoteEditTextFragment extends Fragment {

    private static int currentIndexOfNote = -1;
    public static final String BUBDLE_PARAM_KEY = "NoteIndex";

    public NoteEditTextFragment() {
    }

    public static NoteEditTextFragment newInstance(int indexOfNote) {
        NoteEditTextFragment fragment = new NoteEditTextFragment();
        Bundle args = new Bundle();
        args.putInt(BUBDLE_PARAM_KEY, indexOfNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentIndexOfNote = getArguments().getInt(BUBDLE_PARAM_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View noteEditTextFragment = inflater.inflate(R.layout.fragment_note_edit_text, container, false);
        TextView noteDate = noteEditTextFragment.findViewById(R.id.noteDate);
        TextView noteTitle = noteEditTextFragment.findViewById(R.id.noteTitle);
        TextView noteText = noteEditTextFragment.findViewById(R.id.noteText);

        noteDate.setText(GetNotes.getNotes().getNoteFormattedCreatedDateAsStringByIndex(currentIndexOfNote));
        noteTitle.setText(GetNotes.getNotes().getNoteTitleByIndex(currentIndexOfNote));
        noteText.setText(GetNotes.getNotes().getNoteTextByIndex(currentIndexOfNote));

        noteEditTextFragment.findViewById(R.id.saveAndCloseButton).setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        return noteEditTextFragment;
    }


}