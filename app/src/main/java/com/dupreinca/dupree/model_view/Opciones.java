package com.dupreinca.dupree.model_view;

import androidx.annotation.StringDef;

public class Opciones {

    public Opciones(String id,String opcion) {
        this.id = id;
        this.opcion = opcion;
    }

    String id;
    String opcion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }





}
