package com.mafiaz.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mafiaz.todo.databinding.ActivityAddBinding;
import com.mafiaz.todo.model.NoteData;
import com.mafiaz.todo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private List<String> category_list = new ArrayList<>();
    private String chipName;

    private ActivityAddBinding _binding;

    private MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getSerializable("category_name")!= null){
            category_list.addAll((List<String>) bundle.getSerializable("category_name"));

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(this, R.layout.category_dropdown_item, category_list);
            _binding.dropdownCategory.setAdapter(categoryAdapter);
        }

        chipName = bundle.getString("chipName");
        _binding.dropdownCategory.setText(chipName, false);

        _binding.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!_binding.edtTitle.getText().toString().isEmpty()){
                    viewModel.addNote(prepareDate());
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter categoryAdapter = new ArrayAdapter<>(this, R.layout.category_dropdown_item, category_list);
        _binding.dropdownCategory.setAdapter(categoryAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _binding = null;
    }

    private NoteData prepareDate(){
        NoteData data = new NoteData(
                0,
                _binding.edtTitle.getText().toString(),
                _binding.edtBody.getText().toString(),
                System.currentTimeMillis(),
                _binding.dropdownCategory.getText().toString()
        );
        return data;
    }
}