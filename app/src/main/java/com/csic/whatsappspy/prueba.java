package com.csic.whatsappspy;

import android.util.Log;

import java.io.IOException;

/**
 * Created by martam on 02/12/2016.
 */
public class prueba {
/*
    private void writeContact(Contact contact,DataBaseSpy dataBaseSpy){ //escribe y hace el control de escritura: usar el getcontacts y insert row en las tablas

        Contact contactSpy = getContact(contact.getPhone());
        if( contactSpy != null ){ // si no hay ningun contacto con este numero el cursor es = null (ver metodo getcontact)
            if (contactSpy.getLastStatus() != null && contactSpy.getLastStatus().getDate_long() != contact.getStatus(0).getDate_long() ){ // no entiendo la primera condicion
                dataBaseSpy.insertRowTStatus(contact.getPhone(), contact.getStatus(0).getEvent(), contact.getStatus(0).getDate_long());
                Log.i("Nuevo estado",contact.getLastStatus().getEvent());
            }


            if (contactSpy.getLastPhoto() != null && contactSpy.getLastPhoto().getDate_long() != contact.getPhoto(0).getDate_long()){ // no entiendo la primera condicion
                Log.i("Nueva foto",contact.getLastPhoto().getEvent());


                Boolean exits_photo =  copyPhoto(contact.getPhone(), contact.getPhone() + "_" + + contactSpy.getNumPhotos() + ".png");
                if (exits_photo){
                    dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhoto(0).getEvent()+ "_" + contactSpy.getNumPhotos(), contact.getPhoto(0).getDate_long());

                }

            }


        }else{
            //crear row en spy_contact (id + number)
            dataBaseSpy.insertRowTContacts(contact.getPhone());
            //crear row en spy_status (status, status_timestamp, numero, id)
            dataBaseSpy.insertRowTStatus(contact.getPhone(), contact.getStatus(0).getEvent(), contact.getStatus(0).getDate_long()); // hacer algo con la fecha

            System.out.println("Primera foto " + "phone " + contact.getPhone() + "_0 ts " + contact.getPhoto(0).getDate_long());


            //if(contact.getPhoto(0).getDate_long() != 0 && contact.getPhoto(0).getDate_long() != -1) {
            //crear row en spy_photo (photo_path, photo_ts, numero, id)

            // hacer algo con la fecha



            Boolean exits_photo =  copyPhoto(contact.getPhone(),contact.getPhone() + "_0.png");
            if (exits_photo){
                dataBaseSpy.insertRowTPhotos(contact.getPhone(), contact.getPhone() + "_0", contact.getPhoto(0).getDate_long());

            }

        }

    }


     private boolean copyPhoto(long phone,String photoSpy){

       // final String PREF_ESP = "34";
        final String SUF = "@s.whatsapp.net.j";

        String photoWa = phone + SUF ; // le pongo el long que le quitas los dos primeros ceros del +
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c","cp -r /data/data/com.whatsapp/files/Avatars/"+ photoWa +" /data/data/com.csic.whatsappspy/cache/" + photoSpy});
            p = Runtime.getRuntime().exec(new String[]{"su","-c","chmod -R 777 /data/data/com.csic.whatsappspy/cache/*"});
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    */
}


