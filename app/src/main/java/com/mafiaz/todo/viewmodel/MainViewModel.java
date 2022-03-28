package com.mafiaz.todo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafiaz.todo.model.CategoryList;
import com.mafiaz.todo.model.NoteDAO;
import com.mafiaz.todo.model.NoteData;
import com.mafiaz.todo.model.NoteDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<CategoryList>> allCategory;
    private static NoteDAO noteDAO;

    public MainViewModel(@NonNull Application application) {
        super(application);
        noteDAO = NoteDatabase.getInstance(application).noteDAO();
    }

    public List<NoteData> getAllNote(){
        return noteDAO.getAll();
    }

    public List<NoteData> getAllNoteByCategory(String name){
        return noteDAO.getAllByCategory(name);
    }

    public void addNote(NoteData data){
        noteDAO.addNote(data);
    }

    public void addCategory(String category_name){
        CategoryList categoryList = new CategoryList(0,category_name);
        noteDAO.addCategory(categoryList);
    }

    public void updateCategoryName(String old_name,String new_name){
        noteDAO.updateCategoryName(old_name, new_name);
        noteDAO.updateNoteCategoryName(old_name, new_name);
    }

    public void deleteCategory(String category_name){
        noteDAO.deleteNoteByCategory(category_name);
        noteDAO.deleteCategoryName(category_name);
    }

    public void clearNotesByCategory(String category_name){
        noteDAO.deleteNoteByCategory(category_name);
    }

    public List<CategoryList> getAllCategory(){
        return noteDAO.getCategoryList();
    }
}
