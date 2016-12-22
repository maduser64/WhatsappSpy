package com.csic.whatsappspy;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/*
    Activity principal de la aplicacion
 */
public class MainActivity extends AppCompatActivity {


    //Para el manejo de la agenda de contactos
    private PhoneBook contacts;

    //Campos de texto del rango de telefonos a escanear
    private  EditText editTextFrom;
    private EditText editTextTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);

        editTextFrom.setText("0034680260400");
        editTextTo.setText("0034680260500");

        contacts = new PhoneBook(this);

         //Obligamos a la creacion de la base de datos

        DataBaseSpy dataBaseSpy = new DataBaseSpy(getApplicationContext());
        dataBaseSpy.getWritableDatabase();

        dataBaseSpy.close();

        //optenemos permisos superusuario

        try {

            Process p = Runtime.getRuntime().exec(new String[]{"su","-c","su root"});

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*
    ocClick del botos scan
    */
    public void scan(View v){

        startScan();

    }

    /*
        inicializacion del hilo para escanear los contactos
     */
    private void startScan(){



        new Thread(new Runnable() {

            //Manejador para la progressBar de la conexion
            private Handler puenteProgress;
            private ProgressDialog progressDialog;

            private void setupProgressBar(){


                puenteProgress = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {

                        int progreso = (Integer)msg.obj;
                        progressDialog.setProgress(progreso);

                    }
                };

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Conectando ...");
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);

            }

            private void addContacts(){


                long from =  Long.parseLong(editTextFrom.getText().toString());
                long to =  Long.parseLong(editTextTo.getText().toString());

                while(from <= to){

                    //Creamos los contactos, se añade el 00 delante para el prefijo del pais
                    contacts.createContact("00" + from , "00" + from);
                    from ++;

                    Message msg = new Message();
                    msg.obj = 10;
                    puenteProgress.sendMessage(msg);
                }
            }

            @Override
            public void run() {

                setupProgressBar();
                progressDialog.show();
                //Add contacts
                addContacts();

                //Números móviles: comienzan por 6, 71, 72, 73 y 74.

                /*
                    Reset whatsapp APP
                 */
                //ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                //am.restartPackage("com.whatsapp");

                //Esperamos a que el reset se realice completamenet
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Lanzamos whatsapp para poder descargar las fotos de los contactos escaneados
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    if (launchIntent != null)
                        startActivity(launchIntent);

                progressDialog.cancel();
            }
        }).start();

    }


    /*
        onClick del boton viewContact para visualizar los contactos en el rango de telefonos establecido
     */
    public void viewContacts(View v){

        new Thread(new Runnable() {

            @Override
            public void run() {

                //Reseteamos whatsapp para que su base de datos se actualice
                ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                am.restartPackage("com.whatsapp");

                //le damos tiempo a whatsapp para que se restaure y actualice sus bases de datos
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Procesamos las bases de datos
                dbProcessing();

                //Borramos todos los contactos de la agenda
                contacts.removeAllContacts();


                //Mostramos los contactos
                Intent myIntent = new Intent(MainActivity.this, ViewContacts.class);
                myIntent.putExtra("from", editTextFrom.getText().toString());
                myIntent.putExtra("to", editTextTo.getText().toString());

                //Iniciamos la activity donde se muestran los contactos escaneados
                MainActivity.this.startActivity(myIntent);

            }
        }).start();

    }

    /**
     * Copia los contactos de la DB whatsapp a la Spy y sus eventos si es necesario
     */
    public void dbProcessing(){

            //Copiar base de dataos y darle permisos
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"su","-c","cp -r /data/data/com.whatsapp/databases/wa.db /data/data/com.csic.whatsappspy/databases/wa.db"});

                p = Runtime.getRuntime().exec(new String[]{"su","-c","chmod 777 /data/data/com.csic.whatsappspy/databases/wa.db"});

            } catch (IOException e) {
                e.printStackTrace();
            }


            //le damos tiempo para que el sistema pueda copiar la base de datos de una ruta a otra
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        long from =  Long.parseLong(editTextFrom.getText().toString());
        long to =  Long.parseLong(editTextTo.getText().toString());

        DataBaseWa dataBaseWa = new DataBaseWa(getApplicationContext());
        dataBaseWa.getReadableDatabase();

        DataBaseSpy dataBaseSpy = new DataBaseSpy(getApplicationContext());
        dataBaseSpy.getWritableDatabase();

        //Lista de contactos de la base de datos de whatsapp
        ArrayList<Contact> lista = dataBaseWa.readContact(from,to);

        //Guardamos los contactos y sus eventos en nuestra base de datos
        dataBaseSpy.writeContact(lista);

        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        dataBaseSpy.close();
        dataBaseWa.close();
    }
}
