package ru.androidlearning.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.sdk.api.account.AccountService;
import com.vk.sdk.api.account.dto.AccountUserSettings;
import com.vk.sdk.api.users.UsersService;
import com.vk.sdk.api.users.dto.UsersFields;
import com.vk.sdk.api.users.dto.UsersUserXtrCounters;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ru.androidlearning.notes.common.SingleObjectsGetter;
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
    private TextView userIdView;
    private ImageView userAvatarImageView;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //данная логика отрабатывает при авторизации через VK:
        VKAuthCallback callback = new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                startNotesWithAuthDataVK();
            }

            @Override
            public void onLoginFailed(int i) {
                Log.w(LOG_TAG, "VKSignInResult:failed code= " + i);
            }
        };
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void startNotesWithAuthDataVK() {
        VK.execute(new AccountService().accountGetProfileInfo(), new VKApiCallback<AccountUserSettings>() {
            @Override
            public void success(AccountUserSettings accountUserSettings) {
                final String userID = "VK" + accountUserSettings.getId();
                final String userName = accountUserSettings.getFirstName() + " " + accountUserSettings.getLastName();
                //userData.setUserID("VK" + accountUserSettings.getId());
                //userData.setUserName(accountUserSettings.getFirstName() + " " + accountUserSettings.getLastName());
                //isAuthenticationDone = true;
                List<UsersFields> usersFields = Collections.singletonList(UsersFields.PHOTO_100);
                VK.execute(new UsersService().usersGet(null, usersFields, null), new VKApiCallback<List<UsersUserXtrCounters>>() {
                    @Override
                    public void success(List<UsersUserXtrCounters> usersUserXtrCounters) {
                        for (UsersUserXtrCounters usersUserXtrCounter : usersUserXtrCounters) {
                            Uri userAvatarUri = Uri.parse(usersUserXtrCounter.getPhoto100());
                            setUserData(true, userID, userName, userAvatarUri);
                            //userData.setUserAvatarUri(Uri.parse(usersUserXtrCounter.getPhoto100()));
                            //setUserDataToNavHeader();
                            openNotesFragment();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {
                        Log.w(LOG_TAG, "VKSignInResult:error while reading email = " + e.getMessage());
                    }
                });
            }

            @Override
            public void fail(@NotNull Exception e) {
                Log.e(LOG_TAG, "VKSignInResult:failed code= " + e.getMessage());
            }
        });
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
        userIdView = navigationView.getHeaderView(0).findViewById(R.id.userIdTextView);
        userAvatarImageView = navigationView.getHeaderView(0).findViewById(R.id.userAvatarImageView);
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
        hideFABAndNoteDetailFragmentContainer();
        Fragment settingsFragment = new SettingsFragment();
        runFragment(settingsFragment);
    }

    private void openAboutFragment() {
        hideFABAndNoteDetailFragmentContainer();
        Fragment aboutFragment = new AboutFragment();
        runFragment(aboutFragment);
    }

    public void openNotesFragment() {
        addNewNoteFAB.show();
        showNoteDetailFragmentContainerInLandscape();
        NoteTitlesFragment noteTitlesFragment = new NoteTitlesFragment();
        runFragment(noteTitlesFragment);
    }

    private void openAuthFragment(boolean isSignOutRequired) {
        hideButtonsOnToolbar();
        hideFABAndNoteDetailFragmentContainer();
        if (isSignOutRequired) {
            setUserData(false, "", "", null);
        }
        Fragment authFragment = AuthFragment.newInstance(isSignOutRequired);
        runFragment(authFragment);
    }

    private void hideFABAndNoteDetailFragmentContainer() {
        addNewNoteFAB.hide();
        clearBackStack();
        hideNoteDetailFragmentContainerInLandscape();
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

    public void setUserData(boolean isAuthenticationDone, String userId, String userName, Uri userAvatarUri) {
        this.isAuthenticationDone = isAuthenticationDone;
        userData.setUserID(userId);
        userData.setUserName(userName);
        userData.setUserAvatarUri(userAvatarUri);
        setUserDataToNavHeader();
    }

    private void setUserDataToNavHeader() {
        userLoginTextView.setText(userData.getUserName());
        userIdView.setText(userData.getUserID());
        userAvatarImageView.setImageURI(null);
        if (userData.getUserAvatarUri() != null) {
            Picasso.get().load(userData.getUserAvatarUri()).into(userAvatarImageView);
        } else {
            userAvatarImageView.setImageResource(R.mipmap.ic_launcher_round);
        }
        SingleObjectsGetter.getNotes().setFirebaseCollectionName(userData.getUserID()); //дял каждого пользователя заметки будут храниться в своей коллекции в Firebase
    }
}


