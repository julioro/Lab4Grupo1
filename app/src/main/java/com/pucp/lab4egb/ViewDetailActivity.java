package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pucp.lab4egb.entities.Comment;

public class ViewDetailActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String publicationIdSelected;
    String publicationDescriptionSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        Bundle extras  = getIntent().getExtras();
        if (extras != null){
            publicationIdSelected = extras.getString("publicationIdExtra");
            publicationDescriptionSelected = extras.getString("publicationDescriptionExtra");
        }

        TextView pubDescriptionDetailsTextView = findViewById(R.id.pubDescriptionDetailsTextView);
        pubDescriptionDetailsTextView.setText(publicationDescriptionSelected);

        databaseReference = FirebaseDatabase.getInstance().getReference(); // Variable con conexión a rama raíz (lab4grupo1/)
    }

    public void createComment(View view){
        Comment comment = new Comment();

        EditText editTextNewComment = findViewById(R.id.editTextNewComment);
        comment.setBody(editTextNewComment.getText().toString());



        comment.setDate("1/1/20");
        comment.setHour("11:59");
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