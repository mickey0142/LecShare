package model;

import java.util.ArrayList;

public class LecNote {
    private String title;
    private String description;
    private ArrayList<String> filesURL = new ArrayList<>();
    private String owner;
    //double point;

    public LecNote()
    {

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String descriptions) {
        this.description = description;
    }

    public ArrayList<String> getFilesURL() {
        return filesURL;
    }

    public void setFilesURL(ArrayList<String> filesURL) {
        this.filesURL = filesURL;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addFileURL(String url)
    {
        filesURL.add(url);
    }

    public void removeFileURL(int index)
    {
        filesURL.remove(index);
    }

    public void removeFileURL(String url)
    {
        filesURL.remove(url);// not sure if this work or not. if not just loop in arraylist to find specific url
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
