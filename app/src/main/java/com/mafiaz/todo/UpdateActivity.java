package com.mafiaz.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.mafiaz.todo.databinding.ActivityUpdateBinding;
import com.mafiaz.todo.model.NoteData;
import com.mafiaz.todo.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding _binding;
    private NoteData receiveData;

    private MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            receiveData = bundle.getParcelable("note_data");
        }
        setFields();

        _binding.imgCheck.setOnClickListener(view -> {
            String original_title = receiveData.getTitle();
            String original_body = receiveData.getBody();
            String input_title = _binding.txtTitle.getText().toString();
            String input_body = _binding.txtBody.getText().toString();

            if(!original_title.equals(input_title) ||
               !original_body.equals(input_body)){
                viewModel.updateNote(prepareData());
            }
            finish();
        });
    }

    private void setFields(){
        _binding.txtTitle.setText(receiveData.getTitle());
        _binding.txtBody.setText(receiveData.getBody());

        Date unix = new Date(receiveData.getTimestamps());
        String converted_date = new SimpleDateFormat("EEE, d MMM yyyy").format(unix);

        _binding.txtDate.setText(converted_date);
    }

    private NoteData prepareData(){
        NoteData data = new NoteData(
                receiveData.getId(),
                _binding.txtTitle.getText().toString(),
                _binding.txtBody.getText().toString(),
                System.currentTimeMillis(),
                receiveData.getCategory()
        );
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _binding = null;
    }
}