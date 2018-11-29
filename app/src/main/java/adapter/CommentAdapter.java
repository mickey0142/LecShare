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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private Button editUpdate, editCancel, editBtn;
    private ProgressBar progressBar;
//    private Comment comment;
//    private LecNote lecNote;



    public CommentAdapter (@NonNull Context context, int resource, @NonNull List<Comment> objects, User user){
        super(context, resource, objects);
        this.context = context;
        this.comments = objects;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        View commentItem = LayoutInflater.from(context).inflate(R.layout.fragment_list_comment, parent, false);

        userPost = commentItem.findViewById(R.id.comment_host);
        timeStamp = commentItem.findViewById(R.id.comment_time);
        content = commentItem.findViewById(R.id.comment_content);

        editBtn = commentItem.findViewById(R.id.comment_edit_button);

        editContent = commentItem.findViewById(R.id.comment_edit_content);
        editCancel = commentItem.findViewById(R.id.comment_edit_cancel);
        editUpdate = commentItem.findViewById(R.id.comment_edit_update);

        progressBar = commentItem.findViewById(R.id.comment_progressingBar);
//        editHost = commentItem.findViewById(R.id.comment_edit_host);
//        editTime = commentItem.findViewById(R.id.comment_edit_time);

        final Comment row = comments.get(position);

        userPost.setText("User : " + row.getUserName());
        timeStamp.setText("Time : " + row.getCommentTimeStamp());
        content.setText(row.getComment());
        editContent.setText(row.getComment());

        editUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row.setComment(content.getText().toString());
                renderPost();
                firestore.collection("Comment").document(row.getDocumentID()).update("comment", editContent.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("test", "error edit comment: " + e.getMessage());
                    }
                });
            }
        });

        if (user.getDocumentId().equals(row.getUserNameID())){
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBtn.setVisibility(View.GONE);
                    content.setVisibility(View.GONE);
                    editContent.setVisibility(View.VISIBLE);
                    editCancel.setVisibility(View.VISIBLE);
                    editUpdate.setVisibility(View.VISIBLE);
                }
            });
        }
        else{
            editBtn.setVisibility(View.GONE);
        }
//        if (user.equals(row.getUserNameID())) // if current user is owner of this comment
//        {
//            // set visible to edittext
//            editContent.setVisibility(View.VISIBLE);
//            // set gone to textview
//            editHost.setVisibility(View.GONE);
//            editTime.setVisibility(View.GONE);
//            final ProgressBar progressBar = commentItem.findViewById(R.id.comment_progressingBar);
//            // set button onclick to update edited comment here (upload new comment to firebase here)
//            updateComment = commentItem.findViewById(R.id.comment_update_button);
//            updateComment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    row.setComment(content.getText().toString());
//                    readerPost();
//                    firestore.collection("Comment").document(row.getDocumentID()).update("comment", content).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressBar.setVisibility(View.GONE);
//                            Log.d("test", "error edit comment: " + e.getMessage());
//                        }
//                    });
//                }
//            });
//        }
//        else
//        {
//            // set gone to edittext
//            editContent.setVisibility(View.GONE);
//            // set visible to textview
//            editHost.setVisibility(View.VISIBLE);
//            editTime.setVisibility(View.VISIBLE);
//            _userName = row.getUserName();
//            _content = row.getComment();
//            _timeStamp = row.getCommentTimeStamp();
//
//            userPost.setText(_userName);
//            content.setText(_content);
//            timeStamp.setText(_timeStamp);
//        }

        return commentItem;
    }

    public Comment getItem(int position){
        return comments.get(position);
    }

    void renderPost(){
            this.notifyDataSetChanged();
    }
}
