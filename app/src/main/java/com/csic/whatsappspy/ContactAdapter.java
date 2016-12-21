package com.csic.whatsappspy;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Adaptador para controlar el recyclerView, rellenarlo, mostrarlo y lanzar el evento de click
 * sobre un componente
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    //Contact list
    private ArrayList<Contact> contacts;
    private OnItemClickListener listener;
    //private final String PATH_DATA_BASE = "/mnt/sdcard/Download/com.whatsapp/files/Avatars/";
    private String PATH_DATA_BASE = "/data/data/com.csic.whatsappspy/cache/";


    public ContactAdapter(Context context,ArrayList<Contact> contacts,OnItemClickListener listener){
        this.contacts = contacts;
        this.listener = listener;

        //PATH_DATA_BASE = context.getFilesDir() + "/" + "files/Avatars/";
    }
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacto,parent,false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {
        Contact contact = contacts.get(position);
        contactViewHolder.bind(contact,listener);

        if(contact.getLastPhoto() != null) {
            contactViewHolder.photo.setImageBitmap(BitmapFactory.decodeFile(PATH_DATA_BASE + contact.getLastPhoto().getEvent() + ".png"));
            File f = new File(PATH_DATA_BASE + contact.getLastPhoto().getEvent() + ".png");
            Log.i("info", PATH_DATA_BASE + contact.getLastPhoto().getEvent() + ".png " + f.exists());
        }
        else{
            contactViewHolder.photo.setImageResource(R.drawable.avatar_wa);
        }

        contactViewHolder.name.setText(contact.getName());
//        contactViewHolder.phone.setText(contact.getPhone() + "");
    }


    /**
     * Num of elements
     * @return
     */
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        private ImageView photo;
        private TextView name;
        private TextView phone;

        public ContactViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.imgPhotoCV);
            name = (TextView) itemView.findViewById(R.id.textViewNombreCV);
           // phone =(TextView) itemView.findViewById(R.id.textViewTelefonoCV);

        }

        public void bind(final Contact item, final OnItemClickListener listener) {
            name.setText(item.getName());
            //Picasso.with(itemView.getContext()).load(item.imageUrl).into(image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }


    /**
     * Listener
     */

    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }
}
