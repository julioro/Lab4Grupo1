package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        String userid = "userId1"; // Se deberá cambiar por el Id pasado por Auth (id del usuario logueado)

        // Configuración de parámetros de la Incidencia
        publication.setDescription(((EditText) findViewById(R.id.editTextPublicationDescription)).getText().toString());
        publication.setImage("image1"); // Deberá obtenerse de FB Storage
        publication.setDate("27/05/2020");
        publication.setComments("Comentarios");

        // Guardar publicación en DB
        DatabaseReference path = databaseReference.child(userid).push(); // configurar la ruta para imprimir en DB
        String publicationId = path.getKey(); // obtenemos el instanceId del push
        publication.setPublicationId(publicationId);
        path.setValue(publication)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // si se guardó exitosamente
                        Log.d("publicationSaveSuccess","Guardado de publication exitoso");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // si hubo error al guardar
                        Log.e("publicationSaveFail","Guardado de publication fallido",e.getCause());
                    }
                });
    }
}