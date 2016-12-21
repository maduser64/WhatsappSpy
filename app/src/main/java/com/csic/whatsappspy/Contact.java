package com.csic.whatsappspy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by josel on 16/11/2016.
 */
public class Contact implements Serializable {


    private String name;
    private long phone;
    private ArrayList<Event> photos;
    private ArrayList<Event> status;


    public Contact(String name, long phone){ // crea el contacto pero deja los arrays nulos
        this.name = name;
        this.phone = phone;

        photos = new ArrayList<Event>();
        status = new ArrayList<Event>();
    }

    //getters
    public String getName() {
        return name;
    }

    public long getPhone() {
        return phone;
    }


    //getters por index del array de eventos


    public Event getPhoto(int numPhoto){

        if(numPhoto < photos.size() && numPhoto >= 0)
            return photos.get(numPhoto);
        else
            return null;
    }

    public Event getLastPhoto (){

        if(photos.size() > 0)
            return photos.get(photos.size() - 1);
        else
            return null;
    }


    public Event getStatus(int numState){

        if(numState < status.size() && numState >= 0)
            return status.get(numState); //meter el indice que quieras
        else
            return null;
    }

    public Event getLastStatus (){

        if(status.size() > 0)
            return status.get(status.size() -1);
        else
            return null;
    }

    public ArrayList<Event> getPhotos() {
        return photos;
    }

    public ArrayList<Event> getStatus() {
        return status;
    }


    //counters
    public int getNumPhotos(){
        return  photos.size();
    }
    public int getNumStatus(){
        return status.size();
    }


    //setters de name y phone
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(long phone) {
        this.phone = phone;
    }


    //adders
    public void addPhoto(String url, long date){
        photos.add(new Event(Event.TYPE_PHOTO,url,date));

    }

    public void addStatus(String status, long date){
        this.status.add(new Event(Event.TYPE_STATE,status,date));
    }

    public void addPhoto(Event event){
        photos.add(event);
    }

    public void addStatus(Event event){
        status.add(event);
    }


    public ArrayList<Event> sortEventsByDate( ArrayList<Event> events ){

        Collections.sort(events, Event.eventDateComparator );

        return events;
    }

}
