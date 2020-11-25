package com.fenix.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pusher.client.channel.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import Models.User2;


public class ScrActivity extends AppCompatActivity {
    private Button remuv;
    private Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    RelativeLayout root;
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
        root = findViewById(R.id.root_element);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        btnRegister.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
sowRegisterWindow();

            }
        });


    }

    private void sowRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите все данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.registor_window, null);
        dialog.setView(register_window);

       final MaterialEditText email = register_window.findViewById(R.id.emailFild);
       final MaterialEditText pass = register_window.findViewById(R.id.passlFild);
       final MaterialEditText name = register_window.findViewById(R.id.namelFild);
       final MaterialEditText phone = register_window.findViewById(R.id.phonelFild);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog.Builder builder = dialog.setPositiveButton("Добавить",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (TextUtils.isEmpty(email.getText().toString())) {
                            Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(name.getText().toString())) {
                            Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(phone.getText().toString())) {
                            Snackbar.make(root, "Введите ваш телефон", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (pass.getText().toString().length() < 5) {
                            Snackbar.make(root, "Введите пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        // Регистрация пользователя
                        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        User2 user = new User2();
                                        user.setEmail(email.getText().toString());
                                        user.setName(name.getText().toString());
                                        user.setPass(pass.getText().toString());
                                        user.setPhone(phone.getText().toString());

                                        users.child(user.getEmail())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }
                });
        dialog.show();
    }
}