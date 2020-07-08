package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pucp.lab4egb.entities.Comment;

import java.util.Calendar;

public class ViewDetailActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    private ArrayList<Comment> comments = new ArrayList<>();;
    private RecyclerView recyclerViewComments; // RecyclerView
    private ViewDetailAdapter viewDetailAdapter; // Adapter
    String id = "waa";
    String nombre="";
    String cant="";
    FirebaseAuth mAuth;
    private MenuItem item;

    String publicationIdSelected;
    String publicationDescriptionSelected;

    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            cant = extras.getString("cant");
            publicationDescriptionSelected = extras.getString("publicationDescriptionExtra");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        nombre = user.getDisplayName();

        TextView pubDescriptionDetailsTextView = findViewById(R.id.pubDescriptionDetailsTextView);
        pubDescriptionDetailsTextView.setText(publicationDescriptionSelected);

        databaseReference = FirebaseDatabase.getInstance().getReference(); // Variable con conexión a rama raíz (lab4grupo1/)



        int flag = Integer.valueOf(cant);
        if(flag != 0) {
            publicationValueEventListener(id); // Obtener lista completa de comments
            buildPublicationRecyclerView();
        }


    }

    public void publicationValueEventListener(String id_w){
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

        //Log.d("size2",Integer.toString(publications.size())); // imprimir desde un List
        /*
        viewDetailAdapter.setOnItemClickListener(new ListPublicationsAdapter.OnItemClickListener() {
            @Override
            public void onViewMoreClick(int position) {
                Publication pubSelected = publications.get(position);
                String pubSelectedViewMore = pubSelected.getPublicationId();
            }
        });*/

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
        startActivityForResult(intent2,LAUNCH_CREATE_COMMENT_ACTIVITY);
    }
}