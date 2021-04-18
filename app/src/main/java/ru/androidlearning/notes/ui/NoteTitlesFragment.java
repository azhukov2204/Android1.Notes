package ru.androidlearning.notes.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.Objects;

import ru.androidlearning.notes.R;
import ru.androidlearning.notes.data.SingleObjectsGetter;
import ru.androidlearning.notes.data.ChangeNoteEvent;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = -1;
    private boolean needNotifyTitlesAdapter = false;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";
    private NoteTitlesAdapter noteTitlesAdapter;
    private RecyclerView noteTitlesListRV = null;
    private ChangeNoteEvent changeNoteEvent;
    private static final int MY_DEFAULT_DURATION = 600;

    public static final String TITLES_LIST_BACKSTACK_NAME = "TitlesFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleObjectsGetter.getBus().register(this);  //шина для взаимодействия компонентов приложения
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //используем меню
        View view = inflater.inflate(R.layout.fragment_note_titles, container, false);
        noteTitlesListRV = view.findViewById(R.id.noteTitlesLayout);
        initNoteTitlesListRV(noteTitlesListRV);
        initAddNewNoteButton(view);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        noteTitlesListRV.setItemAnimator(animator);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            currentIndexOfNote = savedInstanceState.getInt(BUNDLE_PARAM_KEY);
        } else {
            currentIndexOfNote = -1;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (currentIndexOfNote >= 0) {
                openNoteDetailFragment(false);
            }
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

        if (needNotifyTitlesAdapter) {
            needNotifyTitlesAdapter = false;
            notifyNoteTitlesAdapter(changeNoteEvent);
        }

        if (currentIndexOfNote >= 0) {
            //без этой задержки прокрутка может нормально не отработать при извлечении фрагмента из стека:
            new Handler(Looper.getMainLooper()).postDelayed(() -> noteTitlesListRV.smoothScrollToPosition(currentIndexOfNote), 100);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_titles_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Toast.makeText(getContext(), "Search will be implemented later: " + queryText, Toast.LENGTH_SHORT).show();
                return true;
            }

            // реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String queryText) {
                Toast.makeText(getContext(), "Search will be implemented later: " + queryText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                openNoteDetailFragment(true);
                return true;
            case R.id.action_sort_none:
                Toast.makeText(getContext(), "Sort none will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sort_ascending:
                Toast.makeText(getContext(), "Sort ascending will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sort_descending:
                Toast.makeText(getContext(), "Sort descending will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNoteTitlesListRV(RecyclerView noteTitlesListRV) {
        //noteTitlesListRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        noteTitlesListRV.setLayoutManager(linearLayoutManager);
        noteTitlesAdapter = new NoteTitlesAdapter(SingleObjectsGetter.getNotes(true), this);
        noteTitlesListRV.setAdapter(noteTitlesAdapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);

        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.note_cards_separator)));
        noteTitlesListRV.addItemDecoration(itemDecoration);

        noteTitlesAdapter.setOnItemClickListener((view, position) -> {
            currentIndexOfNote = position;
            openNoteDetailFragment(false);
        });

    }

    private void initAddNewNoteButton(View view) {
        view.findViewById(R.id.createNewNoteButton).setOnClickListener(v -> openNoteDetailFragment(true));
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
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentTransaction.replace(R.id.noteDetailFragmentContainer, noteDetailFragment);
        } else {
            fragmentTransaction.addToBackStack(NoteTitlesFragment.TITLES_LIST_BACKSTACK_NAME);
            fragmentTransaction.replace(R.id.notesUniversalFragmentContainer, noteDetailFragment);
        }
        fragmentTransaction.commit();
    }

    @Subscribe
    public void RecreateNoteTitlesList(ChangeNoteEvent e) {
        needNotifyTitlesAdapter = true;
        changeNoteEvent = e;

        if (getActivity() != null) {
            if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                notifyNoteTitlesAdapter(e);
                needNotifyTitlesAdapter = false;
            }
        }
    }

    private void notifyNoteTitlesAdapter(ChangeNoteEvent e) {
        switch (e.getChangeNoteType()) {
            case DELETE:
                noteTitlesAdapter.notifyItemRemoved(currentIndexOfNote);
                break;
            case INSERT:
                noteTitlesAdapter.notifyItemInserted(e.getNewIndexOfNote());
                break;
            case UPDATE:
                noteTitlesAdapter.notifyDataSetChanged();
                break;
        }
        if (e.getNewIndexOfNote() >= 0) {
            noteTitlesListRV.smoothScrollToPosition(e.getNewIndexOfNote());
        }
        currentIndexOfNote = e.getNewIndexOfNote();
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater =requireActivity().getMenuInflater();
        inflater.inflate(R.menu.note_titles_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = noteTitlesAdapter.getMenuPosition();
        Toast.makeText(getContext(), " Position " + position, Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }
}
