package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import Models.User2;
import lombok.var;


public class ScrActivity extends AppCompatActivity {
    private Button btnRegister;
    private Button btnSignIn;
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
        Button remus = (Button) findViewById(R.id.button_close);
        remus.setOnClickListener(remove_Listener);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.butRegister);
        root = findViewById(R.id.root_element);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        btnRegister.setOnClickListener(view -> sowRegisterWindow());
        btnSignIn.setOnClickListener(view -> {
            showSignWindow();
        });


    }

    private void showSignWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Войти");
        dialog.setMessage("Введите данные для входа");

        LayoutInflater inflater = LayoutInflater.from(this);
        View sing_in_window = inflater.inflate(R.layout.sing_in_window, null);
        dialog.setView(sing_in_window);

        final MaterialEditText email = sing_in_window.findViewById(R.id.emailFild);
        final MaterialEditText pass = sing_in_window.findViewById(R.id.passlFild);


        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog.Builder builder = dialog.setPositiveButton("Войти",
                (dialogInterface, which) -> {
                    if (TextUtils.isEmpty(email.getText().toString())) {
                        Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (pass.getText().toString().length() < 5) {
                        Snackbar.make(root, "Введите пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnSuccessListener(authResult -> {
//                                   startActivity(new Intent(ScrActivity.this, MapsActivity.this));
                                finish();
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           Snackbar.make(root, "Ошибка авторизации. "+ e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    });

                });
        dialog.show();
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

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());

        dialog.setPositiveButton("Добавить",
                (dialogInterface, which) -> {
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
                    if (pass.getText().toString().length() < 6 && pass.getText().toString().length() >10) {
                        Snackbar.make(root, "Введите пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    // Регистрация пользователя
                    var result = auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnSuccessListener(authResult -> {
                                User2 user = new User2();
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPass(pass.getText().toString());
                                user.setPhone(phone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnCanceledListener(() -> {
                                Log.e("createUserWithEmailAndPassword", "Cancel");
                            })
                            .addOnFailureListener((exception) -> {
                                if (exception instanceof FirebaseAuthWeakPasswordException) {
                                    var caughtException = (FirebaseAuthWeakPasswordException) exception;
                                    Log.e("createUserWithEmailAndPassword", caughtException.getReason());
                                } else
                                    Log.e("createUserWithEmailAndPassword", exception.toString());
                            });

                });
        dialog.show();
    }
}