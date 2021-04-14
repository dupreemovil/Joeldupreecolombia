package com.dupreeinca.lib_api_rest.model.dto.request;

public class SuscripcionSend {

    private String cedula;
    private String respuesta;


    public SuscripcionSend(String cedula, String respuesta) {
        this.cedula = cedula;
        this.respuesta = respuesta;
    }


    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }




}
