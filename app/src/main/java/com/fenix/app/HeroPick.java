package com.fenix.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.ContextService;


import io.netty.util.Constant;

public class HeroPick extends AppCompatActivity {
    private Button myVamp;
    // DatabaseReference users;
    ConstraintLayout root;

    @SuppressLint("WrongViewCast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_pick);
        myVamp = findViewById(R.id.button_Vamp);
        myVamp.setOnClickListener(view -> {

            ActorDto user = ContextService.Context.getActor();

/*            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        String d = ContextService.Context.getActor().getName();
                        Snackbar.make(root, "Герой выбран, Вы Вампир!", Snackbar.LENGTH_SHORT).show();

                    });*/

        });
    }
}