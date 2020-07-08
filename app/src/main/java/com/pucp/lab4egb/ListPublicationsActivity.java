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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pucp.lab4egb.entities.Publication;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListPublicationsActivity extends AppCompatActivity {



    DatabaseReference databaseReference;
    private ArrayList<Publication> publications = new ArrayList<>();;
    private MenuItem item;
    private RecyclerView recyclerViewPublications; // RecyclerView
    private ListPublicationsAdapter listPublicationsAdapter; // Adapter
    String nombre="";
    String correo="";
    FirebaseAuth mAuth;
    Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_publications);

        //cerrar sesion
        logoutBtn = findViewById(R.id.logout_button);

        //Obteniendo usuario y correo
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        nombre = user.getDisplayName();
        correo = user.getEmail();


        databaseReference = FirebaseDatabase.getInstance().getReference();

        publicationValueEventListener(); // Obtener lista completa de publicaciones

        //publicationChildEventListener(); // Obtener solo publicaciones modificadas/creadas

        buildPublicationRecyclerView();
    }

    public void CerrarSesion(View view){
        SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager();
        if (sessionManager.getActiveSession() != null){
            sessionManager.clearActiveSession();
            mAuth.signOut();
        }

        mAuth.signOut();
        updateUI();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            updateUI();
        }
    }

    private void updateUI() {
        Toast.makeText(ListPublicationsActivity.this, "You're logged out", Toast.LENGTH_LONG);

        Intent mainActivity = new Intent(ListPublicationsActivity.this, LoginActivity.class);
        startActivity(mainActivity);
        finish();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void publicationValueEventListener(){
        databaseReference.child("publications").addValueEventListener(new ValueEventListener() { // CAMBIAR POR UN userId VARIABLE
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // cada vez que hay un cambio en Firebase
                Log.d("dataSnapshotJson",dataSnapshot.getValue().toString()); // dataSnapshot contiene el json (equivalente a gson.fromJson)

                publications.clear(); // vaciar el ArrayList de publicaciones, para llenar la lista nuevamente
                // Iterar por todas las publicaciones del JSON
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Publication publication = postSnapshot.getValue(Publication.class);
                    Log.d("pubDescriptionFromSnap",publication.getDescription()); // imprimir desde el snapshot directamente
                    Log.d("pubKeys",postSnapshot.getKey()); // imprimir llaves de los elementos

                    publications.add(publication);  // agregar todas las publicaciones a un arreglo
                    Log.d("pubDescriptionFromArray",publications.get(publications.indexOf(publication)).getDescription()); // imprimir desde un ArrayList
                    Log.d("pubIdFromArray",publications.get(publications.indexOf(publication)).getPublicationId()); // imprimir desde un ArrayList
                }

                buildPublicationRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { // si hay un error al obtener la información en Firebase

            }
        });
    }

    public void publicationChildEventListener(){
        databaseReference.child("abcde01").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Publication publication = dataSnapshot.getValue(Publication.class);
                Log.d("pubAdded",publication.getDescription());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Publication publication = dataSnapshot.getValue(Publication.class);
                Log.d("pubChanged",publication.getDescription());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método para construir el RecyclerView
    public void buildPublicationRecyclerView(){
        listPublicationsAdapter = new ListPublicationsAdapter(publications, ListPublicationsActivity.this);
        recyclerViewPublications = findViewById(R.id.recyclerViewPublications);
        recyclerViewPublications.setAdapter(listPublicationsAdapter);
        recyclerViewPublications.setLayoutManager(new LinearLayoutManager(ListPublicationsActivity.this));

        Log.d("size2",Integer.toString(publications.size())); // imprimir desde un List

        listPublicationsAdapter.setOnItemClickListener(new ListPublicationsAdapter.OnItemClickListener() {
            @Override
            public void onViewMoreClick(int position) {
                Publication pubSelected = publications.get(position);

                String pubSelectedViewMore = pubSelected.getPublicationId();
                Intent intent2 = new Intent(ListPublicationsActivity.this, ViewDetailActivity.class);
                intent2.putExtra("id",pubSelectedViewMore);
                intent2.putExtra("cant",pubSelected.getCant_comments());
                intent2.putExtra("publicationDescriptionExtra",pubSelected.getDescription());
               // Log.d("valores", pubSelectedViewMore);
                startActivity(intent2);

            }
        });
    }

    // Inflar appbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar,menu);
        item = menu.findItem(R.id.name);
        item.setTitle(nombre);

        return true;
    }

    // Al hacer clic en el botón '+' de appbar abrir CreatePublicationActivity
    int LAUNCH_SECOND_ACTIVITY = 1;
    public void actionAddPubAppBar(MenuItem item){
        // Intent i = new Intent(this,CreatePublicationActivity.class);
        // startActivity(i);
        Intent i = new Intent(this, CreatePublicationActivity.class);
        i.putExtra("loggedusername",nombre); // extra el del nombre del usuario logueado

        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }

    // Al regresar del Activity CreatePublicationActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                publicationValueEventListener();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this, "onActivityResult RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}