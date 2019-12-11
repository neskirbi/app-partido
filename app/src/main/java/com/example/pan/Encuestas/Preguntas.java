package com.example.pan.Encuestas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pan.Funciones;
import com.example.pan.R;

public class Preguntas extends AppCompatActivity  {

    String id_ecuesta="",titulo="";
    Funciones funciones ;
    LinearLayout contenedor;
    ImageButton enviar;
    TextView ttitulo;
    TextView repuestas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        funciones=new Funciones(getApplicationContext());
        id_ecuesta=getIntent().getExtras().getString("id_encuesta");
        titulo=getIntent().getExtras().getString("titulo");
        ttitulo=findViewById(R.id.titulo);
        ttitulo.setText(titulo);
        contenedor=findViewById(R.id.contenedor);
        funciones.CargarPreguntas(contenedor,id_ecuesta);
        enviar=findViewById(R.id.enviar);
        repuestas=new TextView(getApplicationContext());
        contenedor.addView(repuestas);
        //funciones.LeerCP();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(70);
                repuestas.setText(funciones.EnviarRespuestas());

            }
        });



    }


}
