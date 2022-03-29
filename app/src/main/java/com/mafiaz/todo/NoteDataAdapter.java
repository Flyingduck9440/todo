package com.mafiaz.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mafiaz.todo.model.NoteData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.myViewHolder> {

    private Context context;
    private List<NoteData> data;

    private onCardClickListener listener;

    public NoteDataAdapter(Context context, onCardClickListener listener){
        this.context = context;
        this.listener = listener;

    }

    public void setData(List<NoteData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public NoteDataAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list,parent, false);
        return new myViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteDataAdapter.myViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());

        Date unix = new Date(data.get(position).getTimestamps());
        String converted_date = new SimpleDateFormat("EEE, d MMM yyyy   -   HH:mm aaa").format(unix);
        holder.date.setText(converted_date);

        holder.body.setText(data.get(position).getBody());
        holder.category.setText(data.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        TextView date;
        TextView body;
        TextView category;
        CardView cardView;

        onCardClickListener listener;

        public myViewHolder(@NonNull View itemView, onCardClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            date = itemView.findViewById(R.id.card_date_time);
            body = itemView.findViewById(R.id.card_body);
            category = itemView.findViewById(R.id.card_category);

            cardView = itemView.findViewById(R.id.card_container);

            this.listener = listener;
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onCardClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onCardLongClicked(getAdapterPosition(), view);
            return true;
        }
    }

    public interface onCardClickListener {
        void onCardClicked(int position);
        void onCardLongClicked(int position, View view);
    }
}
