package com.fenix.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.PersonDto;
import com.fenix.app.service.ContextService;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.fenix.app.util.ThreadUtil;
import com.google.android.gms.common.util.Strings;
import com.google.android.material.snackbar.Snackbar;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.text.WordUtils;
import org.bson.Document;

import lombok.var;


public class LogonActivity extends AppCompatActivity {

    private MongoService mongoService;
    private MongoCollection<Document> actorsCollection;

    private RelativeLayout root;

    private Button btnSignIn;
    private final View.OnClickListener btnSignInListener = v -> {
        Log.i("LogonActivity", "btnSignIn click");
        showSignWindow();
    };

    private Button btnRegister;
    private final View.OnClickListener btnRegisterListener = v -> {
        Log.i("LogonActivity", "btnRegister click");
        showRegisterWindow();
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Services
        ThreadUtil.Do(() -> {
            mongoService = new MongoService("fenix");
            actorsCollection = mongoService.getDocuments("actors");
        }).error(ex -> {
            throw new RuntimeException(ex.toString());
        });

        // Init view
        setContentView(R.layout.activity_logon);
        root = findViewById(R.id.root_element);

        // btnSignIn
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(btnSignInListener);

        // btnRegister
        btnRegister = findViewById(R.id.butRegister);
        btnRegister.setOnClickListener(btnRegisterListener);

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

        dialog.setPositiveButton("Войти",
                (dialogInterface, which) -> {
                    if (TextUtils.isEmpty(email.getText().toString())) {
                        Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(pass.getText().toString())) {
                        Snackbar.make(root, "Введите пароль", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Проверка и загрузка профиля пользователя
                    ThreadUtil
                            .Do(() -> {
                                String emailString = email.getText().toString().toLowerCase();
                                String passString = pass.getText().toString();

                                var actorDocument = actorsCollection.find(Filters
                                        .and(
                                                Filters.eq("email", emailString),
                                                Filters.eq("pass", passString))
                                ).limit(1).first();

                                // Пользователь не найден?
                                if (actorDocument == null) {
                                    Snackbar.make(root, "Ошибка авторизации, проверьте введенную почту и пароль ", Snackbar.LENGTH_SHORT).show();
                                    throw new RuntimeException("Incorrect login or password");
                                }

                                // Пользователь вошёл в игру
                                var actorDto = JsonUtil.Parse(ActorDto.class, actorDocument.toJson());

                                // Проверяю наличие профиля
                                if (actorDto.getPerson() == null)
                                    actorDto.setPerson(new PersonDto());

                                // Устанавливаю пользовательский контекст
                                ContextService.Context.setActor(actorDto);

                                Log.i("showSignWindow", "Logon completed");
                                Intent intent = new Intent(LogonActivity.this, MapsActivity.class);
                                startActivity(intent);
                            })
                            .error(ex -> {
                                Log.e("showSignWindow", ex.toString());
                            });
                });
        dialog.show();
    }

    private void showRegisterWindow() {
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
                        Snackbar.make(root, "Введите пароль, который более 5 символов и менее 10", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Регистрация пользователя
                    String emailString = email.getText().toString().toLowerCase();
                    String nameString = WordUtils.capitalize(name.getText().toString().toLowerCase());
                    String phoneString = phone.getText().toString();
                    String passString = pass.getText().toString();
                    ThreadUtil
                            .Do(() -> {
                                String errorString = "";

                                // Проверка на повторную регистрацию по почте
                                Log.i("showRegisterWindow", "Check user by email " + emailString);
                                var oldActor = actorsCollection.find(Filters.eq("email", emailString)).limit(1).first();
                                if (oldActor != null) {
                                    errorString += "Пользователь с электронной почтой\n" + emailString + "\nуже зарегистрирован!\n";
                                }

                                // Проверка на повторную регистрацию по телефону
                                Log.i("showRegisterWindow", "Check user by phone " + phoneString);
                                oldActor = actorsCollection.find(Filters.eq("phone", phoneString)).limit(1).first();
                                if (oldActor != null) {
                                    errorString += "Пользователь с телефоном\n" + phoneString + "\nуже зарегистрирован!\n";
                                }

                                // Проверка на повторную регистрацию по имени
                                Log.i("showRegisterWindow", "Check user by name " + nameString);
                                oldActor = actorsCollection.find(Filters.eq("name", nameString)).limit(1).first();
                                if (oldActor != null) {
                                    errorString += "Пользователь с именем\n" + nameString + "\nуже зарегистрирован!\n";
                                }

                                // Обработка логических ошибок
                                if (!Strings.isEmptyOrWhitespace(errorString)) {
                                    Log.i("showRegisterWindow", errorString);
                                    Snackbar.make(root, errorString, Snackbar.LENGTH_SHORT).show();
                                } else {
                                    // Создаю нового пользователя
                                    var actor = new ActorDto();
                                    actor.setName(nameString);
                                    actor.setEmail(emailString);
                                    actor.setPhone(phoneString);
                                    actor.setPass(passString);
                                    ThreadUtil.Await(() -> actorsCollection.insertOne(Document.parse(JsonUtil.Serialize(actor))));

                                    Log.i("showRegisterWindow", "User " + nameString + " added!");
                                    Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .error((ex) -> {
                                Log.e("showRegisterWindow", ex.toString());
                            });
                });
        dialog.show();
    }
}