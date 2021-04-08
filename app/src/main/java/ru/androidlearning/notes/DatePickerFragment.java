package ru.androidlearning.notes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends Fragment {


    private static final String ARG_PARAM = "noteDateTextView";

    private TextView noteDateTextView;

    public DatePickerFragment() {
        // Required empty public constructor
    }


    public DatePickerFragment(TextView noteDateTextView) {
        this.noteDateTextView = noteDateTextView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View datePickerFragment = inflater.inflate(R.layout.fragment_date_picker, container, false);

        DatePicker datePicker = datePickerFragment.findViewById(R.id.datePicker);
        Calendar today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
            noteDateTextView.setText("surprise");
        });



        return datePickerFragment;
    }
}