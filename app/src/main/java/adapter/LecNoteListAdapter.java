package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kairanpa.se.lecshare.R;

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
        TextView subject = lecNoteItem.findViewById(R.id.lec_note_list_item_subject);
        TextView owner = lecNoteItem.findViewById(R.id.lec_note_list_item_owner);
        TextView score = lecNoteItem.findViewById(R.id.lec_note_list_item_score);
        TextView date = lecNoteItem.findViewById(R.id.lec_note_list_item_date);
        if (position %2 == 1)
        {
            lecNoteItem.setBackgroundColor(Color.parseColor("#FCDF91"));
        }
        title.setText(lecNote.get(position).getTitle());
        subject.setText(lecNote.get(position).getSubject());
        owner.setText(lecNote.get(position).getOwner());
        date.setText(lecNote.get(position).UploadTimeReverse());
        score.setText(lecNote.get(position).getScore() + "");
        return lecNoteItem;
    }
}
