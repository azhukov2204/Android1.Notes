package ru.androidlearning.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import ru.androidlearning.notes.models.SingleObjectsGetter;
import ru.androidlearning.notes.types.EventTypeRecreateNoteTitles;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}