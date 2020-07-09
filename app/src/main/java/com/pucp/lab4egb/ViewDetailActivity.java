package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pucp.lab4egb.entities.Comment;
import com.pucp.lab4egb.entities.Publication;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pucp.lab4egb.entities.Comment;

import java.util.Calendar;
import java.util.TooManyListenersException;

public class ViewDetailActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Comment> comments2 = new ArrayList<>();
    private RecyclerView recyclerViewComments; // RecyclerView
    private ViewDetailAdapter viewDetailAdapter; // Adapter
    String id = "waa";
    String nombre="";
    String cant="";
    FirebaseAuth mAuth;
    private MenuItem item;

    String publicationIdSelected, publicationDescriptionSelected, publicationDateSelected, publicationUserNameSelected;

    Integer commentsSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            cant = extras.getString("cant");

            publicationDescriptionSelected = extras.getString("publicationDescriptionExtra");
            publicationDateSelected = extras.getString("publicationDateExtra");
            publicationUserNameSelected = extras.getString("publicationUserNameExtra");
        }
        //Log.d("cant",cant);
        Toast.makeText(ViewDetailActivity.this, cant, Toast.LENGTH_LONG).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        nombre = user.getDisplayName();

        TextView pubUserDetailsTextView = findViewById(R.id.pubUserDetailsTextView);
        TextView pubDateDetailsTextView = findViewById(R.id.pubDateDetailsTextView);
        TextView pubDescriptionDetailsTextView = findViewById(R.id.pubDescriptionDetailsTextView);

        publicationValueEventListener(id);
        buildPublicationRecyclerView();

        pubUserDetailsTextView.setText(publicationUserNameSelected);
        pubDateDetailsTextView.setText(publicationDateSelected);

        pubDescriptionDetailsTextView.setText(publicationDescriptionSelected);
    }

    public void publicationValueEventListener(final String id_w){
        databaseReference.child("comments/"+ id_w).addValueEventListener(new ValueEventListener() { // CAMBIAR POR UN userId VARIABLE
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // cada vez que hay un cambio en Firebase
                Log.d("dataSnapshotJson",dataSnapshot.getValue().toString()); // dataSnapshot contiene el json (equivalente a gson.fromJson)

                comments.clear(); // vaciar el ArrayList de publicaciones, para llenar la lista nuevamente
                // Iterar por todas las publicaciones del JSON
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Comment comment = postSnapshot.getValue(Comment.class);
                    //Log.d("pubDescriptionFromSnap",publication.getDescription()); // imprimir desde el snapshot directamente
                    //Log.d("pubKeys",postSnapshot.getKey()); // imprimir llaves de los elementos

                    comments.add(comment);  // agregar todas las publicaciones a un arreglo
                    //Log.d("pubDescriptionFromArray",publications.get(publications.indexOf(publication)).getDescription()); // imprimir desde un ArrayList
                    //Log.d("pubIdFromArray",publications.get(publications.indexOf(publication)).getPublicationId()); // imprimir desde un ArrayList
                }

                commentsSize = comments.size();
                TextView pubCommentsDetailsTextView = findViewById(R.id.pubCommentsDetailsTextView);
                pubCommentsDetailsTextView.setText(commentsSize + " Comentarios");
                // Toast.makeText(ViewDetailActivity.this, "commentsSize: " + commentsSize, Toast.LENGTH_SHORT).show();
                databaseReference.child("publications/" + id_w).child("cant_comments").setValue(commentsSize.toString());

                buildPublicationRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { // si hay un error al obtener la información en Firebase

            }
        });
    }


    // Método para construir el RecyclerView
    public void buildPublicationRecyclerView(){
        viewDetailAdapter = new ViewDetailAdapter(comments, ViewDetailActivity.this);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setAdapter(viewDetailAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(ViewDetailActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_info,menu);
        item = menu.findItem(R.id.name);
        item.setTitle(nombre);

        return true;
    }

    int LAUNCH_CREATE_COMMENT_ACTIVITY = 2;
    public void CrearComentario(View view){
        Intent intent2 = new Intent(ViewDetailActivity.this, CreateCommentActivity.class);
        intent2.putExtra("id",id);
        intent2.putExtra("cant",cant);
        intent2.putExtra("publicationDescriptionExtra",publicationDescriptionSelected);
        startActivityForResult(intent2,LAUNCH_CREATE_COMMENT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CREATE_COMMENT_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){

                //Toast.makeText(this, "onActivityResult resultCode == Activity.RESULT_OK", Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(this, "onActivityResult resultCode == Activity.RESULT_CANCELED", Toast.LENGTH_LONG).show();
            }
        }
    }
}