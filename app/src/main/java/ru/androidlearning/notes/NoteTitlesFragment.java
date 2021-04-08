package ru.androidlearning.notes;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ru.androidlearning.notes.models.GetNotes;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = -1;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_titles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNoteTitlesList(view);
    }

    private void initNoteTitlesList(View view) {
        LinearLayout noteTitlesLinearLayout = view.findViewById(R.id.noteTitlesLayout);
        List<String> noteTitles = GetNotes.getNotes(true).getAllNotesTitles();

        int noteTitleIndex = 0;
        for (String noteTitle : noteTitles) {
            //TextView для отображения заголовков заметок будем брать из "шаблона"
            LayoutInflater inflater = getLayoutInflater();
            View noteTitleItemLayout = inflater.inflate(R.layout.note_title_item, noteTitlesLinearLayout, false);
            TextView noteTitleView = noteTitleItemLayout.findViewById(R.id.noteTitleTextView);
            noteTitleView.setText(noteTitle);
            noteTitlesLinearLayout.addView(noteTitleItemLayout);
            final int finalNoteTitleIndex = noteTitleIndex;

            noteTitleItemLayout.setOnClickListener(v -> {
                currentIndexOfNote = finalNoteTitleIndex;
                openNoteTextFragment();
            });
            noteTitleIndex++;
        }
    }

    private void openNoteTextFragment() {
        System.out.println("currentIndexOfNote: " + currentIndexOfNote);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openNoteTextFragmentInLandscape();
        } else {
            openNoteTextFragmentInPortland();
        }
    }

    private void openNoteTextFragmentInPortland() {
        Intent noteTextActivityIntent = new Intent(getActivity(), NoteDetailActivity.class);
        noteTextActivityIntent.putExtra(BUNDLE_PARAM_KEY, currentIndexOfNote);
        startActivity(noteTextActivityIntent);

    }

    private void openNoteTextFragmentInLandscape() {
        if (currentIndexOfNote >= 0) {
            NoteDetailFragment noteDetailFragment = NoteDetailFragment.newInstance(currentIndexOfNote);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Objects.requireNonNull(getActivity()).findViewById(R.id.nothingSelectedTextView).setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.noteTextFragmentContainerMain, noteDetailFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_PARAM_KEY, currentIndexOfNote);
        System.out.println("Saved: " + currentIndexOfNote);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentIndexOfNote = savedInstanceState.getInt(BUNDLE_PARAM_KEY);
            System.out.println("read currentIndexOfNote: " + currentIndexOfNote);

        } else {
            currentIndexOfNote = -1;
            System.out.println("currentIndexOfNote is null: " + currentIndexOfNote);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openNoteTextFragmentInLandscape();
        }
    }
}