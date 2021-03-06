package ru.androidlearning.notes.ui;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.Objects;

import ru.androidlearning.notes.MainActivity;
import ru.androidlearning.notes.R;
import ru.androidlearning.notes.bus_events.ChangeNoteTypes;
import ru.androidlearning.notes.bus_events.DeleteNoteInLandscapeEvent;
import ru.androidlearning.notes.common.ConfirmDeletingAlertDialog;
import ru.androidlearning.notes.common.SingleObjectsGetter;
import ru.androidlearning.notes.bus_events.ChangeNoteEvent;


public class NoteTitlesFragment extends Fragment {

    private int currentIndexOfNote = -1;
    private boolean needNotifyTitlesAdapter = false;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";
    private NoteTitlesAdapter noteTitlesAdapter;
    private RecyclerView noteTitlesListRV = null;
    private ChangeNoteEvent changeNoteEvent;
    private static final int MY_DEFAULT_DURATION = 600;
    private MainActivity mainActivity;
    private static final String LOG_TAG = "[NoteTitlesFragment]";

    private FloatingActionButton addNewNoteFAB;

    public static final String TITLES_LIST_BACKSTACK_NAME = "TitlesFragment";

    public NoteTitlesFragment() {
        Bundle args = new Bundle();
        args.putInt(BUNDLE_PARAM_KEY, currentIndexOfNote);
        setArguments(args);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleObjectsGetter.getBus().register(this);  //???????? ?????? ???????????????????????????? ?????????????????????? ????????????????????
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SingleObjectsGetter.getBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //???????????????????? ????????
        View view = inflater.inflate(R.layout.fragment_note_titles, container, false);
        noteTitlesListRV = view.findViewById(R.id.noteTitlesLayout);
        initNoteTitlesListRV(noteTitlesListRV);
        initAddNewNoteButtons();

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        noteTitlesListRV.setItemAnimator(animator);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            currentIndexOfNote = getArguments().getInt(BUNDLE_PARAM_KEY);
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
        saveCurrentInstanceState();
        super.onSaveInstanceState(outState);
    }

