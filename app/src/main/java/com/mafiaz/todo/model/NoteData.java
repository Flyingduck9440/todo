package com.mafiaz.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NoteData_Table")
public class NoteData implements Parcelable {
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

    protected NoteData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        body = in.readString();
        timestamps = in.readLong();
        category = in.readString();
    }

    public static final Creator<NoteData> CREATOR = new Creator<NoteData>() {
        @Override
        public NoteData createFromParcel(Parcel in) {
            return new NoteData(in);
        }

        @Override
        public NoteData[] newArray(int size) {
            return new NoteData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeLong(timestamps);
        parcel.writeString(category);
    }
}
