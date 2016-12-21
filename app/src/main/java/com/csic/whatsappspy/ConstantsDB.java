package com.csic.whatsappspy;

/**
 * Created by martam on 16/11/2016.
 */
public class ConstantsDB {

    public static final String DATABASE_NAME_SPY = "spy.db";
    public static final int DATABASE_VERSION_SPY = 1;

    public static final String DATABASE_NAME_WA = "wa.db";
    public static final int DATABASE_VERSION_WA = 1;


    public static final String TABLE_SPY_CONTACTS = "spy_contacts";
    public static final String TABLE_ID = "id";
    public static final String TABLE_NUMBER = "number";

    public static final String TABLE_SPY_STATUS = "spy_status";
    public static final String TABLE_SPY_STATUS_STATUS = "status";
    public static final String TABLE_SPY_STATUS_STATUS_TS = "status_timestamp";


    public static final String TABLE_SPY_PHOTOS = "spy_photos";
    public static final String TABLE_SPY_PHOTOS_PHOTO_TS = "thumb_ts";
    public static final String TABLE_SPY_PHOTOS_PATH = "photo_path";

    public static final String TABLE_WA_CONTACTS  = "wa_contacts";
    public static final String TABLE_WA_CONTACTS_NUMBER = "number";
    public static final String TABLE_WA_CONTACTS_STATUS = "status";
    public static final String TABLE_WA_CONTACTS_STATUS_TS = "status_timestamp";
    public static final String TABLE_WA_CONTACTS_THUMB_TS = "thumb_ts";

    public static final String TABLE_WA_CONTACTS_WA_USER= "is_whatsapp_user";
    //Rutas

    public static final String URL_PHOTO_WHATSAPP = "";
    public static final String URL_PHOTO_SPY = "";
    public static final String URL_DB_WHATSAPP = "";

}