    private void saveCurrentInstanceState() {
        if (getArguments() != null) {
            getArguments().putInt(BUNDLE_PARAM_KEY, currentIndexOfNote);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addNewNoteFAB.show();

        changeHomeButtonToBurgerButtonOnToolbar();

        if (needNotifyTitlesAdapter) {
            needNotifyTitlesAdapter = false;
            notifyNoteTitlesAdapter(changeNoteEvent);
        }

        if (currentIndexOfNote >= 0) {
            noteTitlesListRV.smoothScrollToPosition(currentIndexOfNote);
        }
    }

    private void changeHomeButtonToBurgerButtonOnToolbar() {
        if (mainActivity != null) {
            mainActivity.showBurgerButtonOnToolbar();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_titles_menu, menu);
        /*MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ?????????????????? ???? ?????????? ?????????? ????????????
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Toast.makeText(getContext(), "Search will be implemented later: " + queryText, Toast.LENGTH_SHORT).show();
                return true;
            }

            // ?????????????????? ???? ?????????????? ???????????? ??????????????
            @Override
            public boolean onQueryTextChange(String queryText) {
                Toast.makeText(getContext(), "Search will be implemented later: " + queryText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            openNoteDetailFragment(true);
            return true;
/*            case R.id.action_sort_none:
                Toast.makeText(getContext(), "Sort none will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sort_ascending:
                Toast.makeText(getContext(), "Sort ascending will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sort_descending:
                Toast.makeText(getContext(), "Sort descending will be implemented later...", Toast.LENGTH_SHORT).show();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNoteTitlesListRV(RecyclerView noteTitlesListRV) {
        //noteTitlesListRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        noteTitlesListRV.setLayoutManager(linearLayoutManager);
        noteTitlesAdapter = new NoteTitlesAdapter(this);
        noteTitlesListRV.setAdapter(noteTitlesAdapter);
        SingleObjectsGetter.getNotes().initNotes(notes -> noteTitlesAdapter.notifyDataSetChanged()); //?????? ?????????????????????????? ???????????????????? ???????????????????? ???????????? ???? Firestore
        noteTitlesAdapter.setNotes(SingleObjectsGetter.getNotes());

        DividerItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);

        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.note_cards_separator)));
        noteTitlesListRV.addItemDecoration(itemDecoration);

        noteTitlesAdapter.setOnItemClickListener((view, position) -> {
            currentIndexOfNote = position;
            openNoteDetailFragment(false);
        });

    }

    private void initAddNewNoteButtons() {
        addNewNoteFAB = requireActivity().findViewById(R.id.add_new_note_fab);
        addNewNoteFAB.setOnClickListener(v -> openNoteDetailFragment(true));
    }

    private void openNoteDetailFragment(boolean isNewNote) {
        NoteDetailFragment noteDetailFragment;
        if (isNewNote) {
            noteDetailFragment = NoteDetailFragment.newInstance(-1);
        } else {
            noteDetailFragment = NoteDetailFragment.newInstance(currentIndexOfNote);
        }

        saveCurrentInstanceState();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentTransaction.replace(R.id.noteDetailFragmentContainer, noteDetailFragment);
        } else {
            addNewNoteFAB.hide();
            fragmentTransaction.addToBackStack(NoteTitlesFragment.TITLES_LIST_BACKSTACK_NAME);
            fragmentTransaction.replace(R.id.notesUniversalFragmentContainer, noteDetailFragment);
            changeBurgerButtonToHomeButtonOnToolbar();
        }
        fragmentTransaction.commit();
    }

    private void changeBurgerButtonToHomeButtonOnToolbar() {
        if (mainActivity != null) {
            mainActivity.showHomeButtonOnToolbar();
        }
    }

    @Subscribe
    public void refreshNoteTitlesListViaBus(ChangeNoteEvent e) {
        needNotifyTitlesAdapter = true;
        changeNoteEvent = e;

        if (getActivity() != null) {
            if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                notifyNoteTitlesAdapter(e);
                needNotifyTitlesAdapter = false;
            }
        }
        currentIndexOfNote = e.getNewIndexOfNote();
        saveCurrentInstanceState();
    }

    private void notifyNoteTitlesAdapter(ChangeNoteEvent e) {
        switch (e.getChangeNoteType()) {
            case DELETE:
                noteTitlesAdapter.notifyItemRemoved(currentIndexOfNote);
                currentIndexOfNote = -1;
                break;
            case INSERT:
                noteTitlesAdapter.notifyItemInserted(e.getNewIndexOfNote());
                break;
            case UPDATE:
                noteTitlesAdapter.notifyDataSetChanged();
                break;
        }
        if (e.getNewIndexOfNote() >= 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> noteTitlesListRV.smoothScrollToPosition(currentIndexOfNote), 100);
        }
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.note_titles_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        currentIndexOfNote = noteTitlesAdapter.getMenuPosition();
        if (item.getItemId() == R.id.action_delete_context) {
            ConfirmDeletingAlertDialog.runDialog(requireContext(), () -> { //???????????????????? ?????????? ?????? ?????????????????????????? ????????????????. ?? ???????????? ?????????????????????????? ???????????????????? ?????? ???????????????????? ????????????
                if (isNoteDetailFragmentOpened()) {
                    SingleObjectsGetter.getBus().post(new DeleteNoteInLandscapeEvent());
                } else {
                    SingleObjectsGetter.getNotes().deleteNoteByIndex(currentIndexOfNote);
                    notifyNoteTitlesAdapter(new ChangeNoteEvent(-1, ChangeNoteTypes.DELETE));
                }
            });

        }

        return super.onContextItemSelected(item);
    }

    private boolean isNoteDetailFragmentOpened() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment noteDetailFragment = fragmentManager.findFragmentById(R.id.noteDetailFragmentContainer);
        return (noteDetailFragment != null);
    }

}
