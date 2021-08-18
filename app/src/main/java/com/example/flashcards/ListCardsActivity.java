package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.flashcards.adapter.CustomAdapter;
import com.example.flashcards.model.Card;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListCardsActivity extends AppCompatActivity {

    List<Card> cardList;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    CustomAdapter adapter;
    ProgressDialog progressDialog;
    FloatingActionButton mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cards);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("FlashCards");

        db = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recycler_view);
        mAddBtn = findViewById(R.id.addBtn);

        cardList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(this);

        showData();

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCardsActivity.this, HomeActivity.class);
                intent.putExtra("email", getIntent().getExtras().getString("email"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void showData() {
        progressDialog.setTitle("Loading data...");
        progressDialog.show();

        String activeUserMail = getIntent().getExtras().getString("email");

        db.collection(activeUserMail)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    progressDialog.dismiss();
                    for (DocumentSnapshot doc: task.getResult()) {
                        Card card = new Card(doc.getString("id"),
                                doc.getString("question"),
                                doc.getString("answer"));
                        cardList.add(card);
                    }

                    adapter = new CustomAdapter(ListCardsActivity.this, cardList, activeUserMail);
                    mRecyclerView.setAdapter(adapter);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ListCardsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void  deleteData(int index) {
        progressDialog.setTitle("Loading data...");
        progressDialog.show();

        String activeUserMail = getIntent().getExtras().getString("email");

        db.collection(activeUserMail)
          .document(cardList.get(index).getId())
          .delete()
          .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  progressDialog.dismiss();
                  cardList.clear();
                  Toast.makeText(ListCardsActivity.this, "Deleted...", Toast.LENGTH_SHORT).show();
                  showData();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Toast.makeText(ListCardsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchData(String s) {
        progressDialog.setTitle("Searching...");
        progressDialog.show();

        String activeUserMail = getIntent().getExtras().getString("email");
        if (!s.isEmpty()) {
            db.collection(activeUserMail)
              .whereGreaterThanOrEqualTo("search", s.toLowerCase())
              .whereLessThanOrEqualTo("search", s.toLowerCase() + "~")
              .get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      cardList.clear();
                      progressDialog.dismiss();
                      for (DocumentSnapshot doc: task.getResult()) {
                          Card card = new Card(doc.getString("id"),
                                  doc.getString("question"),
                                  doc.getString("answer"));
                          cardList.add(card);
                      }

                      adapter = new CustomAdapter(ListCardsActivity.this, cardList, activeUserMail);
                      mRecyclerView.setAdapter(adapter);
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      progressDialog.dismiss();
                      Toast.makeText(ListCardsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              });
        } else {
            progressDialog.dismiss();
            adapter = new CustomAdapter(ListCardsActivity.this, cardList, activeUserMail);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}