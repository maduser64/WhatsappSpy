package com.csic.whatsappspy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/*
    Controla la vista de los detalles del contacto, se encarga del manejo de la recyclerView para mostrar
    los eventos del contacto ordenados por fecha
 */
public class ViewDetails extends AppCompatActivity {

    private final String PATH_DATA_BASE = "/storage/sdcard0/Download/com.whatsapp/files/Avatars/";
    private RecyclerView eventsList;

    private TextView textName;
    private TextView textPhone;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        events = new ArrayList<>();
        textName = (TextView) findViewById(R.id.TextNumber);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Contact contact = (Contact) b.get("Contact");


           textName.setText(contact.getPhone() + " changes log");

            eventsList = (RecyclerView) findViewById(R.id.DetailsRV);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            eventsList.setLayoutManager(llm);

            initializeAdapter(contact);
        }
    }

    private void initializeAdapter(Contact contact) {

        eventsList.setAdapter(new DetailsAdapter(this, contact.sortEventsByDate()));
    }

}