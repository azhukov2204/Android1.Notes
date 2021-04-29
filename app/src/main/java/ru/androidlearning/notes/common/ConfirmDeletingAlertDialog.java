package ru.androidlearning.notes.common;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


import ru.androidlearning.notes.R;

public class ConfirmDeletingAlertDialog {

    public static void runDialog(@NonNull Context context, DeleteNoteAction deleteNoteAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention)
                .setMessage(R.string.confirmDeleteMessage)
                .setCancelable(false)
                .setNegativeButton(R.string.declineButtonText, (dialog, which) -> Toast.makeText(context, R.string.deletionCanceledMessage, Toast.LENGTH_SHORT).show())
                .setPositiveButton(R.string.confirmButtonText, (dialog, which) -> deleteNoteAction.deleteNoteAction());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
