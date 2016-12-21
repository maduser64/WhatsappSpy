package com.csic.whatsappspy;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by martam on 01/12/2016.
 */
public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>{

    //Contact list
    private ArrayList<Event> events;

    //private final String PATH_DATA_BASE = "/mnt/sdcard/Download/com.whatsapp/files/Avatars/";
    private String PATH_DATA_BASE = "/data/data/com.csic.whatsappspy/cache/";


    public DetailsAdapter(Context context, ArrayList<Event> events){
        this.events = events;


        //PATH_DATA_BASE = context.getFilesDir() + "/" + "files/Avatars/";
    }
    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_details,parent,false);
        return new DetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder detailsViewHolder, int position) {

        Event event = events.get(position);


            File f = new File(PATH_DATA_BASE + event.getEvent() + ".png");
            System.out.println(PATH_DATA_BASE +event.getEvent() + ".png " + f.exists());

        if (f.exists()) {
            detailsViewHolder.photo.setImageBitmap(BitmapFactory.decodeFile(PATH_DATA_BASE + event.getEvent() + ".png"));

        }

       else {
            detailsViewHolder.status.setText(event.getEvent());
        }

        if (event.date() != null && event.getDate() != 0 && event.getDate() != -1)
        detailsViewHolder.time_stamp.setText(event.date() + "");

        else{
            detailsViewHolder.time_stamp.setText("Unknown"); // a veces la base de datos se copia mal y hay foto pero su ts es null, aun asi me parece importante reflejarlo. Lo controlamos antes...
        }
    }


    /**
     * Num of elements
     * @return
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder{

        private ImageView photo;
        private TextView status;
        private TextView time_stamp;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.imgPhoto);
            status = (TextView) itemView.findViewById(R.id.textViewStatus);
            time_stamp =(TextView) itemView.findViewById(R.id.textViewTM);

        }

    }


}


