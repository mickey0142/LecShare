package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.CommentAdapter;
import model.Comment;
import model.LecNote;
import model.User;

public class CommentFragment extends Fragment {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Button postBtn;
    private EditText comment_form;
    private User user;
    private LecNote lecNote;
    private ListView commentList;
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        lecNote = (LecNote) bundle.getSerializable("LecNote object");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCommentBtn();
        initShowComment();

    }

    private void uploadComment(final Comment comment){
        firestore.collection("Comment").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                firestore.collection("Comment").document(documentReference.getId())
                        .update("documentID", documentReference.getId())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("test", "Add comment to firebase success.");
                        Toast.makeText(getContext(), "Comment success", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User object", user);
                        bundle.putSerializable("LecNote object", lecNote);
                        Fragment viewPage = new ViewFragment();
                        viewPage.setArguments(bundle);
                        FragmentTransaction tragesaction = getActivity().getSupportFragmentManager().beginTransaction();
                        tragesaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        tragesaction.replace(R.id.main_view, viewPage).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("test", "Comment: add documentId to firebase error: " + e.getMessage());
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

    private void initCommentBtn(){
        postBtn = getView().findViewById(R.id.commentBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_form = getView().findViewById(R.id.comment_form);
                String comment_form_check = comment_form.getText().toString();
                if (comment_form_check.isEmpty()){
                    Toast.makeText(getActivity(), "Comment is not empty. Please comment something.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Comment comment = new Comment();
                comment.setUserName(user.getUsername());
                comment.setUserNameID(user.getDocumentId());
                comment.setComment(comment_form_check);
                comment.setCommentTimeStamp();
                uploadComment(comment);

            }
        });
    }

    private void initShowComment(){
        firestore.collection("Comment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc: task.getResult()){
                        Comment comment = doc.toObject(Comment.class);
                        comments.add(comment);
                        }
                        readerPost();
                }
                else{
                    Log.d("test", "Comment: get comment error: " + task.getException());
                }
            }
        });
    }

    private void readerPost(){
        if (getView() != null){
            commentList = getView().findViewById(R.id.comment_list);
            commentList.setDivider(null);
            commentAdapter = new CommentAdapter(getActivity(), R.layout.fragment_list_comment, comments);
            commentList.setAdapter(commentAdapter);
        }
    }

}
