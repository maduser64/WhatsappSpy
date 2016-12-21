package com.csic.whatsappspy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by martam on 16/11/2016.
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


    public Cursor read(long number){
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

        //Cláusula WHERE para buscar por producto

        String where = "number" + " LIKE "+ "'" + "00" + number + "'";


        //Ejecuta la sentencia devolviendo los resultados de los parámetros pasados de tabla, columnas, producto y orden de los resultados obtenidos.
        Cursor cursor = dbWa.query(ConstantsDB.TABLE_WA_CONTACTS, fields, where ,null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

        }
        return cursor;
    }

    public Contact readContact(long number){
        Contact contact = null;

        DataBaseWa dbWa = new DataBaseWa(this.context);
        dbWa.getReadableDatabase();

        contact = readContactPrivate(number,dbWa);

        dbWa.close();
        return contact;
    }

    private Contact readContactPrivate(long number,DataBaseWa dbWa){ // pasar las row de wa a tipo contactos/eventos
        Contact contact = null;
        Event event_status = null;
        Event event_photo = null;

        Cursor cursor = dbWa.read(number);
        int index = cursor.getCount();

        if (index > 0) {

            if (cursor.getInt(4) != 0) {//Es usuario de whatsapp?

                contact = new Contact("00" + number, number);
                String status = cursor.getString(1);
                if (status!=null)
                    contact.addStatus(cursor.getString(1), cursor.getLong(2));

                contact.addPhoto("00" + number, cursor.getLong(3) );

            }
        }

        cursor.close();
        return contact;
    }

    public ArrayList<Contact> readContact(long from,long to){

        DataBaseWa dbWa = new DataBaseWa(this.context);
        dbWa.getReadableDatabase();
        ArrayList<Contact> list = new ArrayList<Contact>();
        Contact contact = null;
        for (long i = from; i <= to; i++){
            contact = readContactPrivate(i,dbWa);
            if(contact != null)
                list.add(contact);
        }

        dbWa.close();
        return list;

    }





}

