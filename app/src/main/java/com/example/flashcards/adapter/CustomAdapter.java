package com.example.flashcards.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.HomeActivity;
import com.example.flashcards.ListCardsActivity;
import com.example.flashcards.R;
import com.example.flashcards.model.Card;
import com.example.flashcards.view.ViewHolder;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    ListCardsActivity listActivity;
    List<Card> cardList;
    String activeUserEmail;

    public CustomAdapter(ListCardsActivity listActivity, List<Card> cardList, String email) {
        this.listActivity = listActivity;
        this.cardList = cardList;
        this.activeUserEmail = email;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String question = cardList.get(position).getQuestion();
                String answer = cardList.get(position).getAnswer();
                String whatToShow = cardList.get(position).getWhatToShow();
                Card card = cardList.get(position);

                if (whatToShow.equals(question)) {
                    card.setWhatToShow(answer);
                } else {
                    card.setWhatToShow(question);
                }

                notifyItemChanged(position);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                String [] options = {"Update", "Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            String id = cardList.get(position).getId();
                            String question = cardList.get(position).getQuestion();
                            String answer = cardList.get(position).getAnswer();

                            Intent intent = new Intent(listActivity, HomeActivity.class);
                            intent.putExtra("pId", id);
                            intent.putExtra("pQuestion", question);
                            intent.putExtra("pAnswer", answer);
                            intent.putExtra("email", activeUserEmail);
                            listActivity.startActivity(intent);
                        }
                        if (which == 1) {
                            listActivity.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mWhatToSHow.setText(cardList.get(position).getWhatToShow());
//        holder.mAnswer.setText(cardList.get(position).getAnswer());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
