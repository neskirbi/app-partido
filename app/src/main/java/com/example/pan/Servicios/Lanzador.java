package com.example.pan.Servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pan.Funciones;

public class Lanzador extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Funciones funciones=new Funciones(context);

        Intent service1 = new Intent(context,  Enviador.class);
        service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(service1);

        if(funciones.Check_Log()) {
            Intent service = new Intent(context,  Location.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(service);
        }


    }



}
