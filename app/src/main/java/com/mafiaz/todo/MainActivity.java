package com.mafiaz.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mafiaz.todo.databinding.ActivityMainBinding;
import com.mafiaz.todo.model.CategoryList;
import com.mafiaz.todo.model.NoteData;
import com.mafiaz.todo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NoteDataAdapter.onCardClickListener {

    private final static String EMBEDDED_CATEGORY = "All";

    private ActivityMainBinding _binding;

    private HashMap<Integer, String> categoryHashMap = new HashMap<>();
    private List<NoteData> receiveData = new ArrayList<>();
    private List<NoteData> filter = new ArrayList<>();

    private MainViewModel viewModel;
    private String currentChipName = EMBEDDED_CATEGORY;

    private NoteDataAdapter adapter;

    private Chip chip;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _binding.chipAdd.setOnClickListener(this);
        _binding.btnNew.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        loadCategoryFromDatabase();

        if(categoryHashMap.size() > 1){
            for(Map.Entry<Integer, String> entry : categoryHashMap.entrySet()){
                if(!entry.getValue().equals(EMBEDDED_CATEGORY)){
                    addChip(entry.getKey(), entry.getValue(),false);
                }
            }
        }


        adapter = new NoteDataAdapter(this, this);
        _binding.recyclerView.setAdapter(adapter);

        _binding.chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                currentChipName = categoryHashMap.get(checkedId);
            }
        });

        _binding.chipItemAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataByCurrentCategory();
            }
        });

        registerForContextMenu(_binding.chipItemAll);

        _binding.searchNote.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    hideKeyboard(_binding.searchNote);
                    _binding.searchNote.clearFocus();
                }else{
                    searchText = newText;
                }
                setFilter(newText);
                return true;
            }
        });

        ImageView clear = _binding.searchNote.findViewById(androidx.appcompat.R.id.search_close_btn);
        clear.setOnClickListener(view -> {
            searchText = "";
            _binding.searchNote.setQuery(searchText,true);
            _binding.searchNote.clearFocus();
            hideKeyboard(_binding.searchNote);
        });

    }

    @SuppressLint("NewApi")
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.all_setting,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.clear_all){
            showClearAllDialog();
            return true;
        }
        return false;
    }

    private void setFilter(String newText){
        filter.clear();
        for(NoteData note : receiveData){
            if(note.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
            note.getBody().toLowerCase().contains(newText.toLowerCase())){
                filter.add(note);
            }
        }
        adapter.setData(filter);
        adapter.notifyDataSetChanged();

        _binding.txtNoData.setVisibility(View.GONE);
        if(filter.size() == 0){
            _binding.txtNoData.setVisibility(View.VISIBLE);
        }
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
                if(categoryHashMap.size() > 0){

                    intent.putExtra("category_name", new ArrayList<>(categoryHashMap.values()));
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCardClicked(int position) {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("note_data", filter.get(position));
        startActivity(intent);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void onCardLongClicked(int position, View view) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.card_setting, menuBuilder);

        MenuPopupHelper popupHelper = new MenuPopupHelper(this, menuBuilder, view);
        popupHelper.setForceShowIcon(true);

        popupHelper.show();
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                showDeleteNoteDialog(filter.get(position).getId());
                return true;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {
            }
        });
    }

    private void chipListener(Chip chip){

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataByCurrentCategory();
            }
        });
        chip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                _binding.chipGroup.check(view.getId());
                getDataByCurrentCategory();
                showPopup(view);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(categoryHashMap.size() == 0){
            loadCategoryFromDatabase();
        }
        getDataByCurrentCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _binding = null;
    }

    private void addChip(int id, String text,boolean check){
        chip = (Chip) getLayoutInflater().inflate(R.layout.chip_choice_item,_binding.getRoot(),false);
        chip.setId(id);
        chip.setText(text);
        chip.setChecked(check);
        _binding.chipGroup.addView(chip);

        chipListener(chip);

        categoryHashMap.put(id, text);
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
                        showRenameDialog(view, currentChipName);
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
                if(categoryHashMap.containsValue(editText.getText().toString())){
                    Toast.makeText(getApplication(), "Already created this category.", Toast.LENGTH_SHORT).show();
                }else {
                    addChip(View.generateViewId(), editText.getText().toString(),false);
                    viewModel.addCategory(editText.getText().toString());
                }
            }
        });

        alert.setView(editText);
        alert.show();

    }

    private void showRenameDialog(View view, String old_name){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.edit_dialog_layout,null);
        EditText editText = dialogLayout.findViewById(R.id.edt_rename);

        alert.setTitle("Rename to");
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!categoryHashMap.containsValue(editText.getText().toString())) {

                    viewModel.updateCategoryName(old_name, editText.getText().toString());
                    categoryHashMap.put(view.getId(), editText.getText().toString());
                    _binding.chipGroup.removeAllViews();

                    boolean check = false;
                    for(Map.Entry<Integer, String> entry : categoryHashMap.entrySet()){
                        check = entry.getValue().equals(editText.getText().toString());
                        addChip(entry.getKey(), entry.getValue(),check);
                    }
                    currentChipName = editText.getText().toString();
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
                categoryHashMap.remove(view.getId());

                _binding.chipItemAll.setChecked(true);
                currentChipName = EMBEDDED_CATEGORY;

                getDataByCurrentCategory();

            }
        });

        alert.show();
    }

    private void showDeleteNoteDialog(int id){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete");
        alert.setMessage("Are you sure want to delete this note?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModel.deleteNote(id);
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

    private void showClearAllDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Clear Data");
        alert.setMessage("All notes in this category will be deleted, Confirm?");
        alert.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { ;
                viewModel.deleteAll();
                getDataByCurrentCategory();
            }
        });

        alert.show();
    }

    private void loadCategoryFromDatabase(){
        categoryHashMap.clear();

        categoryHashMap.put(_binding.chipItemAll.getId(), EMBEDDED_CATEGORY);
        for(CategoryList name: viewModel.getAllCategory()){
            categoryHashMap.put(View.generateViewId(),name.getCategory_name());
        }
    }

    private void getDataByCurrentCategory(){
        receiveData.clear();
        filter.clear();
        if(currentChipName.equals(EMBEDDED_CATEGORY)){
            receiveData = viewModel.getAllNote();
        }else{
            receiveData = viewModel.getAllNoteByCategory(currentChipName);
        }
        adapter.setData(receiveData);
        adapter.notifyDataSetChanged();

        _binding.txtNoData.setVisibility(View.VISIBLE);
        if(receiveData.size() > 0){
            _binding.txtNoData.setVisibility(View.GONE);
        }

        filter.addAll(receiveData);

        if(!searchText.isEmpty()){
            setFilter(searchText);
        }
    }

}