package com.csic.whatsappspy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Controla la lectura y escritura de contactos , fotos y estados en nuestra base de datos
 */
public class DataBaseSpy extends SQLiteOpenHelper {
    private Context context;
    private String PATH_AVATARS = "/data/data/com.csic.whatsappspy/cache/";
    private Boolean equalPhoto = false;

    public DataBaseSpy(Context context){
        super(context, ConstantsDB.DATABASE_NAME_SPY, null, ConstantsDB.DATABASE_VERSION_SPY);
        this.context = context;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String queryCreateTableContacts = "CREATE TABLE " + ConstantsDB.TABLE_SPY_CONTACTS + "(" +
                ConstantsDB.TABLE_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TABLE_NUMBER + " TEXT UNIQUE " +
                ")";

        sqLiteDatabase.execSQL(queryCreateTableContacts);

        String queryCreateTableStatus = "CREATE TABLE " + ConstantsDB.TABLE_SPY_STATUS + "(" +
                ConstantsDB.TABLE_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TABLE_NUMBER + " TEXT, " +
                ConstantsDB.TABLE_SPY_STATUS_STATUS + " TEXT, " +
                ConstantsDB.TABLE_SPY_STATUS_STATUS_TS + " LONG," +
                " FOREIGN KEY (" + ConstantsDB.TABLE_NUMBER + ") REFERENCES " +
                ConstantsDB.TABLE_SPY_CONTACTS + " (" + ConstantsDB.TABLE_NUMBER + ")" +
                ")";

        sqLiteDatabase.execSQL(queryCreateTableStatus);

        String queryCreateTablePhotos = "CREATE TABLE " + ConstantsDB.TABLE_SPY_PHOTOS + "(" +
                ConstantsDB.TABLE_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TABLE_NUMBER + " TEXT, " +
                ConstantsDB.TABLE_SPY_PHOTOS_PATH + " TEXT, " +
                ConstantsDB.TABLE_SPY_PHOTOS_PHOTO_TS + " LONG," +
                " FOREIGN KEY (" + ConstantsDB.TABLE_NUMBER + ") REFERENCES " +
                ConstantsDB.TABLE_SPY_CONTACTS + " (" + ConstantsDB.TABLE_NUMBER + ")" +
                ")";

        sqLiteDatabase.execSQL(queryCreateTablePhotos);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //insertRows en cada una de las tablas
    private void insertRowTPhotos(long number, String photo_path, long photo_ts){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsDB.TABLE_NUMBER, "00" + number);
        values.put(ConstantsDB.TABLE_SPY_PHOTOS_PATH, photo_path);
        values.put(ConstantsDB.TABLE_SPY_PHOTOS_PHOTO_TS, photo_ts);
        db.insert(ConstantsDB.TABLE_SPY_PHOTOS, null, values);
        db.close();
    }

