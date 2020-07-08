package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pucp.lab4egb.entities.Comment;
import com.pucp.lab4egb.entities.Publication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class CreatePublicationActivity extends AppCompatActivity {

    //Se define lo necesario para abrir la galeria y mostrar la foto en pantalla antes de subirla.
    ImageView imageView;
    Button button;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    // HASTA AQUI


    DatabaseReference databaseReference;
    Calendar calendar; // contendrá la hora y fecha obtenida de Firebase Functions
    String username;
    private MenuItem item;
    Intent intent;
    FirebaseAuth mAuth;
    String nombre;

    //Declaramos lo necesario para Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_publication);


        // GENERAMOS EL CLICK EL CUAL LLAMARA A LA FUNCION openGallery();
        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.buttonLoadPicture);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });



        Bundle extras  = getIntent().getExtras();
        if (extras != null){
            username = extras.getString("loggedusername");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(); // Variable con conexión a rama raíz (lab4grupo1/)
        // GetDateTimeFromFirebaseFunctions(); // Obtener Hora y fecha de Firebase Functions
    }

    //FUNCION PARA GENERAR EL INTENT Y ABRIR LA GALERIA
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }

    // Crear nueva publicación
    public void createPublication(View view){
        GetDateTimeFromFirebaseFunctions(); // Obtener Hora y fecha de Firebase Functions
        Publication publication = new Publication(); // Nueva publicación


        //Configuramos los parámtetros necesarios para Storage, se asume una foto por publicacion
        //StorageReference storageRef = storage.getReference();


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

        // Usaremos putBytes para subir nuestra imagen.
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmapImage =((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream  baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] data = baos.toByteArray();


        StorageReference publicationsRef = storageRef.child("publications/"+publicationId);

        UploadTask uploadTask = publicationsRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("infoApp","subido exitoso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("infoApp","subido erróneoooooooooooooooooooooo");
            }
        });







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

    // Inflar appbar_info  // ***********************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_info,menu);
        item = menu.findItem(R.id.name);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            nombre = user.getDisplayName();

        } else {
            Toast.makeText(this, "tostada", Toast.LENGTH_SHORT).show();
        }

        item.setTitle(nombre);

        return true;
    }






}