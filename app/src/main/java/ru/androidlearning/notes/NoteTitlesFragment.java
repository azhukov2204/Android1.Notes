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

import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Objects;

import ru.androidlearning.notes.models.SingleObjectsGetter;
import ru.androidlearning.notes.types.EventUpdateNoteTitles;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = -1;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View noteTitlesFragment = inflater.inflate(R.layout.fragment_note_titles, container, false);

        noteTitlesFragment.findViewById(R.id.createNewNoteButton).setOnClickListener(v -> initNewNoteButton());

        return noteTitlesFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNoteTitlesList(view);
    }

    private void initNoteTitlesList(View view) {
        LinearLayout noteTitlesLinearLayout = view.findViewById(R.id.noteTitlesLayout);
        List<String> noteTitles = SingleObjectsGetter.getNotes(true).getAllNotesTitles();

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
                openNoteTextFragment(false);
            });
            noteTitleIndex++;
        }
    }

    private void initNewNoteButton() {
        currentIndexOfNote = -1; //-1 - c таким индекмом будет вызвана логика создания новой заметки
        openNoteTextFragment(true);
    }


    private void openNoteTextFragment(boolean isNewNote) {
        System.out.println("currentIndexOfNote: " + currentIndexOfNote);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openNoteTextFragmentInLandscape(isNewNote);
        } else {
            openNoteTextFragmentInPortland();
        }
    }

    private void openNoteTextFragmentInPortland() {
        Intent noteTextActivityIntent = new Intent(getActivity(), NoteDetailActivity.class);
        noteTextActivityIntent.putExtra(BUNDLE_PARAM_KEY, currentIndexOfNote);
        startActivity(noteTextActivityIntent);

    }

    private void openNoteTextFragmentInLandscape(boolean isNewNote) {
        if (currentIndexOfNote >= 0 || isNewNote) {
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
            openNoteTextFragmentInLandscape(false);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleObjectsGetter.getBus().register(this);
    }


    @Subscribe
    public void RecreateNoteTitlesList(EventUpdateNoteTitles e) {
        System.out.println("RecreateNoteTitlesList");
        //todo сделать обновление списка заголовков
    }

}