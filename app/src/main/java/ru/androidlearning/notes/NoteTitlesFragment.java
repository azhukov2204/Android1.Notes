package ru.androidlearning.notes;

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
import java.util.concurrent.locks.Condition;

import ru.androidlearning.notes.models.GetNotes;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = -1;

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
                openNoteTextFragment(view);
            });
            noteTitleIndex++;
        }
    }

    private void openNoteTextFragment(View view) {
        NoteEditTextFragment noteEditTextFragment = NoteEditTextFragment.newInstance(currentIndexOfNote);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Objects.requireNonNull(getActivity()).findViewById(R.id.nothingSelectedTextView).setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.noteTextFragmentContainer, noteEditTextFragment);
        } else {
            fragmentTransaction.replace(R.id.noteMainFragmentContainer, noteEditTextFragment).addToBackStack("fragment_note_titles");
        }


        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

}