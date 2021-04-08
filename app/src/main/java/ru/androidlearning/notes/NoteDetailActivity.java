package ru.androidlearning.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;

public class NoteDetailActivity extends AppCompatActivity {

    private static final String BUNDLE_PARAM_KEY = "NoteIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_text);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else {
            int currentIndexOfNote = getIntent().getIntExtra(BUNDLE_PARAM_KEY, -1);
            if (savedInstanceState == null) {
                NoteDetailFragment noteDetailFragment = NoteDetailFragment.newInstance(currentIndexOfNote);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.noteTextPortlandContainer, noteDetailFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        }

    }

}