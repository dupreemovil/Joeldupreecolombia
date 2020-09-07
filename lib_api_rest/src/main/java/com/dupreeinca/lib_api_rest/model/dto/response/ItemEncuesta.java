package com.dupreeinca.lib_api_rest.model.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ItemEncuesta {


    private List<ItemOpcion> opciones;
    private String tipo_pregunta;
    private String descripcionpregunta;
    private String idpregunta;

    public List<ItemOpcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<ItemOpcion> opciones) {
        this.opciones = opciones;
    }

    public String getTipo_pregunta() {
        return tipo_pregunta;
    }

    public void setTipo_pregunta(String tipo_pregunta) {
        this.tipo_pregunta = tipo_pregunta;
    }

    public String getDescripcionpregunta() {
        return descripcionpregunta;
    }

    public void setDescripcionpregunta(String descripcionpregunta) {
        this.descripcionpregunta = descripcionpregunta;
    }

    public String getIdpregunta() {
        return idpregunta;
    }

    public void setIdpregunta(String idpregunta) {
        this.idpregunta = idpregunta;
    }



}