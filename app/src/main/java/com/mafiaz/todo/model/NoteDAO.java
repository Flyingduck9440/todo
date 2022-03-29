package com.mafiaz.todo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM NoteData_Table ORDER BY timestamps DESC")
    List<NoteData> getAll();

    @Query("SELECT * FROM NoteData_Table WHERE category IN (:category) ORDER BY timestamps DESC")
    List<NoteData> getAllByCategory(String category);

    @Query("SELECT * FROM Category_Table")
    List<CategoryList> getCategoryList();

    @Query("DELETE FROM NoteData_Table WHERE id = :id")
    void deleteNote(int id);

    @Query("DELETE FROM NoteData_Table WHERE category = :category")
    void deleteNoteByCategory(String category);

    @Query("DELETE FROM Category_Table WHERE category_name = :category")
    void deleteCategoryName(String category);

    @Query("UPDATE Category_Table SET category_name = :new_name WHERE category_name = :old_name")
    void updateCategoryName(String old_name, String new_name);

    @Query("UPDATE NoteData_Table SET category = :new_name WHERE category = :old_name")
    void updateNoteCategoryName(String old_name, String new_name);

    @Update
    void updateNote(NoteData data);

    @Query("DELETE FROM NoteData_Table")
    void deleteAll();

    @Insert
    void addNote(NoteData data);

    @Insert
    void addCategory(CategoryList category_name);
}
