package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pucp.lab4egb.entities.Comment;

import java.util.Calendar;

public class CreateCommentActivity extends AppCompatActivity {
    String id = "waa";
    DatabaseReference databaseReference;
    String publicationIdSelected;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            publicationIdSelected = extras.getString("id");

        }

        databaseReference = FirebaseDatabase.getInstance().getReference();


    }


    public void createComment(View view){
        Comment comment = new Comment();

        EditText editTextNewComment = findViewById(R.id.editTextNewComment);
        comment.setBody(editTextNewComment.getText().toString());

        calendar = Calendar.getInstance();

        comment.setDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR));
        comment.setHour(String.format("%02d", calendar.get(Calendar.HOUR)) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)));

        comment.setUser("loggedusername");

        DatabaseReference path2 = databaseReference.child("comments/" + publicationIdSelected).push();
        path2.setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // si se guardó exitosamente
                        Log.d("commentSaveSuccess","Guardado de comentario exitoso");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // si hubo error al guardar
                        Log.e("comentarioSaveFail","Guardado de comentario fallido",e.getCause());
                    }
                });
    }
}