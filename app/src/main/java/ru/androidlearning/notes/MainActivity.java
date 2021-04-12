package ru.androidlearning.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private final static String IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY = "isHiddenNoteDetailContainer";
    private static boolean isHiddenNoteDetailContainer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY)) {
                Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - true");
                hideNoteDetailFragmentContainerInLandscape();
            } else {
                showNoteDetailFragmentContainerInLandscape();
                Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - false");
            }
        } else {
            Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - none");
        }

        openNoteTitlesFragmentAtFirstRun(savedInstanceState);
        removeUnnecessaryNoteDetailFragment(); //при смене ориентации на портретную надо удалить фрагмент из noteDetailFragmentContainer, иначе в ToolBar останется его меню
        initView();
    }

    private void openNoteTitlesFragmentAtFirstRun(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            openNotesFragment();
        }
        fragmentManager.popBackStack(NoteTitlesFragment.TITLES_LIST_BACKSTACK_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void removeUnnecessaryNoteDetailFragment() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment noteDetailFragment = fragmentManager.findFragmentById(R.id.noteDetailFragmentContainer);

            if (noteDetailFragment != null) {
                fragmentManager.beginTransaction().remove(noteDetailFragment).commit();
            }
        }
    }

    private void initView() {
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private boolean navigateFragment(int id) {
        switch (id) {
            case R.id.action_notes:
                openNotesFragment();
                return true;
            case R.id.action_settings:
                openSettingsFragment();
                return true;
            case R.id.action_about:
                return true;
        }
        return false;
    }

    private void openSettingsFragment() {
        hideNoteDetailFragmentContainerInLandscape();
        Fragment settingsFragment = new SettingsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.notesUniversalFragmentContainer, settingsFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void openNotesFragment() {
        showNoteDetailFragmentContainerInLandscape();
        NoteTitlesFragment noteTitlesFragment = new NoteTitlesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.notesUniversalFragmentContainer, noteTitlesFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void hideNoteDetailFragmentContainerInLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            FrameLayout notesUniversalFragmentContainer = findViewById(R.id.notesUniversalFragmentContainer);
            notesUniversalFragmentContainer.setLayoutParams(params);

            isHiddenNoteDetailContainer = true;
            Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - set to true");
        }
    }

    private void showNoteDetailFragmentContainerInLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            FrameLayout notesUniversalFragmentContainer = findViewById(R.id.notesUniversalFragmentContainer);
            notesUniversalFragmentContainer.setLayoutParams(params);

            isHiddenNoteDetailContainer = false;
            Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - set to false");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY, isHiddenNoteDetailContainer);
    }
}


