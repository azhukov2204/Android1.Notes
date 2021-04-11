package ru.androidlearning.notes;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import ru.androidlearning.notes.models.SingleObjectsGetter;
import ru.androidlearning.notes.types.EventUpdateNoteTitles;

public class NoteDetailFragment extends Fragment {

    private int currentIndexOfNote;
    private static final String BUNDLE_PARAM_KEY = "NoteIndex";

    public static NoteDetailFragment newInstance(int indexOfNote) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_PARAM_KEY, indexOfNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentIndexOfNote = getArguments().getInt(BUNDLE_PARAM_KEY);
        }
        SingleObjectsGetter.getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View noteEditTextFragment = inflater.inflate(R.layout.fragment_note_detail, container, false);
        TextView noteDate = noteEditTextFragment.findViewById(R.id.noteDate);
        TextView noteTitle = noteEditTextFragment.findViewById(R.id.noteTitle);
        TextView noteText = noteEditTextFragment.findViewById(R.id.noteText);

        if (currentIndexOfNote >= 0) {
            //заполняем поля существующей заметки:
            noteDate.setText(SingleObjectsGetter.getNotes().getNoteFormattedCreatedDateAsStringByIndex(currentIndexOfNote));
            noteTitle.setText(SingleObjectsGetter.getNotes().getNoteTitleByIndex(currentIndexOfNote));
            noteText.setText(SingleObjectsGetter.getNotes().getNoteTextByIndex(currentIndexOfNote));
        } else {
            //создаем новую заметку:
            Calendar calendar = Calendar.getInstance();
            noteDate.setText(String.format(Locale.US, "%02d.%02d.%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        }

        noteEditTextFragment.findViewById(R.id.saveAndCloseButton).setOnClickListener(v -> saveAndCloseButtonAction());
        noteDate.setOnClickListener(v -> setDateFromDatePicker(noteDate));

        return noteEditTextFragment;
    }

    @Override
    public void onPause() {
        saveNoteChanges();
        super.onPause();
    }

    private void saveAndCloseButtonAction() {
        saveNoteChanges();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void setDateFromDatePicker(TextView noteDate) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> noteDate.setText(String.format(Locale.US, "%02d.%02d.%04d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void saveNoteChanges() {
        EditText noteTitle = Objects.requireNonNull(getActivity()).findViewById(R.id.noteTitle);
        EditText noteText = Objects.requireNonNull(getActivity()).findViewById(R.id.noteText);
        TextView noteDate = Objects.requireNonNull(getActivity()).findViewById(R.id.noteDate);

        if (noteTitle != null && noteText != null && noteDate != null) {
            if (currentIndexOfNote >= 0) {
                //Редактирование существующей заметки
                String oldTitle = SingleObjectsGetter.getNotes().getNoteTitleByIndex(currentIndexOfNote);
                SingleObjectsGetter.getNotes().updateNoteByIndex(currentIndexOfNote, noteTitle.getText().toString(), noteText.getText().toString(), noteDate.getText().toString());

                if (!oldTitle.equals(noteTitle.getText().toString())) { //если заголовок изменился - тоже делаем обновление списка
                    SingleObjectsGetter.getBus().post(new EventUpdateNoteTitles(currentIndexOfNote));
                    Log.d("currentIndexOfNote", "currentIndexOfNote in NoteDetailFragment: " + currentIndexOfNote);
                }
            } else {
                //Создание новой заметки:
                SingleObjectsGetter.getNotes().addNote(noteTitle.getText().toString(), noteText.getText().toString(), noteDate.getText().toString());
                currentIndexOfNote = SingleObjectsGetter.getNotes().getAllNotesTitles().size() - 1;
                SingleObjectsGetter.getBus().post(new EventUpdateNoteTitles(currentIndexOfNote));
                Log.d("currentIndexOfNote", "currentIndexOfNote in NoteDetailFragment: " + currentIndexOfNote);
            }
        }
    }

}