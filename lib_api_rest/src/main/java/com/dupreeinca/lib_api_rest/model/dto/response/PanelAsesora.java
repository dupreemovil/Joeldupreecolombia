package com.dupreeinca.lib_api_rest.model.dto.response;

import com.dupreeinca.lib_api_rest.model.dto.response.Faltante;
import com.dupreeinca.lib_api_rest.model.dto.response.Tracking;

import java.util.List;

/**
 * Created by cloudemotion on 1/9/17.
 */

public class PanelAsesora {
    private String nombre_asesora;
    private String cantidad_mensajes;
    private String saldo;
    private String cupo_credito;
    private String mensaje_general;
    private List<Tracking> tracking;
    private List<Faltante> faltantes;
    private String campana;

    private String activa_encuesta;

    private String activa_actualizacion;

    public String getNombre_asesora() {
        return nombre_asesora;
    }

    public String getSaldo() {
        return saldo;
    }

    public String getCupo_credito() {
        return cupo_credito;
    }

    public String getCampana() {
        return campana;
    }

    public String getMensaje_general() {
        return mensaje_general;
    }

    public List<Tracking> getTracking() {
        return tracking;
    }

    public List<Faltante> getFaltantes() {
        return faltantes;
    }

    public String getCantidad_mensajes() {
        return cantidad_mensajes;
    }

    public String getActiva_encuesta() {
        return activa_encuesta;
    }

    public void setActiva_encuesta(String activa_encuesta) {
        this.activa_encuesta = activa_encuesta;
    }
    public String getActiva_actualizacion() {
        return activa_actualizacion;
    }

    public void setActiva_actualizacion(String activa_actualizacion) {
        this.activa_actualizacion = activa_actualizacion;
    }

}
