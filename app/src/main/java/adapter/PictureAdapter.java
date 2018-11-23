package adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.kairanpa.se.lecshare.GlideApp;
import com.kairanpa.se.lecshare.R;

import java.io.File;
import java.util.ArrayList;

public class PictureAdapter extends ArrayAdapter {

    Context context;
    ArrayList<String> fileName;
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();

    public PictureAdapter(Context context, int  resource, ArrayList<String> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.fileName = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View pictureItem = LayoutInflater.from(context).inflate(R.layout.fragment_picture_list_item, parent, false);
        ImageView picture = pictureItem.findViewById(R.id.picture_list_item_picture);
        TextView pictureName = pictureItem.findViewById(R.id.picture_list_item_name);
        final ProgressBar progressBar = pictureItem.findViewById(R.id.picture_list_item_progress_bar);
        final ImageView done = pictureItem.findViewById(R.id.picture_list_item_done);
        final ImageView downloadButton = pictureItem.findViewById(R.id.picture_list_item_download_button);
        if (!fileName.get(position).endsWith(".pdf"))
        {
            StorageReference imageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com").child(fileName.get(position));
            GlideApp.with(getContext()).load(imageRef).into(picture);
        }
        pictureName.setText(fileName.get(position));
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                done.setVisibility(View.INVISIBLE);
                downloadButton.setVisibility(View.INVISIBLE);
                try
                {
                    File directory = new File(Environment.getExternalStorageDirectory(), "LectureNote");
                    if(!directory.exists())
                    {
                        boolean check = directory.mkdir();
                        Log.d("test", "make directory : " + check);
                    }
                    final File file = new File(directory, fileName.get(position));
                    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
                    StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com")
                            .child(fileName.get(position));
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("test", "download success");
                            Log.d("test", "create at : " + file.toString());
                            Toast.makeText(getContext(), "download " + fileName.get(position) + " completed", Toast.LENGTH_SHORT).show();
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
                                progressBar.setVisibility(View.INVISIBLE);
                                done.setVisibility(View.VISIBLE);
                                downloadButton.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    Log.d("test", "error from picture adapter : " + e.getMessage());
                }
            }
        });

        return pictureItem;
    }
}
