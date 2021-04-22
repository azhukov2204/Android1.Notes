package ru.androidlearning.notes.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import ru.androidlearning.notes.R;
import ru.androidlearning.notes.data.NoteEntry;
import ru.androidlearning.notes.data.Notes;

public class NoteTitlesAdapter extends RecyclerView.Adapter<NoteTitlesAdapter.ViewHolder> {

    private Notes notes;
    private OnItemClickListener onItemClickListener;
    private final Fragment fragment;
    private int menuPosition;

    public NoteTitlesAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteTitlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteTitlesAdapter.ViewHolder holder, int position) {
        holder.setData(notes.getNoteEntryByIndex(position));
    }

    @Override
    public int getItemCount() {
        return notes.getAllNotesTitles().size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public int getMenuPosition() {
        return menuPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteDate;
        TextView noteDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteCardTitle);
            noteDate = itemView.findViewById(R.id.noteCardDate);
            noteDetails = itemView.findViewById(R.id.noteCardDetail);

            registerContextMenu(itemView);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            /*itemView.setOnLongClickListener(v -> {
                menuPosition = getLayoutPosition();
                return false;
            });*/

        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null) {
                itemView.setOnLongClickListener(v -> {
                    menuPosition = getLayoutPosition();
                    return false;
                });
                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(NoteEntry noteEntry) {
            noteTitle.setText(noteEntry.getNoteTitle());
            noteDate.setText(noteEntry.getNoteCreatedDate());
            noteDetails.setText(noteEntry.getNoteText());
        }
    }
}
