package ru.androidlearning.notes.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import ru.androidlearning.notes.R;

import static androidx.core.content.ContextCompat.getSystemService;

public class EnterNoteTitleDialog extends DialogFragment {

    private EditText enterTitleEditText;
    private final TextView targetTextView;

    public EnterNoteTitleDialog(TextView targetTextView) {
        this.targetTextView = targetTextView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialogfragment_edit_title, null);
        enterTitleEditText = view.findViewById(R.id.editTitle);

        view.findViewById(R.id.editTitleCancelButton).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.editTitleConfirmButton).setOnClickListener(v -> {
            targetTextView.setText(enterTitleEditText.getText());
            dismiss();
        });

        setCancelable(false);

        String text = targetTextView.getText().toString();
        enterTitleEditText.setText(text);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
