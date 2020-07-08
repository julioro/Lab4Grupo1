package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pucp.lab4egb.entities.Comment;
import com.pucp.lab4egb.entities.Publication;

import java.util.Calendar;

public class CreatePublicationActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    Calendar calendar; // contendrá la hora y fecha obtenida de Firebase Functions
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_publication);

        Bundle extras  = getIntent().getExtras();
        if (extras != null){
            username = extras.getString("loggedusername");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(); // Variable con conexión a rama raíz (lab4grupo1/)
        // GetDateTimeFromFirebaseFunctions(); // Obtener Hora y fecha de Firebase Functions
    }

    // Crear nueva publicación
    public void createPublication(View view){
        GetDateTimeFromFirebaseFunctions(); // Obtener Hora y fecha de Firebase Functions
        Publication publication = new Publication(); // Nueva publicación

        // Configuración de parámetros de la publicación
        publication.setUserName(username);
        publication.setDescription(((EditText) findViewById(R.id.editTextPublicationDescription)).getText().toString());

        if (calendar != null) {
            publication.setDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR));
            Log.d("fechaOrigen","Se obutvo fecha de Firebase");
        } else {
            calendar = Calendar.getInstance();
            publication.setDate(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR));
            Log.d("fechaOrigen","Se obutvo fecha de celular");
        }
        publication.setImage("image1"); // Deberá obtenerse de FB Storage
        publication.setCant_comments("0");


        // Guardar publicación en DB
        DatabaseReference path = databaseReference.child("publications").push(); // configurar la ruta para imprimir en DB
        String publicationId = path.getKey(); // obtenemos el instanceId del push
        //creacion de id de publicacion en comentarios
        // DatabaseReference path2 = databaseReference.child("comments").push(); // configurar la ruta para imprimir en DB
        //------------------

        //DatabaseReference path2 = databaseReference.child("comments/" + publicationId);
        //path2.setValue("");
        databaseReference.child("comments/" + publicationId).setValue(true);
        //------------------
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

        // retornar a ListPublicationsActivity
      //  databaseReference.child("comments").child(publicationId).push();

        intentListPublications();
//        databaseReference.child("comments").child(publicationId);

    }

    /*
    public void createComment(View view){
        Comment comment = new Comment();

        comment.setBody("Cuerpo del comentario");
        comment.setDate("1/1/20");
        comment.setHour("11:59");
        comment.setUser("loggedusername");

        DatabaseReference path2 = databaseReference.child("comments/" + "-MBfGg9DyfKqlyFxjAFK").push();
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
     */

    // Obtener Hora y fecha de Firebase Functions
    public void GetDateTimeFromFirebaseFunctions(){
        FirebaseFunctions.getInstance().getHttpsCallable("getTime")
                .call().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                long timestamp = (long) httpsCallableResult.getData();
                Log.d("timestamp",Long.toString(timestamp));

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                // Log.d("fechaDay", Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
                // Log.d("fechaMonth", Integer.toString(calendar.get(Calendar.MONTH)+1));
                // Log.d("fechaYear", Integer.toString(calendar.get(Calendar.YEAR)));
                // Log.d("fecha",calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR));
            }
        });
    }

    // Volver al Activity que abrió esta Activity mediante un Intent
    public void intentListPublications(){
        Intent returnIntent = new Intent();
        //returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}