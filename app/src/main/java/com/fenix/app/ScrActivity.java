package com.fenix.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ScrActivity extends AppCompatActivity {
    private Button remuv;
    private Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    private final View.OnClickListener remove_Listener = v -> {
        this.finish();
        //       Intent intent = new Intent(MapsActivity.this, ScrActivity.class);
        //      startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr);
        remuv = (Button) findViewById(R.id.button_close);
        remuv.setOnClickListener(remove_Listener);

        btnSignIn = findViewById(R.id.butSignIn);
        btnRegister = findViewById(R.id.butRegister);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        

    }
}