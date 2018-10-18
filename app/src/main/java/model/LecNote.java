package model;

import android.util.Log;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LecNote implements Serializable{
    private String title;
    private String description;
    private ArrayList<String> filesName = new ArrayList<>();
    private String owner;
    private String uploadTimeStamp;
    //double point;

    public LecNote()
    {

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getFilesName() {
        return filesName;
    }

    public void setFilesName(ArrayList<String> filesName) {
        this.filesName = filesName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addFileName(String name)
    {
        filesName.add(name);
    }

    public void removeFileName(int index)
    {
        filesName.remove(index);
    }

    public void removeFileName(String name)
    {
        filesName.remove(name);// not sure if this work or not. if not just loop in arraylist to find specific url
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUploadTimeStamp()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        uploadTimeStamp = dateFormat.format(date);
    }

    public String getUploadTimeStamp()
    {
        return this.uploadTimeStamp;
    }
}
