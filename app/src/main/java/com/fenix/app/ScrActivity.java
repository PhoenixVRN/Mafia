package com.fenix.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import Models.User2;


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

        AlertDialog.Builder builder = dialog.setPositiveButton("Добавить",
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
                    if (pass.getText().toString().length() < 5) {
                        Snackbar.make(root, "Введите пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    // Регистрация пользователя
                    auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnSuccessListener(authResult -> {
                                User2 user = new User2();
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPass(pass.getText().toString());
                                user.setPhone(phone.getText().toString());

                                users.child(user.getEmail())
                                        .setValue(user)
                                        .addOnSuccessListener(aVoid -> Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show());
                            });
                });
        dialog.show();
    }
}