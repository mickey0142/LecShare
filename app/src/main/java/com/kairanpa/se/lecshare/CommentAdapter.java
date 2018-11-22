package com.kairanpa.se.lecshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Comment;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private List<Comment> comments = new ArrayList<>();
    private Context context;
    private String _content, _userName, _timeStamp;


    public CommentAdapter (@NonNull Context context, int resource, @NonNull List<Comment> objects){
        super(context, resource, objects);
        this.context = context;
        this.comments = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View commentItem = LayoutInflater.from(context).inflate(R.layout.fragment_list_comment, parent, false);

        TextView userPost = commentItem.findViewById(R.id.comment_host);
        TextView timeStamp = commentItem.findViewById(R.id.comment_time);
        TextView content = commentItem.findViewById(R.id.comment_content);

        Comment row = comments.get(position);
        _userName = row.getUserName();
        _content = row.getComment();
        _timeStamp = row.getCommentTimeStamp();

        userPost.setText(_userName);
        content.setText(_content);
        timeStamp.setText(_timeStamp);

        return commentItem;
    }

    public Comment getItem(int position){
        return comments.get(position);
    }
}
