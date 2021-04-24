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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import ru.androidlearning.notes.common.UserData;
import ru.androidlearning.notes.ui.AboutFragment;
import ru.androidlearning.notes.ui.AuthFragment;
import ru.androidlearning.notes.ui.NoteTitlesFragment;
import ru.androidlearning.notes.ui.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private final static String IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY = "isHiddenNoteDetailContainer";
    private final static String IS_AUTHENTICATION_DONE_BUNDLE_KEY = "isAuthenticationDone";
    private final static String USER_DATA_BUNDLE_KEY = "userData";
    private boolean isHiddenNoteDetailContainer = false;
    private boolean isAuthenticationDone = false;
    private static final String LOG_TAG = "[MainActivity]";
    FloatingActionButton addNewNoteFAB;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private UserData userData = new UserData();
    private TextView userLoginTextView;
    private TextView userMailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (savedInstanceState != null) {
            isHiddenNoteDetailContainer = savedInstanceState.getBoolean(IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY);
            isAuthenticationDone = savedInstanceState.getBoolean(IS_AUTHENTICATION_DONE_BUNDLE_KEY);
        }

        checkInstanceStateAndHideOrShowNoteDetailFragmentContainer();

        if (isAuthenticationDone) {
            openNoteTitlesFragmentAtFirstRun(savedInstanceState);
        } else {
            openAuthFragment(false);
        }

        removeUnnecessaryNoteDetailFragment(); //при смене ориентации на портретную надо удалить фрагмент из noteDetailFragmentContainer, иначе в ToolBar останется его меню
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY, isHiddenNoteDetailContainer);
        outState.putBoolean(IS_AUTHENTICATION_DONE_BUNDLE_KEY, isAuthenticationDone);
        outState.putParcelable(USER_DATA_BUNDLE_KEY, userData);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        userData = savedInstanceState.getParcelable(USER_DATA_BUNDLE_KEY);
        setUserDataToNavHeader();
    }

    private void checkInstanceStateAndHideOrShowNoteDetailFragmentContainer() {
        if (isHiddenNoteDetailContainer) {
            hideNoteDetailFragmentContainerInLandscape();
        } else {
            showNoteDetailFragmentContainerInLandscape();
        }
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

    private void initViews() {
        addNewNoteFAB = findViewById(R.id.add_new_note_fab);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        userLoginTextView = navigationView.getHeaderView(0).findViewById(R.id.userLoginTextView);
        userMailView = navigationView.getHeaderView(0).findViewById(R.id.userMailTextView);
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
                openAboutFragment();
                return true;
            case R.id.action_logout:
                openAuthFragment(true);
                return true;
        }
        return false;
    }

    public void showHomeButtonOnToolbar() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> onBackPressed());
    }

    public void showBurgerButtonOnToolbar() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.setToolbarNavigationClickListener(null);
    }

    public void hideButtonsOnToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setToolbarNavigationClickListener(null);
    }

    private void openSettingsFragment() {
        addNewNoteFAB.hide();
        clearBackStack();
        hideNoteDetailFragmentContainerInLandscape();
        Fragment settingsFragment = new SettingsFragment();
        runFragment(settingsFragment);
    }

    private void openAboutFragment() {
        addNewNoteFAB.hide();
        clearBackStack();
        hideNoteDetailFragmentContainerInLandscape();
        Fragment aboutFragment = new AboutFragment();
        runFragment(aboutFragment);
    }

    public void openNotesFragment() {
        Log.d(LOG_TAG, userData.getUserName() + " " + userData.getUserEmail());
        addNewNoteFAB.show();
        showNoteDetailFragmentContainerInLandscape();
        NoteTitlesFragment noteTitlesFragment = new NoteTitlesFragment();
        runFragment(noteTitlesFragment);
    }

    private void openAuthFragment(boolean isSignOutRequired) {
        hideButtonsOnToolbar();
        addNewNoteFAB.hide();
        clearBackStack();
        hideNoteDetailFragmentContainerInLandscape();
        if (isSignOutRequired) {
            setUserData(false, null, null);
        }
        Fragment authFragment = AuthFragment.newInstance(isSignOutRequired);
        runFragment(authFragment);
    }

    private void runFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.notesUniversalFragmentContainer, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void hideNoteDetailFragmentContainerInLandscape() {
        isHiddenNoteDetailContainer = true;
        Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - set to true");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            FrameLayout notesUniversalFragmentContainer = findViewById(R.id.notesUniversalFragmentContainer);
            notesUniversalFragmentContainer.setLayoutParams(params);
        }
    }

    private void showNoteDetailFragmentContainerInLandscape() {
        isHiddenNoteDetailContainer = false;
        Log.d("Is Hidden", "IS_HIDDEN_NOTE_DETAIL_CONTAINER_BUNDLE_KEY - set to false");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            FrameLayout notesUniversalFragmentContainer = findViewById(R.id.notesUniversalFragmentContainer);
            notesUniversalFragmentContainer.setLayoutParams(params);
        }
    }

    private void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void setUserData(boolean isAuthenticationDone, String userName, String userEmail) {
        this.isAuthenticationDone = isAuthenticationDone;
        userData.setUserName(userName);
        userData.setUserEmail(userEmail);
        setUserDataToNavHeader();
    }

    private void setUserDataToNavHeader() {
        userLoginTextView.setText(userData.getUserName());
        userMailView.setText(userData.getUserEmail());
    }
}


