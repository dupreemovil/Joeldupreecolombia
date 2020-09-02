package com.dupreinca.dupree.mh_required_api;

public class RequiredVisit{


    private String username;
    private String tiempo;
    private String opcion_menu;


    public RequiredVisit(String username, String tiempo, String opcion_menu) {
        this.username = username;
        this.tiempo = tiempo;
        this.opcion_menu = opcion_menu;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getOpcion_menu() {
        return opcion_menu;
    }

    public void setOpcion_menu(String opcion_menu) {
        this.opcion_menu = opcion_menu;
    }





}