    private void insertRowTContacts(long number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsDB.TABLE_NUMBER, "00" + number);
        db.insert(ConstantsDB.TABLE_SPY_CONTACTS, null, values);
        db.close();
    }

    private void insertRowTStatus(long number, String status, long status_ts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsDB.TABLE_NUMBER, "00" + number);
        values.put(ConstantsDB.TABLE_SPY_STATUS_STATUS, status);
        values.put(ConstantsDB.TABLE_SPY_STATUS_STATUS_TS, status_ts);
        db.insert(ConstantsDB.TABLE_SPY_STATUS, null, values);
        db.close();
    }


    /*
        Lee un contacto de la base de datos y lo devuelve como cursor
     */
    private Cursor read(long number, String table, String[] fields){ //el array fields son los campos de los que se hace select
        //Se establecen permisos de lectura
        SQLiteDatabase dbSpy = this.getReadableDatabase();

        //Cláusula WHERE para buscar por numero
        String where = "number LIKE '00" + number + "'";

        //Ejecuta la consulta

        return dbSpy.query(table, fields, where ,null, null, null, null);
    }

    /**
     * Lee un contacto de la base de datos por su numero de telefono
     */
    public Contact getContact(long number){

        DataBaseSpy dataBaseSpy = new DataBaseSpy(this.context);
        dataBaseSpy.getReadableDatabase();

        Contact contact = getContactPrivate(number,dataBaseSpy);

        dataBaseSpy.close();
        return contact;
    }


    /*
        Lee un contacto de la base de datos por el numero de telefono
     */
    private Contact getContactPrivate(long number, DataBaseSpy dataBaseSpy){ //lee la base de datos Spy crea los contactos y eventos
        Contact contact = null;

        //Campos a obtener de la tabla contactos
        String[] fields_contacts = {ConstantsDB.TABLE_ID, ConstantsDB.TABLE_NUMBER};
        Cursor cursor_contactos = dataBaseSpy.read(number, ConstantsDB.TABLE_SPY_CONTACTS, fields_contacts);

        if(cursor_contactos != null) {
            int size = cursor_contactos.getCount();

            if (size > 0) {//hay algun contacto?

                //cursor de estados
                String[] fields_status = {ConstantsDB.TABLE_ID, ConstantsDB.TABLE_NUMBER, ConstantsDB.TABLE_SPY_STATUS_STATUS, ConstantsDB.TABLE_SPY_STATUS_STATUS_TS};
                Cursor cursor_status = dataBaseSpy.read(number, ConstantsDB.TABLE_SPY_STATUS, fields_status);

                //Cursor de fotos
                String[] fields_photo = {ConstantsDB.TABLE_ID, ConstantsDB.TABLE_NUMBER, ConstantsDB.TABLE_SPY_PHOTOS_PATH, ConstantsDB.TABLE_SPY_PHOTOS_PHOTO_TS};
                Cursor cursor_photos = dataBaseSpy.read(number, ConstantsDB.TABLE_SPY_PHOTOS, fields_photo);

                //Creamos un objeto contacto con el numero buscado
                contact = new Contact("00" + number, number);

                //Añadimos los estados que tenga
                if(cursor_status != null) {
                    int size_status = cursor_status.getCount();
                    while (cursor_status.getPosition() < (size_status - 1)) {

                        cursor_status.moveToNext();
                        contact.addStatus(cursor_status.getString(2), cursor_status.getLong(3));


                    }
                    cursor_status.close();
                }

                //Añadimos la fotos que tenga
                if(cursor_photos != null) {
                    int size_photos = cursor_photos.getCount();
                    while (cursor_photos.getPosition() < (size_photos - 1)) {

                        cursor_photos.moveToNext();
                        contact.addPhoto(cursor_photos.getString(2), cursor_photos.getLong(3));
                    }


                    cursor_photos.close();
                }
                cursor_contactos.close();
            }

        }

        return contact;

    }

    /*
        Lista de todos los contactos de nuestra base de datos dentro del rango de telefonos dados
     */
    public ArrayList<Contact> readContact(long from, long to){
        ArrayList<Contact> contacts_list = new ArrayList<>();
        DataBaseSpy dataBaseSpy = new DataBaseSpy(this.context);
        dataBaseSpy.getReadableDatabase();

        for (long i = from;i <= to; i++){
            Contact contact = getContactPrivate(i,dataBaseSpy);

            if(contact != null){ contacts_list.add(contact);}

        }

        dataBaseSpy.close();
        return contacts_list;
    }

    /*
        Por cada numero en la base de datos de whatsapp cera un contacto en nuestra base de datos,
        si este no existe y registra sus nuevas fotos y estados
     */
    private void writeContact2(Contact contact,DataBaseSpy dataBaseSpy) {

        Contact contactSpy = getContact(contact.getPhone());   //Contacto Spy

        //Si el contacto no esta registrado en nuestra base de datos
        if(contactSpy == null){
            //crear row en spy_contact (id + number)
            dataBaseSpy.insertRowTContacts(contact.getPhone());
            contactSpy = new Contact(contact.getPhone() + "" ,contact.getPhone());
            Log.i("info", "New contact " + contact.getName());
        }

        //Si no tiene stado o el nuevo es distinto
        if (contact.getLastStatus() != null && (contactSpy.getLastStatus() == null || !contactSpy.getLastStatus().getEvent().equals(contact.getLastStatus().getEvent()))){

            dataBaseSpy.insertRowTStatus(contact.getPhone(), contact.getLastStatus().getEvent(), contact.getLastStatus().getDate());
            Log.i("info", "New status " + contact.getLastStatus().getEvent());
            Log.i("info", "ts " + contact.getLastStatus().date().toString());
        }


        //Photo
        String urlSpy = PATH_AVATARS + contactSpy.getPhone() + "_" + contactSpy.getNumPhotos() +".png";
        String urlWa = "/data/data/com.whatsapp/files/Avatars/" + contact.getPhone()+ "@s.whatsapp.net.j";

        //Copiamos la foto como si fuera nueva
        this.copyPhoto(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");
        File f = new File(PATH_AVATARS + contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");
        Log.i("info",PATH_AVATARS + contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png" + f.exists());



        //Si el nuevo contacto tiene foto y ademas no esta registrada
        if (f.exists()) {

            if (contactSpy.getLastPhoto() == null ||
                    !compareImages(BitmapFactory.decodeFile(PATH_AVATARS + contactSpy.getPhone() + "_" + (contactSpy.getNumPhotos() - 1) + ".png"),
                    BitmapFactory.decodeFile(PATH_AVATARS + contactSpy.getPhone() + "_" + contactSpy.getNumPhotos() + ".png")))
            {
                dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos(), contact.getLastPhoto().getDate());

                Log.i("info", "New photo " + contact.getPhone() + "_" + contactSpy.getNumPhotos());
                Log.i("info", "ts " + contact.getPhoto(0).date().toString());
            } else {
                //Borramos la foto copiada
                this.deletePhoto(contactSpy.getPhone(), contactSpy.getNumPhotos());
            }
        }

    }

    /**
     * Introduce un contacto en la base de datos. Si ya existe, solo introduce la foto y el estado
     * si son nuevos
     */


    private void writeContact(Contact contact,DataBaseSpy dataBaseSpy) { //escribe y hace el control de escritura: usar el getcontacts y insert row en las tablas
        Contact contactSpy = getContact(contact.getPhone());
        Log.i("info","Scanning... " + contact.getPhone());
        if( contactSpy != null ) { // si no hay ningun contacto con este numero el cursor es = null (ver metodo getcontact)
        Log.i("info", "ya existe; " + contactSpy.getPhone());
            if (contact.getStatus(0).getEvent() != null) {
                if (contactSpy.getLastStatus() == null || contactSpy.getLastStatus().getEvent().compareTo(contact.getStatus(0).getEvent()) != 0 ) {
                    dataBaseSpy.insertRowTStatus(contact.getPhone(), contact.getStatus(0).getEvent(), contact.getStatus(0).getDate());
                    Log.i("info", "New status " + contact.getStatus(0).getEvent());
                    Log.i("info", "ts " + contact.getStatus(0).date().toString());
                }
            }

            if (contactSpy.getLastPhoto() != null) { // si la foto anterior existe
                copyPhoto(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");  //copiar sea como sea
                equalPhoto = compareImages(BitmapFactory.decodeFile(PATH_AVATARS + contactSpy.getPhone() + "_" + (contactSpy.getNumPhotos() - 1) + ".png"),
                            BitmapFactory.decodeFile(PATH_AVATARS + contactSpy.getPhone() + "_" + contactSpy.getNumPhotos() + ".png"));

                Log.i("info", "equalPhoto: " + equalPhoto);

                if(equalPhoto){
                    deletePhoto( contactSpy.getPhone(), contactSpy.getNumPhotos());
                    Log.i("info", "Deleting " + contactSpy.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");
                }

                else{
                    if (contact.getPhoto(0).getDate() != 0){ dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos(), contact.getPhoto(0).getDate()); }
                    else{ dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos(), 0 );} //revisar
                    Log.i("info", "New photo " + contact.getPhone() + "_" + contactSpy.getNumPhotos() + " ; ts " + contact.getPhoto(0).date().toString());

                }



            }else{ // si la foto anterior era null, que intente copiar y si existe intertar registro

                copyPhoto(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");
                File f2 = new File(PATH_AVATARS +  contact.getPhone() +"_" + contactSpy.getNumPhotos() +".png");
                Log.i("info",PATH_AVATARS + contact.getPhone() +"_" + contactSpy.getNumPhotos() + ".png " + f2.exists());
                if (f2.exists()) {
                    if (contact.getPhoto(0).getDate() != 0){ dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos(), contact.getPhoto(0).getDate()); }
                    else{ dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos(), 0 );} //revisar
                    Log.i("info", "New photo " + contact.getPhone() + "_" + contactSpy.getNumPhotos() + " ; ts " + contact.getPhoto(0).date().toString());
                }

            }

        } else {
                //crear row en spy_contact (id + number)
                dataBaseSpy.insertRowTContacts(contact.getPhone());
                Log.i("info","Writing new contact " + contact.getPhone());

                //crear row en spy_status (status, status_timestamp, numero, id)
                if (contact.getStatus(0).getEvent() !=  null) {
                    dataBaseSpy.insertRowTStatus(contact.getPhone(), contact.getStatus(0).getEvent(), contact.getStatus(0).getDate()); // hacer algo con la fecha
                    Log.i("info", "first status " + "phone " + contact.getPhone() + "; Status: " + contact.getStatus(0).getEvent() + " ; ts: " + contact.getStatus(0).getDate());
                }

                //copiar la foto
                copyPhoto(contact.getPhone(), contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");

                File f = new File(PATH_AVATARS + contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png");
                Log.i("info", "Foto copiada "+ PATH_AVATARS +  contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png ; " + f.exists());
                if (f.exists()) {
                    // if(contact.getPhoto(0).getDate_long() != 0 && contact.getPhoto(0).getDate_long() != -1) {
                    //crear row en spy_photo (photo_path, photo_ts, numero, id)
                    if (contact.getPhoto(0).getDate() != 0){dataBaseSpy.insertRowTPhotos(contact.getPhone(),  contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png", contact.getPhoto(0).getDate());}
                    else {dataBaseSpy.insertRowTPhotos(contact.getPhone(),  contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png", 0 );}
                    Log.i("info", "first photo " +  contact.getPhone() + "_" + contactSpy.getNumPhotos() + ".png ; ts " + contact.getPhoto(0).getDate());

                    //copyPhoto(contact.getPhone(),contact.getPhone() + "_0.png");
                }
            }

        }


    /*
        Copia la foto de un contacto de whatsapp a nuesto sistema de ficheros
     */
    private void copyPhoto(long phone,String photoSpy){

       // final String PREF_ESP = "34";
        final String SUF = "@s.whatsapp.net.j";

        String photoWa = phone + SUF ;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c","cp -r /data/data/com.whatsapp/files/Avatars/"+ photoWa +" /data/data/com.csic.whatsappspy/cache/" + photoSpy});
            p = Runtime.getRuntime().exec(new String[]{"su","-c","chmod -R 777 /data/data/com.csic.whatsappspy/cache/" + photoSpy});
            Log.i("info","Copied " + PATH_AVATARS + photoSpy );


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("info", "No se ha copiado " + PATH_AVATARS + photoSpy );
        }

        //Le damos tiempo a que la copie
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
        Borra una foto de un contacto en nuestro sistema de ficheros
     */
    private  void  deletePhoto(long phone, int numPhonto) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c", "rm " + PATH_AVATARS + phone + "_" + numPhonto});
            Log.i("info", "borrando " + PATH_AVATARS + phone + "_" + numPhonto);
        }catch (IOException e) {e.printStackTrace();}
    }

    /**
     * Introduce una lista de contactos en la base de datos
     */
    public void writeContact(ArrayList<Contact> list){
        DataBaseSpy dataBaseSpy = new DataBaseSpy(this.context);
        for (int i= 0; i < list.size(); i++){

            writeContact2(list.get(i),dataBaseSpy);
        }

        dataBaseSpy.close();
    }

    /**
     * Compare two images.
     * @return true iff both images have the same dimensions and pixel values.
     */
    public static boolean compareImages(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1.getWidth() != bitmap2.getWidth() ||
                bitmap1.getHeight() != bitmap2.getHeight()) {
            return false;
        }

        for (int y = 0; y < bitmap1.getHeight(); y++) {
            for (int x = 0; x < bitmap1.getWidth(); x++) {
                if (bitmap1.getPixel(x, y) != bitmap2.getPixel(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

}
