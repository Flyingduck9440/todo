package com.mafiaz.todo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NoteData_Table")
public class NoteData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private long timestamps;
    private String category;

    public NoteData(int id, String title, String body, long timestamps, String category) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamps = timestamps;
        this.category = category;
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public String getCategory() {
        return category;
    }

}
