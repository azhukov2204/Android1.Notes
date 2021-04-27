package ru.androidlearning.notes.common;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.androidlearning.notes.R;

public class EditNoteTitleDialog extends DialogFragment {

    private EditText editTitleEditText;
    private final TextView targetTextView;

    public EditNoteTitleDialog(TextView targetTextView) {
        this.targetTextView = targetTextView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialogfragment_edit_title, null);
        editTitleEditText = view.findViewById(R.id.editTitle);

        view.findViewById(R.id.editTitleCancelButton).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.editTitleConfirmButton).setOnClickListener(v -> {
            targetTextView.setText(editTitleEditText.getText());
            dismiss();
        });

        setCancelable(false);
        String text = targetTextView.getText().toString();
        editTitleEditText.setText(text);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause() {
        dismiss(); //такой "костыль", чтоб не было падения при смене ориентации. Т.к. в конструктор подается вьюха, которая уничтожается при смене ориентации. Все равно планирую отказаться от этого диалога
        super.onPause();
    }
}
