package org.fountainhook.reminders;

/**
 * Created by nicholson on 3/13/2017.
 */

public class Reminder {

    private int mId;
    private String mContent;
    private int mImportant;

    public Reminder(int id, String content, int important)
    {
        this.mId = id;
        this.mImportant = important;
        this.mContent = content;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public int getImportant() {
        return mImportant;
    }

    public void setImportant(int mImportant) {
        this.mImportant = mImportant;
    }
}
