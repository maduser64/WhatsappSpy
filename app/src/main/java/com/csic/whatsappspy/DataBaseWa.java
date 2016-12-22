package com.csic.whatsappspy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 *
 * Controlador de la base de datos de whatsapp
 */
public class DataBaseWa extends SQLiteOpenHelper {
    private Context context;

    public DataBaseWa(Context context) {
        super(context, ConstantsDB.DATABASE_NAME_WA, null, ConstantsDB.DATABASE_VERSION_WA);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion,  int newVersion){

        db.setVersion(newVersion); //importante
    }


    /*
        Lee un numero y lo devuelve en forma de cursor
     */
    private Cursor read(long number){
        //Se establecen permisos de lectura
        SQLiteDatabase dbWa = this.getReadableDatabase();
        //Columnas que devolverá la consulta.
        String[] fields = {
                ConstantsDB.TABLE_WA_CONTACTS_NUMBER,        //index 0
                ConstantsDB.TABLE_WA_CONTACTS_STATUS,        //index 1
                ConstantsDB.TABLE_WA_CONTACTS_STATUS_TS,     //index 2
                ConstantsDB.TABLE_WA_CONTACTS_THUMB_TS,      //index 3 (tm del avatar)
                ConstantsDB.TABLE_WA_CONTACTS_WA_USER,       //index 4

        };

        //Cláusula WHERE para buscar por numero

        String where = "number" + " LIKE "+ "'" + "00" + number + "'";

        //Ejecutamos la consulta
        return dbWa.query(ConstantsDB.TABLE_WA_CONTACTS, fields, where ,null, null, null, null);
    }

    /*
        Lee en contacto con el numero indicado de la base de datos
     */
    public Contact readContact(long number){

        DataBaseWa dbWa = new DataBaseWa(this.context);
        dbWa.getReadableDatabase();

        Contact contact = readContactPrivate(number,dbWa);

        dbWa.close();
        return contact;
    }

    private Contact readContactPrivate(long number,DataBaseWa dbWa){ // pasar las row de wa a tipo contactos/eventos
        Contact contact = null;

        Cursor cursor = dbWa.read(number);

        //Si el numero existe
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getCount();

            if (index > 0) {

                if (cursor.getInt(4) != 0) {//Es usuario de whatsapp?

                    contact = new Contact("00" + number, number);
                    String status = cursor.getString(1);
                    if (status != null)
                        contact.addStatus(cursor.getString(1), cursor.getLong(2));

                    contact.addPhoto("00" + number, cursor.getLong(3));

                }
            }
            cursor.close();
        }

        return contact;
    }

    /*
        Lista de todos los contactos en un rango de numeros
     */
    public ArrayList<Contact> readContact(long from,long to){

        DataBaseWa dbWa = new DataBaseWa(this.context);
        dbWa.getReadableDatabase();
        ArrayList<Contact> list = new ArrayList<>();

        for (long i = from; i <= to; i++){
            Contact contact = readContactPrivate(i,dbWa);
            if(contact != null)
                list.add(contact);
        }

        dbWa.close();
        return list;

    }





}

