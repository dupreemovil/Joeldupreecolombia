package com.dupreeinca.lib_api_rest.model.dto.request;

public class ActualizacionSend {

    private String cedula;
    private String correo;
    private String celular;


    public ActualizacionSend(String cedula, String correo, String celular) {
        this.cedula = cedula;
        this.correo = correo;
        this.celular = celular;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }





}
