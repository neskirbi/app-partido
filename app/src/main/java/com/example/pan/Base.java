package com.example.pan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Base extends SQLiteOpenHelper {
    private static  String RESPUESTAS_TABLE_CREATE="",ENCUESTAS_TABLE_CREATE="",USUARIOS_TABLE_CREATE="",LOGIN_TABLE_CREATE= "",LOCATION_TABLE_CREATE="";
    private static  String DB_NAME = "comments.sqlite";
    private static  int DB_VERSION = 1;



    public Base(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        USUARIOS_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  usuarios(" +
                "id_usuario TEXT PRIMARY KEY NOT NULL," +
                "id_superior TEXT not null," +
                "user TEXT NOT NULL," +
                "pass TEXT NOT NULL," +
                "nombre TEXT NOT NULL," +
                "foto TEXT NOT NULL," +
                "foto64 TEXT DEFAULT ''," +
                "activo TEXT NOT NULL)";

        LOGIN_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  login(" +
                "id_usuario TEXT PRIMARY KEY NOT NULL," +
                "id_superior TEXT not null," +
                "user TEXT NOT NULL," +
                "pass TEXT NOT NULL," +
                "nombre TEXT NOT NULL," +
                "foto TEXT NOT NULL," +
                "activo TEXT NOT NULL)";


        LOCATION_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  locations(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "json TEXT NOT NULL," +
                "enviado INTEGER DEFAULT 0   )";

        ENCUESTAS_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  encuestas(" +
                "id_encuesta TEXT NOT NULL," +
                "json TEXT NOT NULL," +
                "orden int NOT NULL  )";

        RESPUESTAS_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  respuestas(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "json TEXT NOT NULL," +
                "enviado INTEGER DEFAULT 0   )";


        db.execSQL(USUARIOS_TABLE_CREATE);
        db.execSQL(LOGIN_TABLE_CREATE);
        db.execSQL(LOCATION_TABLE_CREATE);
        db.execSQL(ENCUESTAS_TABLE_CREATE);
        db.execSQL(RESPUESTAS_TABLE_CREATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
