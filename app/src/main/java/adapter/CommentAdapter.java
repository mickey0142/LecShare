package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kairanpa.se.lecshare.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Comment;
import model.LecNote;
import model.User;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<Comment> comments = new ArrayList<>();
    private Context context;
    private String _content, _userName, _timeStamp;
    private User user;
    private TextView userPost, timeStamp, content, editHost, editTime;
    private EditText editContent;
    private Button updateComment;
    private Comment comment;


    public CommentAdapter (@NonNull Context context, int resource, @NonNull List<Comment> objects, User user){
        super(context, resource, objects);
        this.context = context;
        this.comments = objects;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View commentItem = LayoutInflater.from(context).inflate(R.layout.fragment_list_comment, parent, false);

        userPost = commentItem.findViewById(R.id.comment_host);
        timeStamp = commentItem.findViewById(R.id.comment_time);
        content = commentItem.findViewById(R.id.comment_content);

        editContent = commentItem.findViewById(R.id.comment_edit_content);
        editHost = commentItem.findViewById(R.id.comment_edit_host);
        editTime = commentItem.findViewById(R.id.comment_edit_time);

        Comment row = comments.get(position);

        if (user.equals(row.getUserNameID())) // if current user is owner of this comment
        {
            // set visible to edittext
            editContent.setFocusableInTouchMode(true);
            // set gone to textview
            editHost.setFocusable(false);
            editTime.setFocusable(false);
            // set button onclick to update edited comment here (upload new comment to firebase here)
            updateComment = commentItem.findViewById(R.id.comment_update_button);
            updateComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firestore.collection("Comment").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            firestore.collection("Comment").document(documentReference.getId())
                                    .update("documentID", documentReference.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("test", "Update comment to firebase success.");
                                            Toast.makeText(getContext(), "Comment success", Toast.LENGTH_SHORT).show();
// i have question ??? 1.similar add comment? 2.why not create new class?
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("test", "Comment: Update documentId to firebase error: " + e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("test", "Comment: add comment to firebase error: " + e.getMessage());
                        }
                    });
                }
            });
        }
        else
        {

            // set gone to edittext
            editContent.setFocusable(false);
            // set visible to textview
            editHost.setFocusableInTouchMode(true);
            editTime.setFocusableInTouchMode(true);
            _userName = row.getUserName();
            _content = row.getComment();
            _timeStamp = row.getCommentTimeStamp();

            userPost.setText(_userName);
            content.setText(_content);
            timeStamp.setText(_timeStamp);
        }

        return commentItem;
    }

    public Comment getItem(int position){
        return comments.get(position);
    }
}
