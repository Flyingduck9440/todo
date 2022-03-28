package com.mafiaz.todo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Category_Table")
public class CategoryList {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String category_name;

    public CategoryList(int id,String category_name) {
        this.id = id;
        this.category_name = category_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public int getId(){
        return id;
    }
}
