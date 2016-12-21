package com.csic.whatsappspy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;


public class ViewContacts extends AppCompatActivity {

    private final String PATH_DATA_BASE = "/storage/sdcard0/Download/com.whatsapp/files/Avatars/";
    private RecyclerView contactsList;
    private ArrayList<Contact> contactArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            long from =  Long.parseLong(bundle.getString("from"));
            long to =  Long.parseLong(bundle.getString("to"));

            this.contactArrayList = new ArrayList<Contact>();

            contactsList = (RecyclerView) findViewById(R.id.contactsRV);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            contactsList.setLayoutManager(llm);

            inicializar(from, to);
            initializeAdapter();
        }

    }

    /**
     * OonClick en un contacto de la lista, inicia una nueva actividad de detalles de contacto
     * y le pasa el contacto que se quiere mostras
     */
    public void onClick(Contact contact){

        Intent myIntent = new Intent(ViewContacts.this, ViewDetails.class);
        Bundle b = new Bundle();
        b.putSerializable("Contact",contact);
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    private void initializeAdapter(){

        //ContactAdapter contactAdapter = new ContactAdapter(contactArrayList);
        contactsList.setAdapter(new ContactAdapter(this,contactArrayList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                onClick(contact);
            }
        }));

    }

    /**
     *  Lee los contactos de la base de datos y los mete en la lista
     */
    private void inicializar(long from, long to){

        //int from = b;
        //int to = 680260442;

        ArrayList<Contact> lista;


        DataBaseSpy dataBaseSpy = new DataBaseSpy(getApplicationContext());

        lista = dataBaseSpy.readContact(from,to);

        dataBaseSpy.close();

        this.contactArrayList.addAll(lista);
        /*
        //Creo contactos de prueba para la lista
        Contact c = new Contact("1",1);
        this.contactArrayList.add(c);
        c = new Contact("2",2);
        this.contactArrayList.add(c);
        c = new Contact("3",3);
        this.contactArrayList.add(c);
        c = new Contact("4",4);
        this.contactArrayList.add(c);
        c = new Contact("5",5);
        this.contactArrayList.add(c);
        c = new Contact("6",6);
        this.contactArrayList.add(c);
        c = new Contact("7",7);
        this.contactArrayList.add(c);
        c = new Contact("8",8);
        this.contactArrayList.add(c);
        c = new Contact("9",9);
        this.contactArrayList.add(c);

        */



    }




}
