package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    private List<Comment> comments;
    private Context context;
    private User user;

    public CommentAdapter (@NonNull Context context, int resource, @NonNull List<Comment> objects, User user){
        super(context, resource, objects);
        this.context = context;
        this.comments = objects;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final View commentItem = LayoutInflater.from(context).inflate(R.layout.fragment_list_comment, parent, false);

        final ProgressBar progressBar = commentItem.findViewById(R.id.comment_progress_bar);
        final Comment comment = comments.get(position);
        TextView owner = commentItem.findViewById(R.id.comment_owner);
        TextView time = commentItem.findViewById(R.id.comment_time);
        final TextView content = commentItem.findViewById(R.id.comment_content);
        String ownerStr = comment.getUserName();
        String timeStr = comment.getCommentTimeStamp();
        String contentStr = comment.getComment();
        owner.setText("Username : " + ownerStr);
        time.setText("Time : " + timeStr);
        content.setText(contentStr);

        final Button editButton = commentItem.findViewById(R.id.comment_edit_button);
        final Button updateButton = commentItem.findViewById(R.id.comment_edit_update);
        final Button cancelButton = commentItem.findViewById(R.id.comment_edit_cancel);
        final EditText editContent = commentItem.findViewById(R.id.comment_edit_content);

        if (user.getDocumentId().equals(comment.getUserNameID()))
        {
            editButton.setVisibility(View.VISIBLE);
        }
        else
        {
            editButton.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                editContent.setVisibility(View.VISIBLE);
                editContent.setText(comment.getComment());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                editContent.setVisibility(View.GONE);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editContent.getText().toString();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentItem.getWindowToken(), 0);
                if (text.isEmpty() || text.equals(""))
                {
                    Toast.makeText(getContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                comment.setComment(text);
                fbStore.collection("Comment").document(comment.getDocumentID()).set(comment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "update complete", Toast.LENGTH_SHORT).show();
                                Log.d("test", "update comment complete");
                                content.setText(comment.getComment());
                                editButton.setVisibility(View.VISIBLE);
                                updateButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.GONE);
                                content.setVisibility(View.VISIBLE);
                                editContent.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("test", "update comment error : " + e.getMessage());
                    }
                });
            }
        });

        return commentItem;
    }

    public Comment getItem(int position){
        return comments.get(position);
    }
}
