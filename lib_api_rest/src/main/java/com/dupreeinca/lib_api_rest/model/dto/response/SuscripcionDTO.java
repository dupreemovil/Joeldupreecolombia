package com.dupreeinca.lib_api_rest.model.dto.response;

import java.util.List;

public class SuscripcionDTO {

    private String status;
    private boolean valid;
    private String result;
    private String imagen_suscripcion;
    private int pedido;
    private int panel_principal;
    private int code;


    private String url_suscribe;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public int getPanel_principal() {
        return panel_principal;
    }

    public void setPanel_principal(int panel_principal) {
        this.panel_principal = panel_principal;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public String getImagen_suscripcion() {
        return imagen_suscripcion;
    }

    public void setImagen_suscripcion(String imagen_suscripcion) {
        this.imagen_suscripcion = imagen_suscripcion;
    }
    public String getUrl_suscribe() {
        return url_suscribe;
    }

    public void setUrl_suscribe(String url_suscribe) {
        this.url_suscribe = url_suscribe;
    }


}
