package com.example.antifakeuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton btnComSearch=null;
    ImageButton btnQualifySearch=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnComSearch=findViewById(R.id.imageButton_com_search);
        btnQualifySearch=findViewById(R.id.imageButton_qualify_search);
        btnComSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ComSearchActivity.class);
                startActivity(intent);
            }
        });
        btnQualifySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, QualifySearchActivity.class);
                startActivity(intent);
            }
        });
    }
}