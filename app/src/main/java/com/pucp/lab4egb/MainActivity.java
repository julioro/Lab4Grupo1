package com.pucp.lab4egb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Button logoutBtn;

    String nombre="";
    String correo="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        FirebaseUser user = mAuth.getCurrentUser();

        TextView textView = findViewById(R.id.idNombre);
        nombre = user.getDisplayName();
        textView.setText(nombre);
        TextView textView1 = findViewById(R.id.textView2);
        correo = user.getEmail();
        textView1.setText(correo);
        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Logout Btn
        logoutBtn = findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager();
                if (sessionManager.getActiveSession() != null){
                    sessionManager.clearActiveSession();
                    mAuth.signOut();
                }

                mAuth.signOut();
                updateUI();
            }
        });


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
        Toast.makeText(MainActivity.this, "You're logged out", Toast.LENGTH_LONG);

        Intent mainActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(mainActivity);
        finish();
    }
}