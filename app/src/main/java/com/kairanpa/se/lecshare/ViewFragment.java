package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.CommentAdapter;
import adapter.PictureAdapter;
import model.Comment;
import model.LecNote;
import model.User;

public class ViewFragment extends Fragment{

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    LecNote lecNote;
    User user;
    ListView commentList;
    ArrayList<Comment> comments = new ArrayList<Comment>();
    CommentAdapter commentAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lecNote = (LecNote) bundle.getSerializable("LecNote object");
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTextBox();
        initBackButton();
        initEditButton();
        showImage();
        initToolbar();
        initDownloadAllButton();
        initVoteStar();
        CommentBtn();
        initShowComment();

//        TextView textView = getView().findViewById(R.id.view_comment_form);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("User object", user);
//                bundle.putSerializable("LecNote object", lecNote);
//                Fragment fragment = new CommentFragment();
//                fragment.setArguments(bundle);
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
//            }
//        });
    }

    public void setTextBox()
    {
        TextView titleBox = getView().findViewById(R.id.view_lec_note_title);
        titleBox.setText("Title : " + lecNote.getTitle());
        TextView ownerBox = getView().findViewById(R.id.view_lec_note_owner);
        ownerBox.setText("Author : " + lecNote.getOwner());
        TextView descriptionBox = getView().findViewById(R.id.view_lec_note_description);
        descriptionBox.setText("Description : " + lecNote.getDescription());
    }

    public void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.view_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initEditButton()
    {
        TextView _editButton = getView().findViewById(R.id.view_edit_post_button);
        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putSerializable("LecNote object", lecNote);
                Fragment fragment = new EditFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
            }
        });
        if (user.getDocumentId().equals(lecNote.getOwnerId())){
            _editButton.setVisibility(View.VISIBLE);
        }
        else {
            _editButton.setVisibility(View.GONE);

        }
    }

    public void showImage()
    {
        ArrayList<String> pictureOnly = new ArrayList<>();
        ArrayList<String> pdfOnly = new ArrayList<>();
        ArrayList<String> combine = new ArrayList<>();
        for (int i = 0; i < lecNote.getFilesName().size(); i++)
        {
            if (lecNote.getFilesName().get(i).endsWith(".pdf"))
            {
                pdfOnly.add(lecNote.getFilesName().get(i));
            }
            else
            {
                pictureOnly.add(lecNote.getFilesName().get(i));
            }
        }
        for (int i = 0; i < pictureOnly.size(); i++)
        {
            combine.add(pictureOnly.get(i));
        }
        for (int i = 0; i < pdfOnly.size(); i++)
        {
            combine.add(pdfOnly.get(i));
        }
        ListView pictureListView = getView().findViewById(R.id.view_picture_list_view);
        PictureAdapter pictureAdapter = new PictureAdapter(getActivity(), R.layout.fragment_picture_list_item, combine);
        pictureListView.setAdapter(pictureAdapter);
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.view_toolbar);
        mTool.inflateMenu(R.menu.fragment_menu);
        mTool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Log.d("test", "itemId : " + itemId);
                if (itemId == R.id.menu_home)
                {
                    Log.d("test", "press home");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, homeFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_profile)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    bundle.putSerializable("Target object", user);
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_upload)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment uploadFragment = new UploadFragment();
                    uploadFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, uploadFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_search_user)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment fragment = new SearchUserFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_logout)
                {
                    Log.d("test", "press logout");
                    fbAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .commit();
                }
                return false;
            }
        });
        TextView titleText = getView().findViewById(R.id.view_title_text);
        titleText.setText(lecNote.getTitle());
    }

    public int convertToPixel(double dp)
    {
        float scale = getContext().getResources().getDisplayMetrics().density;
        int pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }

    public void initDownloadAllButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.view_download_all_progress_bar);
        final Button downloadAllButton = getView().findViewById(R.id.view_download_all_button);
        if (lecNote.getFilesName().size() == 0)
        {
            downloadAllButton.setVisibility(View.GONE);
        }
        downloadAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Integer> allProgress = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                for (int i = 0; i < lecNote.getFilesName().size(); i++)
                {
                    allProgress.add(0);
                }
                for (int i = 0; i < lecNote.getFilesName().size(); i++)
                {
                    try {
                        File directory = new File(Environment.getExternalStorageDirectory(), "LectureNote");
                        if (!directory.exists()) {
                            boolean check = directory.mkdir();
                            Log.d("test", "make directory : " + check);
                        }
                        final int temp = i;
                        final File file = new File(directory, lecNote.getFilesName().get(i));
                        FirebaseStorage fbStorage = FirebaseStorage.getInstance();
                        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com")
                                .child(lecNote.getFilesName().get(i));
                        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("test", "download success");
                                Log.d("test", "create at : " + file.toString());
                                Toast.makeText(getContext(), "download " + lecNote.getFilesName().get(temp) + " completed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("test", "download failed");
                                Toast.makeText(getContext(), "download error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                int progressInt = (int) progress;
                                allProgress.set(temp, progressInt);
                                int sumProgress = 0;
                                for (int j = 0; j < allProgress.size(); j++)
                                {
                                    sumProgress += allProgress.get(j);
                                }
                                sumProgress /= allProgress.size();
                                progressBar.setProgress(sumProgress);
                                if (sumProgress == 100)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    downloadAllButton.setEnabled(false);
                                    downloadAllButton.setText("Download completed");
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.d("test", "download error : " + e.getMessage());
                    }
                }
            }
        });
    }

    void initVoteStar()
    {
        LinearLayout voteLayout = getView().findViewById(R.id.view_vote_linear_layout);
        if (lecNote.getOwnerId().equals(user.getDocumentId()))
        {
            voteLayout.setVisibility(View.GONE);
        }
        final RatingBar voteStar = getView().findViewById(R.id.view_score);
        final TextView averageScore = getView().findViewById(R.id.view_average_score);
        averageScore.setText("Average score : " + lecNote.getScore());
        if (lecNote.getVote().get(user.getUsername()) != null)
        {
            voteStar.setRating(lecNote.getVote().get(user.getUsername()));
        }
        final ProgressBar progressBar = getView().findViewById(R.id.view_vote_progress_bar);
        Button voteButton = getView().findViewById(R.id.view_vote_button);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (lecNote.getVote().get(user.getUsername()) == null)
                {
                    user.addMoney(10);
                    fbStore.collection("User").document(user.getUsername()).update("money", user.getMoney())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("test", "money added");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("test", "money add failed : " + e.getMessage());
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    fbStore.collection("User").document(lecNote.getOwnerId()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d("test", "documentSnapshot : " + documentSnapshot);
                                    Log.d("test", "username : " + documentSnapshot.getString("username"));
                                    Log.d("test", "username(object) : " + documentSnapshot.get("username"));
                                    Log.d("test", "money : " + documentSnapshot.get("money"));
                                    Log.d("test", "money(double) : " + documentSnapshot.getDouble("money"));
                                    Double doubleMoney = documentSnapshot.getDouble("money");
                                    int ownerMoney = doubleMoney.intValue();
                                    ownerMoney += 10 * voteStar.getRating();
                                    fbStore.collection("User").document(lecNote.getOwnerId())
                                            .update("money", ownerMoney)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("test", "add money for owner success");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Log.d("test", "add money for owner fail : " + e.getMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("test", "get owner fail : " + e.getMessage());
                        }
                    });
                }
                lecNote.addVote(user.getUsername(), voteStar.getRating());
                fbStore.collection("LecNote").document(lecNote.getDocumentId())
                        .set(lecNote)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("test", "vote success");
                                Toast.makeText(getContext(), "Vote Success", Toast.LENGTH_SHORT).show();
                                averageScore.setText("Average score : " + lecNote.getScore());
                                calculateUserAverageScore(lecNote.getOwnerId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("test", "vote error : " + e.getMessage());
                        Toast.makeText(getContext(), "Vote Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void calculateUserAverageScore(final String userId)
    {
        fbStore.collection("LecNote").whereEqualTo("ownerId", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            double sumScore = 0;
                            int count = 0;
                            for (DocumentSnapshot doc : task.getResult())
                            {
                                Log.d("test", "score in loop : " + doc.getDouble("score"));
                                sumScore += doc.getDouble("score");
                                count += 1;
                            }
                            Log.d("test", "sum score = " + sumScore + " count = " + count);
                            double averageScore = sumScore / count;
                            Log.d("test", "averageScore : " + averageScore);
                            fbStore.collection("User").document(userId)// over here
                                    .update("averageScore", averageScore)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("test", "update owner average score success");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("test", "update owner average score fail : " + e.getMessage());
                                }
                            });
                        }
                        else
                        {
                            Log.d("test", "get user average score error : " + task.getException());
                        }
                    }
                });
    }

    void uploadComment(final Comment comment){
        fbStore.collection("Comment").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                fbStore.collection("Comment").document(documentReference.getId())
                        .update("documentID", documentReference.getId())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("test", "Add comment to firebase success.");
                                Toast.makeText(getContext(), "Comment success", Toast.LENGTH_SHORT).show();
                                initShowComment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("User object", user);
                                bundle.putSerializable("LecNote object", lecNote);
                                Fragment fragment = new ViewFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                transaction.replace(R.id.main_view, fragment).commit();
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

    void CommentBtn(){
        Button button = getView().findViewById(R.id.commentBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = getView().findViewById(R.id.comment_form);
                String form_check = editText.getText().toString();
                if (form_check.isEmpty()){
                    Toast.makeText(getActivity(), "Comment is empty. Please comment something.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Comment comment = new Comment();
                comment.setUserName(user.getUsername());
                comment.setUserNameID(user.getDocumentId());
                comment.setLecNoteID(lecNote.getDocumentId());
                comment.setComment(form_check);
                comment.setCommentTimeStamp();
                uploadComment(comment);
            }
        });
    }

    void initShowComment(){
        fbStore.collection("Comment").whereEqualTo("lecNoteID", lecNote.getDocumentId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    comments = new ArrayList<>();
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

    void readerPost(){
        if (getView() != null){
            commentList = getView().findViewById(R.id.comment_list);
            commentList.setDivider(null);
            commentAdapter = new CommentAdapter(getActivity(), R.layout.fragment_list_comment, comments, user);
            commentList.setAdapter(commentAdapter);
        }
    }
}
