package com.example.pan;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.pan.Servicios.Enviador;
import com.example.pan.Servicios.Location;

public class Portada extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);

        Funciones funciones=new Funciones(getApplicationContext());

        Intent service1 = new Intent(getApplicationContext(),  Enviador.class);
        service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(service1);

        if(funciones.Check_Log()) {
            Intent service = new Intent(getApplicationContext(),  Location.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(service);
        }

        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(Portada.this, Login.class);
                startActivity(intent);
                finish();
            };
        }, 1000);
    }
}
