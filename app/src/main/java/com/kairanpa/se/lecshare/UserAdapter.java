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

import model.User;

public class UserAdapter extends ArrayAdapter {


    Context context;
    ArrayList<User> userList;

    public UserAdapter(Context context, int  resource, ArrayList<User> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.userList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View fileItem = LayoutInflater.from(context).inflate(R.layout.fragment_user_list_item, parent, false);
        TextView username = fileItem.findViewById(R.id.user_list_item_username);
        TextView email = fileItem.findViewById(R.id.user_list_item_email);
        TextView score = fileItem.findViewById(R.id.user_list_item_score);
        User user = userList.get(position);
        username.setText("Username : " + user.getUsername());
        email.setText("Email : " + user.getEmail());
        score.setText("Score : " + user.getAverageScore());

        return fileItem;
    }
}
