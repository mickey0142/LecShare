package com.kairanpa.se.lecshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.LecNote;

public class LecNoteListAdapter extends ArrayAdapter {

    Context context;
    ArrayList<LecNote> lecNote;

    public LecNoteListAdapter(Context context, int  resource, ArrayList<LecNote> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.lecNote = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View lecNoteItem = LayoutInflater.from(context).inflate(R.layout.fragment_lec_note_list_item, parent, false);
        TextView title = lecNoteItem.findViewById(R.id.lec_note_list_item_title);
//        TextView owner = lecNoteItem.findViewById(R.id.lec_note_list_item_owner);
//        TextView score = lecNoteItem.findViewById(R.id.lec_note_list_item_score);
        TextView date = lecNoteItem.findViewById(R.id.lec_note_list_item_date);

        title.setText(lecNote.get(position).getTitle());
        date.setText(lecNote.get(position).getUploadTimeStamp());
        return lecNoteItem;
    }
}
