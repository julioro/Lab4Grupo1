package com.pucp.lab4egb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pucp.lab4egb.entities.Publication;

public class CreatePublicationActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_publication);

        // Variable con conexión a rama raíz (lab4grupo1/)
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Crear nueva publicación
    public void createPublication(View view){
        Publication publication = new Publication();

        // Se deberá cambiar por el Id pasado por Auth (id del usuario logueado)
        String userid = "abcde01";

        // Configuración de parámetros de la Incidencia

    }
}