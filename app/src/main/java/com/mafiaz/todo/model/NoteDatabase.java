package com.mafiaz.todo.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NoteData.class, CategoryList.class}, version = 3,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    public abstract NoteDAO noteDAO();

    private static NoteDatabase INSTANCE;

    private static String DATABASE_NAME = "NoteData";

    public synchronized static NoteDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
