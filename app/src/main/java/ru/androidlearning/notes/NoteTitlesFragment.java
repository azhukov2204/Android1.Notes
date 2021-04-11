package ru.androidlearning.notes;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.List;

import ru.androidlearning.notes.models.SingleObjectsGetter;
import ru.androidlearning.notes.types.EventUpdateNoteTitles;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = 0;
    private int newIndexOfNote = 0;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";
    private boolean needRecreateNoteTitlesList = false;
    private LinearLayout noteTitlesLinearLayout = null;

    public static final String TITLES_LIST_BACKSTACK_NAME = "TitlesFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleObjectsGetter.getBus().register(this);  //шина для взаимодействия компонентов приложения
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if (savedInstanceState == null)
        setHasOptionsMenu(true); //используем меню
        return inflater.inflate(R.layout.fragment_note_titles, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_titles_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //todo реализовать логику поиска
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
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
            openNoteDetailFragment(false);
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

    private void initViews(View view) {
        view.findViewById(R.id.createNewNoteButton).setOnClickListener(v -> openNoteDetailFragment(true));
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
                openNoteDetailFragment(false);
            });
            noteTitleIndex++;
        }
    }

    private void openNoteDetailFragment(boolean isNewNote) {
        NoteDetailFragment noteDetailFragment;
        if (isNewNote) {
            noteDetailFragment = NoteDetailFragment.newInstance(-1);
        } else {
            noteDetailFragment = NoteDetailFragment.newInstance(currentIndexOfNote);
        }

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requireActivity().findViewById(R.id.nothingSelectedTextView).setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.noteDetailFragmentContainer, noteDetailFragment);
        } else {
            fragmentTransaction.addToBackStack(NoteTitlesFragment.TITLES_LIST_BACKSTACK_NAME);
            fragmentTransaction.replace(R.id.notesUniversalFragmentContainer, noteDetailFragment);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void RecreateNoteTitlesList(EventUpdateNoteTitles e) {
        needRecreateNoteTitlesList = true;
        newIndexOfNote = e.getNewIndexOfNote();

        if (getActivity() != null) {
            if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                needRecreateNoteTitlesList = false;
                currentIndexOfNote = newIndexOfNote;
                initNoteTitlesList();
            }
        }
    }

}
