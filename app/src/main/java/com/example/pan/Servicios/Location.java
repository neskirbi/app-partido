package com.example.pan.Servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.example.pan.Funciones;
import com.example.pan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Location extends Service {
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    Double Lat , Lon ;
    String fecha="",id_usuario="",id_location;
    Funciones funciones;
    ArrayList<Double> lat=new ArrayList<>(),lon=new ArrayList<>();
    Boolean ciclo;


    public Location() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Iniciando Servicios.", Toast.LENGTH_SHORT).show();


        ciclo=true;


        funciones=new Funciones(getApplicationContext());


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(android.location.Location location) {

                lat.add (location.getLatitude());
                lon.add (location.getLongitude());

                Log.i("Location","Lat: "+location.getLatitude());
                Log.i("Location","Lon: "+location.getLongitude());


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }else{
            boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gps_enabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }else if (network_enabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }


        }

        Localizando localizando=new Localizando();
        localizando.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Detener servicio de localizacion
        Toast.makeText(this, "Deteniendo Servicios.", Toast.LENGTH_SHORT).show();

        ciclo=false;
        locationManager.removeUpdates(locationListener);


        if(funciones.Check_Log()) {
            Intent service = new Intent(getApplicationContext(),  Location.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(service);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    class Localizando extends AsyncTask{



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("Location","Iniciado servicio 1 ");
            while(ciclo){
                try {


                    if(lat.size() > 0){
                        ArrayList<Double>lat2= new ArrayList<>(lat),lon2= new ArrayList<>(lon);


                        lat.clear();
                        lon.clear();

                        Lat=0.0;
                        Lon=0.0;

                        for(int i=0;i < lat2.size();i++){

                            Lat += lat2.get(i);
                            Lon += lon2.get(i);


                        }
                        Lat = Lat/lat2.size();
                        Lon = Lon/lon2.size();
                        if((Lat+"").length()>15){
                            Lat=Double.parseDouble(Lat.toString().substring(0,14));

                        }
                        if((Lon+"").length()>15){
                            Lon=Double.parseDouble(Lon.toString().substring(0,14));
                        }




                        id_location=funciones.GetUIID();
                        id_usuario=funciones.GetIdUser();
                        fecha = funciones.GetDateTime();

                        Log.i("Location", "id_location: " + id_location);
                        Log.i("Location", "id_usuario: " + id_usuario);
                        Log.i("Location", "fecha: " + fecha);
                        Log.i("Location", "lat: " + Lat);
                        Log.i("Location", "lon: " + Lon);
                        Log.i("Location", "bateria: " + funciones.BateriaNivel());




                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("id_location",id_location);
                            jsonObject.put("id_usuario",id_usuario);
                            jsonObject.put("fecha",fecha);
                            jsonObject.put("lat",Lat);
                            jsonObject.put("lon",Lon);
                            jsonObject.put("bateria",funciones.BateriaNivel());
                            funciones.GuardarLocation(jsonObject);

                            //Verificar cada que guarda coordenadas si el enviador esta corriendo
                            if(!funciones.isMyServiceRunning(Enviador.class)) {
                                Intent service = new Intent(getApplicationContext(),Enviador.class);
                                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startService(service);
                            }

                            //String url=funciones.URL_Dominio()+getString(R.string.url_CargarLocation);
                            //Log.i("Location",funciones.Conexion(jsonObject.toString(),url));
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }



                    }else{
                        Log.i("Location","Sin datos.");
                    }
                    Thread.sleep(60000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }



}


