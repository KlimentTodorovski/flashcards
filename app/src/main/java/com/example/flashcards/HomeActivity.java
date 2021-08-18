package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    EditText mQuestionEt, mAnswerEt;
    Button mSaveButton, mListButton;
    ProgressDialog progressDialog;
    ActionBar actionBar;
    String pId, pQuestion, pAnswer;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Data");

        mQuestionEt = findViewById(R.id.questionEt);
        mAnswerEt = findViewById(R.id.answerEt);

        progressDialog = new ProgressDialog(this);

        mSaveButton = findViewById(R.id.saveBtn);
        mListButton = findViewById(R.id.listBtn);

        Bundle bundle = getIntent().getExtras();

        db = FirebaseFirestore.getInstance();

        if (bundle.getString("pId") != null) {
            actionBar.setTitle("Update");
            mSaveButton.setText("Update");
            pId = bundle.getString("pId");
            pQuestion = bundle.getString("pQuestion");
            pAnswer = bundle.getString("pAnswer");

            mQuestionEt.setText(pQuestion);
            mAnswerEt.setText(pAnswer);
        } else {
            actionBar.setTitle("Add Data");
            mSaveButton.setText("Save");
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();

                String question = mQuestionEt.getText().toString().trim();
                String answer = mAnswerEt.getText().toString().trim();

                if (bundle.getString("pId") != null) {
                    String id = pId;
                    updateData(id, question, answer);
                } else {
                    uploadData(question, answer);
                }
            }
        });

        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ListCardsActivity.class);
                intent.putExtra("email", getIntent().getExtras().getString("email"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateData(String id, String question, String answer) {
        progressDialog.setTitle("Updating data...");
        progressDialog.show();

        String activeUserMail = getIntent().getExtras().getString("email");

        db.collection(activeUserMail)
                .document(id)
                .update("question", question, "search", question.toLowerCase(), "answer", answer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadData(String question, String answer) {
        progressDialog.setTitle("Adding Data");
        progressDialog.show();

        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("question", question);
        doc.put("search", question.toLowerCase());
        doc.put("answer", answer);

        String activeUserMail = getIntent().getExtras().getString("email");

        db.collection(activeUserMail).document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Intent intent = new Intent(HomeActivity.this, ListCardsActivity.class);
        intent.putExtra("email", getIntent().getExtras().getString("email"));
        startActivity(intent);
    }
}