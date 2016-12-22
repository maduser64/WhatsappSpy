package com.csic.whatsappspy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by josel on 16/11/2016.
 *
 * Representa un evento de un contacto
 * puede ser o una foto, el nombre del fichero de la foto.
 * puede ser un estado, el estado del contacto
 */
public class Event implements Serializable {


    public static final String TYPE_PHOTO = "photo";
    public static final String TYPE_STATE = "state";

    private String type;
    private long date;
    private String event;


    //Constructores
    public Event(String type, String event, long date) {

        this.type = type;
        this.event = event;
        this.date = date;
    }

    public Event(String type){
        this.type = type;
    }

    //Getters
    public String getType(){
        return type;
    }

    public String getEvent(){
        return this.event;
    }

    public long getDate(){
        return this.date;
    }

    /*
        fecha del evento en formato Date
     */
    public Date date() {

        if (type.equals(Event.TYPE_STATE))
            return new Date(this.date);

        else
            return new Date(this.date * 1000);
    }


    //setters
    public void setType(String type) {

        this.type = type;
    }

    public void setDate(long date_long) {
        this.date = date_long;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }


    /*
        Compara dos eventos por su fecha
     */
    public static Comparator<Event> eventDateComparator = new Comparator<Event>() {

        @Override
        public int compare(Event event1, Event event2) {
            return event1.date().compareTo(event2.date());

        }
    };


}
