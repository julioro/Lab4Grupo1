package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pucp.lab4egb.entities.Publication;

import java.util.ArrayList;
import java.util.List;

public class ListPublications extends AppCompatActivity {

    DatabaseReference databaseReference;
    List<Publication> publications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_publications);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        publicationValueEventListener(); // Obtener lista completa de incidencias por usuario
        publicationChildEventListener(); // Obtener solo incidencias modificadas/creadas por usuario
    }

    public void publicationValueEventListener(){
        databaseReference.child("userId1").addValueEventListener(new ValueEventListener() { // CAMBIAR POR UN userId VARIABLE
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // cada vez que hay un cambio en Firebase
                // dataSnapshot contiene el json (equivalente a gson.fromJson)
                Log.d("dataSnapshotJson",dataSnapshot.getValue().toString());

                // Iterar por todas las incidencias del JSON
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Publication publication = postSnapshot.getValue(Publication.class);
                    Log.d("pubDescriptionFromSnap",publication.getDescription()); // imprimir desde el snapshot directamente
                    Log.d("pubKeys",postSnapshot.getKey()); // imprimir llaves de los elementos

                    publications.add(publication);  // agregar todas las incidencias a un arreglo
                    Log.d("pubDescriptionFromArray",publications.get(publications.indexOf(publication)).getDescription()); // imprimir desde un List
                }
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

}