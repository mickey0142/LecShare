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

import model.LecNote;
import model.User;

public class ViewFragment extends Fragment{

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    LecNote lecNote;
    User user;

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
        showImage();
        initToolbar();
        initDownloadAllButton();
        initVoteStar();
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

    public void showImage()
    {
        final ArrayList<String> pdfList = new ArrayList<>();
        LinearLayout imageLinearLayout = getView().findViewById(R.id.view_picture_linear_layout);
        for (int i = 0; i < lecNote.getFilesName().size(); i++)
        {
            if (lecNote.getFilesName().get(i).endsWith(".pdf"))
            {
                pdfList.add(lecNote.getFilesName().get(i));
                Log.d("test", "it is pdf add to arraylist");
                continue;
            }
            final int pos = i;
            int temp = convertToPixel(30);

            ImageView fileImage = new ImageView(getContext());
            imageLinearLayout.addView(fileImage);

            LinearLayout bottomLinearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams bottomLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bottomLayoutParam.setMargins(0, 0, 0, convertToPixel(10));
            bottomLinearLayout.setLayoutParams(bottomLayoutParam);
            bottomLinearLayout.setOrientation(LinearLayout.VERTICAL);

            temp = convertToPixel(50);
            TextView imageName = new TextView(getContext());
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            float scale = getContext().getResources().getDisplayMetrics().density;
            int textSize = (int) ((dpWidth*0.87) * scale + 0.5f);
            imageName.setLayoutParams(new LinearLayout.LayoutParams(textSize, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageName.setText(lecNote.getFilesName().get(i));
            imageName.setGravity(Gravity.CENTER_VERTICAL);
            bottomLinearLayout.addView(imageName);

            final ProgressBar progressBar = new ProgressBar(getContext());
            LinearLayout.LayoutParams progressParam = new LinearLayout.LayoutParams(temp, temp);
            progressParam.gravity = Gravity.END;
            progressParam.topMargin = -(convertToPixel(20));
            progressBar.setLayoutParams(progressParam);
            progressBar.setVisibility(View.GONE);
            bottomLinearLayout.addView(progressBar);

            final ImageView downloadCompleted = new ImageView(getContext());
            LinearLayout.LayoutParams completedParam = new LinearLayout.LayoutParams(temp, temp);
            completedParam.gravity = Gravity.END;
            completedParam.topMargin = -(convertToPixel(20));
            downloadCompleted.setLayoutParams(completedParam);
            downloadCompleted.setImageResource(R.drawable.ico_yes);
            downloadCompleted.setVisibility(View.GONE);
            bottomLinearLayout.addView(downloadCompleted);

            final ImageView downloadButton = new ImageView(getContext());
            temp = convertToPixel(35);
            LinearLayout.LayoutParams downloadParam = new LinearLayout.LayoutParams(temp, temp);
            downloadParam.gravity = Gravity.END;
            downloadParam.topMargin = -(convertToPixel(20));
            downloadButton.setLayoutParams(downloadParam);
            downloadButton.setImageResource(R.drawable.ico_download);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    downloadCompleted.setVisibility(View.INVISIBLE);
                    downloadButton.setVisibility(View.GONE);
                    try
                    {
                        File directory = new File(Environment.getExternalStorageDirectory(), "LectureNote");
                        if(!directory.exists())
                        {
                            boolean check = directory.mkdir();
                            Log.d("test", "make directory : " + check);
                        }
                        final File file = new File(directory, lecNote.getFilesName().get(pos));
                        FirebaseStorage fbStorage = FirebaseStorage.getInstance();
                        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com")
                                .child(lecNote.getFilesName().get(pos));
                        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("test", "download success");
                                Log.d("test", "create at : " + file.toString());
                                Toast.makeText(getContext(), "download " + lecNote.getFilesName().get(pos) + " completed", Toast.LENGTH_SHORT).show();
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
                                progressBar.setProgress(progressInt);
                                if (progressInt == 100)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    downloadCompleted.setVisibility(View.VISIBLE);
                                    downloadButton.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.d("test", "error from filelistdownloadadapter : " + e.getMessage());
                    }
                }
            });
            bottomLinearLayout.addView(downloadButton);
            imageLinearLayout.addView(bottomLinearLayout);
            Log.d("test", "file name : " + lecNote.getFilesName().get(0));
            StorageReference imageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com").child(lecNote.getFilesName().get(i));
            GlideApp.with(getContext()).load(imageRef).into(fileImage);
        }
        for (int i = 0; i < pdfList.size(); i++)
        {
            int temp = convertToPixel(30);
            final int pos = i;
            LinearLayout bottomLinearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams bottomLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bottomLayoutParam.setMargins(0, 0, 0, convertToPixel(10));
            bottomLinearLayout.setLayoutParams(bottomLayoutParam);
            bottomLinearLayout.setOrientation(LinearLayout.VERTICAL);

            temp = convertToPixel(50);
            TextView pdfName = new TextView(getContext());
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            float scale = getContext().getResources().getDisplayMetrics().density;
            int textSize = (int) ((dpWidth*0.87) * scale + 0.5f);
            pdfName.setLayoutParams(new LinearLayout.LayoutParams(textSize, ViewGroup.LayoutParams.WRAP_CONTENT));
            pdfName.setText(pdfList.get(i));
            pdfName.setGravity(Gravity.CENTER_VERTICAL);
            bottomLinearLayout.addView(pdfName);

            final ProgressBar progressBar = new ProgressBar(getContext());
            LinearLayout.LayoutParams progressParam = new LinearLayout.LayoutParams(temp, temp);
            progressParam.gravity = Gravity.END;
            progressParam.topMargin = -(convertToPixel(20));
            progressBar.setLayoutParams(progressParam);
            progressBar.setVisibility(View.GONE);
            bottomLinearLayout.addView(progressBar);

            final ImageView downloadCompleted = new ImageView(getContext());
            LinearLayout.LayoutParams completedParam = new LinearLayout.LayoutParams(temp, temp);
            completedParam.gravity = Gravity.END;
            completedParam.topMargin = -(convertToPixel(20));
            downloadCompleted.setLayoutParams(completedParam);
            downloadCompleted.setImageResource(R.drawable.ico_yes);
            downloadCompleted.setVisibility(View.GONE);
            bottomLinearLayout.addView(downloadCompleted);

            final ImageView downloadButton = new ImageView(getContext());
            temp = convertToPixel(35);
            LinearLayout.LayoutParams downloadParam = new LinearLayout.LayoutParams(temp, temp);
            downloadParam.gravity = Gravity.END;
            downloadParam.topMargin = -(convertToPixel(20));
            downloadButton.setLayoutParams(downloadParam);
            downloadButton.setImageResource(R.drawable.ico_download);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    downloadCompleted.setVisibility(View.INVISIBLE);
                    downloadButton.setVisibility(View.GONE);
                    try
                    {
                        File directory = new File(Environment.getExternalStorageDirectory(), "LectureNote");
                        if(!directory.exists())
                        {
                            boolean check = directory.mkdir();
                            Log.d("test", "make directory : " + check);
                        }
                        final File file = new File(directory, pdfList.get(pos));
                        FirebaseStorage fbStorage = FirebaseStorage.getInstance();
                        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com")
                                .child(pdfList.get(pos));
                        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("test", "download success");
                                Log.d("test", "create at : " + file.toString());
                                Toast.makeText(getContext(), "download " + pdfList.get(pos) + " completed", Toast.LENGTH_SHORT).show();
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
                                progressBar.setProgress(progressInt);
                                if (progressInt == 100)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    downloadCompleted.setVisibility(View.VISIBLE);
                                    downloadButton.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.d("test", "error from filelistdownloadadapter : " + e.getMessage());
                    }
                }
            });
            bottomLinearLayout.addView(downloadButton);
            imageLinearLayout.addView(bottomLinearLayout);
        }
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
        if (lecNote.getOwner().equals(user.getUsername()))
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
        Button voteButton = getView().findViewById(R.id.view_vote_button);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        }
                    });
                    fbStore.collection("User").document(lecNote.getOwner()).get()
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
                                    fbStore.collection("User").document(lecNote.getOwner())
                                            .update("money", ownerMoney)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("test", "add money for owner success");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("test", "add money for owner fail : " + e.getMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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
                                Log.d("test", "vote success");
                                Toast.makeText(getContext(), "Vote Success", Toast.LENGTH_SHORT).show();
                                averageScore.setText("Average score : " + lecNote.getScore());
                                calculateUserAverageScore(lecNote.getOwner());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("test", "vote error : " + e.getMessage());
                        Toast.makeText(getContext(), "Vote Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void calculateUserAverageScore(final String username)
    {
        fbStore.collection("LecNote").whereEqualTo("owner", username).get()
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
                            fbStore.collection("User").document(username)
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
}
