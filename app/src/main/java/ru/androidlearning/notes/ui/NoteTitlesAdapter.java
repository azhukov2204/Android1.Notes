package ru.androidlearning.notes.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.androidlearning.notes.R;
import ru.androidlearning.notes.data.Notes;

public class NoteTitlesAdapter extends RecyclerView.Adapter<NoteTitlesAdapter.ViewHolder> {

    private final Notes notes;
    private OnItemClickListener onItemClickListener;

    public NoteTitlesAdapter(Notes notes) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteDate;
        TextView noteDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteCardTitle);
            noteDate = itemView.findViewById(R.id.noteCardDate);
            noteDetails = itemView.findViewById(R.id.noteCardDetail);

            itemView.findViewById(R.id.noteCardView).setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

        }

        public void setData(Notes.NoteEntry noteEntry) {
            noteTitle.setText(noteEntry.getNoteTitle());
            noteDate.setText(noteEntry.getNoteCreatedDate());
            noteDetails.setText(noteEntry.getNoteText());
        }
    }

}
