package com.fenix.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.ContextService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.ThreadUtil;
import com.mongodb.client.model.Filters;

import lombok.var;


public class ScrActivity extends AppCompatActivity {
    private Button remuv;
    private final View.OnClickListener remove_Listener = v -> {

        Log.i(">>>>>>>>>>>>>>>>>>>> mongo", "begin");

        var service = new MongoService("fenix");

        Log.i(">>>>>>>>>>>>>>>>>>>> mongo", "get collection");

        var collection = service.getDocuments("actors");

        ThreadUtil
                .Do(() -> {
                    Log.i(">>>>>>>>>>>>>>>>>>>> mongo", Long.toString(collection.count()));

                })
                .then((res) -> {
                    ThreadUtil.Do(() -> Log.i(">>>>>>>>>>>>>>>>>>>> mongo", collection.find(Filters.eq("name", "test do not remove")).first().toJson()));
                })
                .then((res) -> {
                    this.finish();
                })
                .error((ex) -> {
                    Log.e(">>>>>>>>>>>>>>>>>>>> mongo", ex.toString());
                });

        Log.i(">>>>>>>>>>>>>>>>>>>> mongo", "complete");

        // this.finish();
        //       Intent intent = new Intent(MapsActivity.this, ScrActivity.class);
        //      startActivity(intent);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr);


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
//
//                                try {
//                                    Intent intent = new Intent(ScrActivity.this, MapsActivity.class);
//                                    startActivity(intent);
//                                }catch (Throwable e){
//                                    Log.e("FireBase", e.toString());
//                                }

                                finish();
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(root, "Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
                    if (pass.getText().toString().length() < 5 && pass.getText().toString().length() > 10) {
                        Snackbar.make(root, "Введите пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    // Регистрация пользователя
                    auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnSuccessListener(authResult -> {
                                ActorDto user = new ActorDto();
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPass(pass.getText().toString());
                                user.setPhone(phone.getText().toString());
                                ContextService.Context.setActor(user);
                                ContextService.Context.setActor2(user);

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                            Intent hero = new Intent(ScrActivity.this, HeroPick.class);
                                            startActivity(hero);
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