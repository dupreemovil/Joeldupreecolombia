package com.dupreinca.dupree.mh_required_api;

public class  RequiredValida {

    public String nume_iden;

    public String celular;

    public String nombre;


    public RequiredValida(String nume_iden, String celular, String nombre) {
        this.nume_iden = nume_iden;
        this.celular = celular;
        this.nombre = nombre;
    }

    public String getNume_iden() {
        return nume_iden;
    }

    public void setNume_iden(String nume_iden) {
        this.nume_iden = nume_iden;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }




}
