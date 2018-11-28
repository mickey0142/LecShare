package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Comment implements Serializable {
    private String comment;
    private String lecNoteID;
    private String userName;
    private String userNameID;
    private String documentID;
    private String commentTimeStamp;

    public Comment() {

    }

    public Comment(String userName) {
        this.setUserName(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentTimeStamp(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        commentTimeStamp = dateFormat.format(date);
    }

    public String getCommentTimeStamp(){
        return this.commentTimeStamp;
    }

    public String commentTimeReverse(){
        String temp = commentTimeStamp.substring(8, 10);
        temp += commentTimeStamp.substring(4, 8);
        temp += commentTimeStamp.substring(0, 4);
        temp += commentTimeStamp.substring(10, 19);
        return temp;
    }

    public String getUserNameID() {
        return userNameID;
    }

    public void setUserNameID(String userNameID) {
        this.userNameID = userNameID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getLecNoteID() {
        return lecNoteID;
    }

    public void setLecNoteID(String lecNoteID) {
        this.lecNoteID = lecNoteID;
    }
}
