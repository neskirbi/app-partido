package com.example.pan.Encuestas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.pan.Funciones;
import com.example.pan.R;

public class Preguntas extends AppCompatActivity  {

    String id_ecuesta="";
    Funciones funciones ;
    LinearLayout contenedor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        funciones=new Funciones(getApplicationContext());
        id_ecuesta=getIntent().getExtras().getString("id_encuesta");
        contenedor=findViewById(R.id.contenedor);
        funciones.CargarPreguntas(contenedor,id_ecuesta);

        //funciones.LeerCP();



    }


}
