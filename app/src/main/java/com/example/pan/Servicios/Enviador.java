package com.example.pan.Servicios;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.example.pan.Funciones;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Enviador extends Service {
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    Funciones funciones;
    public Enviador() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Iniciando Servicios2.", Toast.LENGTH_SHORT).show();
        funciones=new Funciones(getApplicationContext());

        Enviar enviar=new Enviar();
        enviar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "PAN::MyWakelockTag");
        wakeLock.acquire();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }




        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        Intent service = new Intent(getApplicationContext(),  Enviador.class);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(service);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class Enviar extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {

            Log.i("Location","Iniciado servicio 2 ");
            while(true){
                funciones.EnviarLocation();
                funciones.EnviarRespuestas();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);
        }
    }
}
