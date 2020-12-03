package com.fenix.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        remuv = (Button) findViewById(R.id.button_close);
        remuv.setOnClickListener(remove_Listener);


    }
}