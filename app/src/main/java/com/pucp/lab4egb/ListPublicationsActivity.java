package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pucp.lab4egb.entities.Publication;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListPublicationsActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    private ArrayList<Publication> publications = new ArrayList<>();;

    private RecyclerView recyclerViewPublications; // RecyclerView
    private ListPublicationsAdapter listPublicationsAdapter; // Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_publications);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        publicationValueEventListener(); // Obtener lista completa de publicaciones por usuario

        //publicationChildEventListener(); // Obtener solo publicaciones modificadas/creadas por usuario

        buildPublicationRecyclerView();
    }

    public void publicationValueEventListener(){
        databaseReference.child("userId1").addValueEventListener(new ValueEventListener() { // CAMBIAR POR UN userId VARIABLE
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // cada vez que hay un cambio en Firebase
                Log.d("dataSnapshotJson",dataSnapshot.getValue().toString()); // dataSnapshot contiene el json (equivalente a gson.fromJson)

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
            }
        });

    }

}