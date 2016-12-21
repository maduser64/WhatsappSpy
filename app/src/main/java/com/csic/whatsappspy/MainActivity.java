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

public class MainActivity extends AppCompatActivity {

    //Manejador para la progressBar de la conexion
    private Handler puenteProgress;
    private PhoneBook contacts;
    private ProgressDialog progressDialog;
    private  EditText editTextFrom;
    private EditText editTextTo;
    private Thread scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);

        editTextFrom.setText("0034680260400");
        editTextTo.setText("0034680260500");

        contacts = new PhoneBook(this);

        DataBaseSpy dataBaseSpy = new DataBaseSpy(getApplicationContext());
        dataBaseSpy.getWritableDatabase();

        dataBaseSpy.close();

        //aceptar superuser

        try {

            Process p = Runtime.getRuntime().exec(new String[]{"su","-c","su root"});

            Toast.makeText(getApplicationContext(), "Acepta root access", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        setupProgressBar();
        //setup();

        //Crear directorios



    }

    public void setupProgressBar(){


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

    private void setupThread(){


        new Thread(new Runnable() {


            private void addContacts(){


                long from =  Long.parseLong(editTextFrom.getText().toString());
                long to =  Long.parseLong(editTextTo.getText().toString());

                while(from <= to){
                  //  System.out.println(from);
                    contacts.createContact("00" + from , "00" + from);
                    from ++;

                // System.out.println(from);
                    Message msg = new Message();
                    msg.obj = 10;
                    puenteProgress.sendMessage(msg);
                }
            }

            private void deleteContacts(){

                long from =  Long.parseLong(editTextFrom.getText().toString());
                long to =  Long.parseLong(editTextTo.getText().toString());


                while(from <= to){

                    contacts.deleteContact("00" + from , "00" + from);
                    from ++;

                    Message msg = new Message();
                    msg.obj = 10;
                    puenteProgress.sendMessage(msg);
                }
            }


            @Override
            public void run() {


                //Delete everything before?
                //Add contacts
                addContacts();
                //deleteContacts();

                //Números móviles: comienzan por 6, 71, 72, 73 y 74.


                /*
                    Reset whatsapp APP
                 */

                ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                am.restartPackage("com.whatsapp");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    if (launchIntent != null) {startActivity(launchIntent);//null pointer check in case package name was not found
                }
                //Toast.makeText(getApplicationContext(), "Debes hacer scroll para cargar las fotos", Toast.LENGTH_LONG).show();

                //Wait
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                progressDialog.cancel();

            }
        }).start();

    }

    public void viewContacts(View v){

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

        new Thread(new Runnable() {

            @Override
            public void run() {

              //  progressDialog.show();
                ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                am.restartPackage("com.whatsapp");

                //Wait
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Process the data base (se copian los avatars)
                dbProcessing();

                //Wait
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Delete all contacts
                contacts.removeAllContacts();

                //View contacts data

                //Pasarle los parametros from, to para mostrar solo esos contactos??????
                Intent myIntent = new Intent(MainActivity.this, ViewContacts.class);
                myIntent.putExtra("from", editTextFrom.getText().toString());
                myIntent.putExtra("to", editTextTo.getText().toString());
           //     progressDialog.cancel();
                MainActivity.this.startActivity(myIntent);

            }
        }).start();

    }
    public void scan(View v){

        progressDialog.show();
        setupThread();

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


            //Wait
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

        ArrayList<Contact> lista;


        lista = dataBaseWa.readContact(from,to);
        dataBaseSpy.writeContact(lista);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dataBaseSpy.close();
        dataBaseWa.close();
    }
}
