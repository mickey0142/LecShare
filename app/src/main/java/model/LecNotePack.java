package model;

import java.io.Serializable;
import java.util.ArrayList;

public class LecNotePack implements Serializable {
    private ArrayList<LecNote> lecNoteList;

    public LecNotePack()
    {

    }

    public LecNotePack(ArrayList<LecNote> lecNoteList)
    {
        this.lecNoteList = lecNoteList;
    }

    public ArrayList<LecNote> getLecNoteList() {
        return lecNoteList;
    }

    public void setLecNoteList(ArrayList<LecNote> lecNoteList) {
        this.lecNoteList = lecNoteList;
    }
}
