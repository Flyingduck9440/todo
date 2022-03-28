package com.mafiaz.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mafiaz.todo.databinding.ActivityMainBinding;
import com.mafiaz.todo.model.CategoryList;
import com.mafiaz.todo.model.NoteData;
import com.mafiaz.todo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NoteDataAdapter.onCardClickListener{

    private final static String EMBEDDED_CATEGORY = "All";

    private ActivityMainBinding _binding;

    private ArrayList<String> categoryArrayList = new ArrayList<>();
    private List<NoteData> receiveData = new ArrayList<>();

    private MainViewModel viewModel;
    private String currentChipName = EMBEDDED_CATEGORY;

    private NoteDataAdapter adapter;

    private Chip chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _binding.chipAdd.setOnClickListener(this);
        _binding.btnNew.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        loadCategoryFromDatabase();

        if(categoryArrayList.size() > 1){
            for(String name : categoryArrayList){
                if(!name.equals(EMBEDDED_CATEGORY)){
                    addChip(View.generateViewId(), name);
                }
            }
        }

        adapter = new NoteDataAdapter(this, this);
        _binding.recyclerView.setAdapter(adapter);

        _binding.chipItemAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChipName = _binding.chipItemAll.getText().toString();
                getDataByCurrentCategory();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chip_add:
                showAddDialog();
                break;
            case R.id.btn_new:
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("chipName", currentChipName);
                if(categoryArrayList.size() > 0){
                    intent.putExtra("category_name",categoryArrayList);
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCardClicked(int note_id) {
        Log.e("Card ID", ""+note_id);
    }

    private void chipListener(Chip chip){

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChipName = categoryArrayList.get(view.getId());

                getDataByCurrentCategory();
            }
        });
        chip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                _binding.chipGroup.check(view.getId());
                currentChipName = categoryArrayList.get(view.getId());
                getDataByCurrentCategory();
                showPopup(view);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(categoryArrayList.size() == 0){
            loadCategoryFromDatabase();
        }
        getDataByCurrentCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _binding = null;
    }

    private void addChip(int id, String text){
        chip = (Chip) getLayoutInflater().inflate(R.layout.chip_choice_item,_binding.getRoot(),false);
        chip.setId(id);
        chip.setText(text);

        _binding.chipGroup.addView(chip);

        chipListener(chip);
    }

    @SuppressLint("RestrictedApi")
    private void showPopup(View view){
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.chips_setting, menuBuilder);

        MenuPopupHelper popupHelper = new MenuPopupHelper(this, menuBuilder, view);
        popupHelper.setForceShowIcon(true);

        popupHelper.show();

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.rename:
                        showRenameDialog(currentChipName);
                        return true;
                    case R.id.delete:
                        showDeleteDialog(view, currentChipName);
                        return true;
                    case R.id.clear:
                        showClearDialog();
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {
            }
        });
    }

    private void showAddDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.edit_dialog_layout,null);
        EditText editText = dialogLayout.findViewById(R.id.edt_rename);

        alert.setTitle("New Category");
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(categoryArrayList.contains(editText.getText().toString())){
                    Toast.makeText(getApplication(), "Already created this category.", Toast.LENGTH_SHORT).show();
                }else {
                    addChip(View.generateViewId(), editText.getText().toString());
                    viewModel.addCategory(editText.getText().toString());
                    categoryArrayList.add(editText.getText().toString());
                }
            }
        });

        alert.setView(editText);
        alert.show();

    }

    private void showRenameDialog(String old_name){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.edit_dialog_layout,null);
        EditText editText = dialogLayout.findViewById(R.id.edt_rename);

        alert.setTitle("Rename to");
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!categoryArrayList.contains(editText.getText().toString())) {

                    viewModel.updateCategoryName(old_name, editText.getText().toString());
                    categoryArrayList.set(categoryArrayList.indexOf(old_name), editText.getText().toString());
                    _binding.chipGroup.removeViews(1, categoryArrayList.size());

                    for(String name: categoryArrayList){
                        addChip(View.generateViewId(), name);
                    }

                    getDataByCurrentCategory();
                }
            }
        });

        alert.setView(editText);
        alert.show();
    }

    private void showDeleteDialog(View view, String chipName){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete");
        alert.setMessage("All data in this category will be deleted, Confirm?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                _binding.chipGroup.removeView(view);
                viewModel.deleteCategory(chipName);
                categoryArrayList.remove(chipName);

                _binding.chipItemAll.setChecked(true);
                currentChipName = EMBEDDED_CATEGORY;

                getDataByCurrentCategory();

            }
        });

        alert.show();
    }

    private void showClearDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Clear Data");
        alert.setMessage("All notes in this category will be deleted, Confirm?");
        alert.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { ;
                viewModel.clearNotesByCategory(currentChipName);
                getDataByCurrentCategory();

            }
        });

        alert.show();
    }

    private void loadCategoryFromDatabase(){
        categoryArrayList.clear();
        categoryArrayList.add(EMBEDDED_CATEGORY);
        for(CategoryList name: viewModel.getAllCategory()){
            categoryArrayList.add(name.getCategory_name());
        }
    }

    private void getDataByCurrentCategory(){
        receiveData.clear();
        if(currentChipName.equals(EMBEDDED_CATEGORY)){
            receiveData = viewModel.getAllNote();
        }else{
            receiveData = viewModel.getAllNoteByCategory(currentChipName);
        }
        adapter.setData(receiveData);
        adapter.notifyDataSetChanged();

    }

}