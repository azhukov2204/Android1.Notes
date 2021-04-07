package ru.androidlearning.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.androidlearning.notes.types.NoteEntryType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}