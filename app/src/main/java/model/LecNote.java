package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LecNote implements Serializable{
    private String title;
    private String description;
    private ArrayList<String> filesName = new ArrayList<>();
    private String owner;
    private String uploadTimeStamp;
    private String subject;
    private double score = 0;
    private HashMap<String, Integer> vote = new HashMap<>();

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
        filesName.remove(name);// not sure if this work or not. if not just loop in arraylist to find specific name
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        uploadTimeStamp = dateFormat.format(date);
    }

    public String getUploadTimeStamp()
    {
        return this.uploadTimeStamp;
    }

    public String UploadTimeReverse()
    {
        String temp = uploadTimeStamp.substring(8, 10);
        temp += uploadTimeStamp.substring(4, 8);
        temp += uploadTimeStamp.substring(0, 4);
        temp += uploadTimeStamp.substring(10, 19);
        return temp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getScore() {
        calculateAverageScore();
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addVote(String username, int score)
    {
        vote.put(username, score);
        calculateAverageScore();
    }

    private void calculateAverageScore()
    {
        int totalScore = 0;
        for (String key : vote.keySet())
        {
            totalScore += vote.get(key);
        }
        if (vote.size() > 0)
        {
            score = totalScore / vote.size();
        }
        else
        {
            score = 0;
        }
    }


    public HashMap<String, Integer> getVote() {
        return vote;
    }

    public void setVote(HashMap<String, Integer> vote) {
        this.vote = vote;
        calculateAverageScore();
    }
}
