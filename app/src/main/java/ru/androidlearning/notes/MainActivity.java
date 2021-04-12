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

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNoteTitlesFragment(savedInstanceState);
        removeUnnecessaryNoteDetailFragment(); //при смене ориентации на портретную надо удалить фрагмент из noteDetailFragmentContainer, иначе в ToolBar останется его меню
        initView();
    }

    private void createNoteTitlesFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            NoteTitlesFragment noteTitlesFragment = new NoteTitlesFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.notesUniversalFragmentContainer, noteTitlesFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
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
            if (navigateFragment(id)){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private boolean navigateFragment(int id) {

        return true;
    }

}
