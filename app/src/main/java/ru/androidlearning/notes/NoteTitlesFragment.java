package ru.androidlearning.notes;

import android.content.Context;
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

import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Objects;

import ru.androidlearning.notes.models.SingleObjectsGetter;
import ru.androidlearning.notes.types.EventUpdateNoteTitles;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = 0;
    private int newIndexOfNote = 0;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";
    private boolean needRecreateNoteTitlesList = false;
    private LinearLayout noteTitlesLinearLayout = null;

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (needRecreateNoteTitlesList) {
            needRecreateNoteTitlesList = false;
            currentIndexOfNote = newIndexOfNote;
            initNoteTitlesList();
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleObjectsGetter.getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_titles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragmentElements(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentIndexOfNote = savedInstanceState.getInt(BUNDLE_PARAM_KEY);
        } else {
            currentIndexOfNote = 0;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openNoteTextFragmentInLandscape(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_PARAM_KEY, currentIndexOfNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRecreateNoteTitlesList) {
            needRecreateNoteTitlesList = false;
            currentIndexOfNote = newIndexOfNote;
            initNoteTitlesList();
        }
    }

    private void initFragmentElements(View view) {
        view.findViewById(R.id.createNewNoteButton).setOnClickListener(v -> newNoteButtonAction());
        noteTitlesLinearLayout = view.findViewById(R.id.noteTitlesLayout);
        initNoteTitlesList();
    }


    private void initNoteTitlesList() {
        List<String> noteTitles = SingleObjectsGetter.getNotes(true).getAllNotesTitles();
        LayoutInflater inflater = getLayoutInflater();
        noteTitlesLinearLayout.removeAllViews();
        int noteTitleIndex = 0;
        for (String noteTitle : noteTitles) {
            //TextView для отображения заголовков заметок будем брать из "шаблона"
            View noteTitleItemLayout = inflater.inflate(R.layout.note_title_item, noteTitlesLinearLayout, false);
            TextView noteTitleView = noteTitleItemLayout.findViewById(R.id.noteTitleTextView);
            noteTitleView.setText(noteTitle);
            noteTitlesLinearLayout.addView(noteTitleItemLayout);
            final int finalNoteTitleIndex = noteTitleIndex;

            noteTitleItemLayout.setOnClickListener(v -> {
                currentIndexOfNote = finalNoteTitleIndex;
                openNoteTextFragment(false);
            });
            noteTitleIndex++;
        }
    }

    private void newNoteButtonAction() {
        openNoteTextFragment(true);
    }

    private void openNoteTextFragment(boolean isNewNote) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openNoteTextFragmentInLandscape(isNewNote);
        } else {
            openNoteTextFragmentInPortland(isNewNote);
        }
    }

    private void openNoteTextFragmentInPortland(boolean isNewNote) {
        Intent noteTextActivityIntent = new Intent(getActivity(), NoteDetailActivity.class);
        if (isNewNote) {
            noteTextActivityIntent.putExtra(BUNDLE_PARAM_KEY, -1);
        } else {
            noteTextActivityIntent.putExtra(BUNDLE_PARAM_KEY, currentIndexOfNote);
        }
        startActivity(noteTextActivityIntent);
    }

    private void openNoteTextFragmentInLandscape(boolean isNewNote) {
        NoteDetailFragment noteDetailFragment;
        if (isNewNote) {
            noteDetailFragment = NoteDetailFragment.newInstance(-1);
        } else {
            noteDetailFragment = NoteDetailFragment.newInstance(currentIndexOfNote);
        }
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Objects.requireNonNull(getActivity()).findViewById(R.id.nothingSelectedTextView).setVisibility(View.GONE);
        fragmentTransaction.replace(R.id.noteTextFragmentContainerMain, noteDetailFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void RecreateNoteTitlesList(EventUpdateNoteTitles e) {
        needRecreateNoteTitlesList = true;
        newIndexOfNote = e.getNewIndexOfNote();

        if (getActivity() != null) {
            if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                needRecreateNoteTitlesList = false;
                currentIndexOfNote = newIndexOfNote;
                initNoteTitlesList();
            }
        }
    }

}
